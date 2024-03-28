// IN2011 Computer Networks
// Coursework 2023/2024
//
// Submission by
// Rideone Nahian Islam
// 220057967
// rideone.islam@city.ac.uk


import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// DO NOT EDIT starts
interface FullNodeInterface {
    public boolean listen(String ipAddress, int portNumber);
    public void handleIncomingConnections(String startingNodeName, String startingNodeAddress) throws Exception;
}
// DO NOT EDIT ends


public class FullNode implements FullNodeInterface {
    ServerSocket socket;
    String ipAddress;
    int port;
    Socket client;
    private BufferedReader reader;
    private Writer writer;
    private String res;
    private String startingNodeName;
    private final HashMap<String, String> table;
    private final HashMap<Integer, ArrayList<NodeContainer>> networkMap;

    public FullNode() {
        table = new HashMap<>();
        networkMap = new HashMap<>();
    }

    public boolean listen(String ipAddress, int portNumber) {
	// Implement this!
	// Return true if the node can accept incoming connections
	// Return false otherwise
        try{
            socket = new ServerSocket(portNumber);
            client = socket.accept();
            this.ipAddress = ipAddress;
            this.port = portNumber;

            initialise();

            return true;

        } catch (IOException e) {
            System.err.println(e);
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void handleIncomingConnections(String startingNodeName, String startingNodeAddress){
	// Implement this!
        this.startingNodeName = startingNodeName;

        //networkMap.put(startingNodeName, startingNodeAddress);

        try {
            updateNetworkMap(startingNodeName, startingNodeAddress);
            while(true) {
                if(client.isClosed()){
                    client = socket.accept();
                    initialise();

                }
                if(!client.isClosed() && reader.ready()) {
                    res = reader.readLine();
                    String[] checkRes = res.split(" ");

                    switch (checkRes[0]) {
                        case "START":
                            startHandle();
                            break;

                        case "ECHO?":
                            echoHandle();
                            break;

                        case "PUT?": //Do FAILED after
                            putHandle();
                            break;

                        case "GET?":
                            getHandle();
                            break;

                        case "NOTIFY?":
                            notifyHandle();
                            break;

                        case "NEAREST?":
                            nearestHandle();
                            break;

                        default:
                            writer.write("END INVALID REQUEST");
                            writer.flush();
                            System.out.println("INVALID REQUEST");
                            client.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void initialise(){
        try {
            writer = new OutputStreamWriter(client.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void startHandle() throws IOException {
        System.out.println("Connected to Temporary Node!\n");
        writer.write("START 1 " + startingNodeName + "\n");
        writer.flush();
    }

    private void echoHandle() throws IOException {
        System.out.println(res);
        writer.write("OHCE\n");
        writer.flush();
    }

    private void putHandle() throws IOException {
        String[] put = res.split(" ");

        System.out.println(res);

        int keys = Integer.parseInt(put[1]);
        int values = Integer.parseInt(put[2]);

        // Read the key and value
        String key = "";
        String value = "";
        for (int i = 0; i < keys; i++) {
            key += reader.readLine() + "\n";
        }
        for (int i = 0; i < values; i++) {
            value += reader.readLine() + "\n";
        }

        // Determine if this node should store the key-value pair

        table.put(key, value);
        writer.write("SUCCESS\n");

        System.out.println(table);
        writer.flush();
        client.close();
    }

    private void getHandle() throws IOException{
        res = reader.readLine();
        System.out.println(res);
        int keyLines = Integer.parseInt(res);
        String key = "";
        for (int i = 0; i < keyLines; i++) {
            key += reader.readLine() + "\n";
        }

        if (table.containsKey(key)) { // Replace 'table' with your data structure
            writer.write("VALUE\n");
            String value = table.get(key);
            writer.write(TemporaryNode.countSub(value, "\n") + "\n"); // Count lines in the value
            writer.write(value);
        } else {
            writer.write("NOPE\n");
        }
        writer.flush();
    }

    private void notifyHandle(){
        try {
            // Read node name and address
            String nodeName = reader.readLine();
            String nodeAddress = reader.readLine();

            updateNetworkMap(nodeName + "\n", nodeAddress + "\n");

            // Respond with "NOTIFIED"
            writer.write("NOTIFIED\n");
            writer.flush();

        } catch (IOException e) {
            System.err.println("Error during NOTIFY handling: " + e.getMessage());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateNetworkMap(String name, String addr) throws Exception {
        NodeContainer newNode = new NodeContainer(name, addr);
        byte[] h1 = HashID.computeHashID(startingNodeName);
        byte[] h2 = HashID.computeHashID(name);

        Integer dist = HashID.calculateDistance(h1, h2);

        if (networkMap.containsKey(dist)){
            ArrayList<NodeContainer> container = networkMap.get(dist);
            // Add the new node if there are less than 3 nodes with the same distance
            if (container.size() < 3){
                container.add(newNode);
            }else{
                container.remove(0);
                container.add(newNode);
            }
            networkMap.put(dist, container);
        }else{
            // Add new node container if it doesn't exist already to a given distance
            ArrayList<NodeContainer> nc = new ArrayList<>();
            nc.add(newNode);
            networkMap.put(dist, nc);
        }
    }

    private void nearestHandle() throws Exception {
        String hexRequest = res.split(" ")[1];
        int distance = HashID.calculateDistance(HashID.hexStringToByteArray(hexRequest), HashID.computeHashID(startingNodeName));
        List<NodeContainer> container = new ArrayList<>();
        String message = "";

        for (int i = 0; i >= 0 && container.size() < 3; i++){
            List<NodeContainer> tempContainer = networkMap.get(distance - i);

            for(NodeContainer node: tempContainer){
                container.add(node);
                if(container.size() == 3){
                    break;
                }
            }
        }

        for(NodeContainer node: container){
            message += node.getNodeName() +"\n" + node.getAddress() + "\n";
        }

        writer.write("NODES " + container.size() + "\n" + message);
        writer.flush();
    }
}

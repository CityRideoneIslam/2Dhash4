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
import java.util.HashMap;

// DO NOT EDIT starts
interface FullNodeInterface {
    public boolean listen(String ipAddress, int portNumber);
    public void handleIncomingConnections(String startingNodeName, String startingNodeAddress);
}
// DO NOT EDIT ends


public class FullNode implements FullNodeInterface {
    ServerSocket socket;

    Socket client;
    private BufferedReader reader;
    private Writer writer;
    private String res;
    private HashMap<String, String> table;

    public boolean listen(String ipAddress, int portNumber) {
	// Implement this!
	// Return true if the node can accept incoming connections
	// Return false otherwise
        try{
            InetAddress host = InetAddress.getByName(ipAddress);
            socket = new ServerSocket(portNumber);
            client = socket.accept();
            writer = new OutputStreamWriter(client.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            table = new HashMap<>();

            return true;

        } catch (IOException e) {
            System.err.println(e);
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void handleIncomingConnections(String startingNodeName, String startingNodeAddress) {
	// Implement this!
        try {
            res = reader.readLine();

            if (res.startsWith("START")) {
                System.out.println("Connected to Temporary Node!\n");
                writer.write("START 1 " + startingNodeName + "\n");
                writer.flush();
            }

            res = reader.readLine();

            if (res.startsWith("PUT?")) {
                String[] put = res.split(" ");

                int keys = Integer.parseInt(put[1]);
                int values = Integer.parseInt(put[2]);

                String key = "";
                String value = "";

                for (int i = 0; i < keys; i++){
                    res = reader.readLine();
                    key += res;
                    System.out.println(res);
                }

                for (int i = 0; i < values; i++){
                    res = reader.readLine();
                    value += res;
                    System.out.println(res);
                }
                table.put(key, value);
                System.out.println(table);
                writer.write("SUCCESS\n");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return;
    }
}

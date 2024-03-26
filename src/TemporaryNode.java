// IN2011 Computer Networks
// Coursework 2023/2024
//
// Submission by
// Rideone Nahian Islam
// 220057967
// rideone.islam@city.ac.uk


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

// DO NOT EDIT starts
interface TemporaryNodeInterface {
    public boolean start(String startingNodeName, String startingNodeAddress) throws IOException;
    public boolean store(String key, String value);
    public String get(String key);
}
// DO NOT EDIT ends


public class TemporaryNode implements TemporaryNodeInterface {
    private String startingNodeName;
    private String startingNodeAddress;
    private Socket socket; // Store the socket for communication
    private Writer writer;
    private BufferedReader reader;
    private String res;

    public boolean start(String startingNodeName, String startingNodeAddress) {
        try {
            this.startingNodeName = startingNodeName;
            this.startingNodeAddress = startingNodeAddress;
            // Establish connection to starting node
            String[] ip_address_port = startingNodeAddress.split(":");
            String ip_address = ip_address_port[0];
            InetAddress host = InetAddress.getByName(ip_address);
            int port = Integer.parseInt(ip_address_port[1]);
            socket = new Socket(host, port);

            // Send START message
            writer = new OutputStreamWriter(socket.getOutputStream());
            writer.write("START 1 " + startingNodeName + "\n");
            writer.flush();

            // Read START message from the starting node (for confirmation)
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            res = reader.readLine();
            if (res.startsWith("START")) {
                System.out.println("Connected to full node!\n");
                return true;
            }

        } catch (IOException e) {
            System.err.println(e);
            closeSocket(); // Close the socket if any error occurs
        }
        return false;
    }

    public boolean store(String key, String value) {
        if (!echo()) {
            System.err.println("ECHO failed. Connection or Full Node might have an issue.");
            return false; // Or take corrective action
        }

        try {
            int nKey = countSub(key, "\n");
            int nValue = countSub(value, "\n");
            writer.write("PUT? " + nKey + " " + nValue + "\n" + key + value);
            writer.flush();

            res = reader.readLine();
            System.out.println(res);
            if(res.equals("SUCCESS")){
                closeSocket();
                return true;
            }
            else if (res.equals("FAILURE")){
                return false;
            }
            else{
                System.err.println("Unexpected response from full node");
                writer.write("END Connection issue\n"); // Specify a reason if possible
                writer.flush();
                closeSocket(); // Ensure socket is closed
                return false;}

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e);
            closeSocket();
            return false;
        }
    }

    public String get(String key) {
        try {
            writer.write("GET?\n" + key + "\n");
            writer.flush();

            String response = reader.readLine();
            if (response.equals("VALUE")) {
                // Read and return the value
                int valueLines = Integer.parseInt(response.split(" ")[1]);
                String value = "";
                for (int i = 0; i < valueLines; i++) {
                    value += reader.readLine() + "\n";
                }
                return value; // Return the retrieved value

            } else if (response.equals("NOPE")) {
                System.out.println("Key not found.");
                return null; // Indicate that the key was not found
            } else {
                System.err.println("Unexpected response from full node: " + response);
                return null;
            }

        } catch (IOException e) {
            System.err.println("Error during GET operation: " + e.getMessage());
            return null;
        }
    }

    private void closeSocket() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            // Ignore any errors during socket closure
        }
    }

    private boolean echo() {
        try {
            writer.write("ECHO?\n");
            writer.flush();

            res = reader.readLine();
            System.out.println(res);

            if (res != null && res.equals("OHCE")) {
                return true; // Connection alive and responder working
            } else {
                return false; // Potential connection or responder issue
            }

        } catch (IOException e) {
            System.err.println("Error during ECHO exchange: " + e.getMessage());
            return false;
        }
    }

    public static int countSub(String s, String sub) {
        int n = 0;
        int index = 0;

        while ((index = s.indexOf(sub, index)) != -1) {
            n++;
            index += sub.length(); // Move the search index ahead by the substring length
        }

        return n;
    }
}
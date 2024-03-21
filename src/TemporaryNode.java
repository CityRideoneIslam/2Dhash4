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
    private Socket socket; // Store the socket for communication
    private Writer writer;
    private BufferedReader reader;
    private String res;

    public boolean start(String startingNodeName, String startingNodeAddress) {
        try {
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
        try {
            int nKey = countSub(key, "\n");
            int nValue = countSub(value, "\n");
            writer.write("PUT? " + nKey + " " + nValue + "\n" + key + value);
            writer.flush();

            res = reader.readLine();
            System.out.println(res);
            return res.equals("SUCCESS");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e);
            closeSocket();
            return false;
        }
    }

    public String get(String key) {
        try {
            Writer writer = new OutputStreamWriter(socket.getOutputStream());
            writer.write("GET? 1\n" + key + "\n");
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = reader.readLine();
            if (response.startsWith("VALUE")) {
                int numLines = Integer.parseInt(response.substring(6));
                StringBuilder valueBuilder = new StringBuilder();
                for (int i = 0; i < numLines; i++) {
                    valueBuilder.append(reader.readLine());
                }
                return valueBuilder.toString();
            } else {
                return null;
            }
        } catch (IOException e) {
            closeSocket();
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

    private int countSub(String s, String sub) {
        int n = 0;
        int index = 0;

        while ((index = s.indexOf(sub, index)) != -1) {
            n++;
            index += sub.length(); // Move the search index ahead by the substring length
        }

        return n;
    }
}
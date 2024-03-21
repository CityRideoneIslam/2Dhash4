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

    public boolean listen(String ipAddress, int portNumber) {
	// Implement this!
	// Return true if the node can accept incoming connections
	// Return false otherwise
        try{
            InetAddress host = InetAddress.getByName(ipAddress);
            socket = new ServerSocket(portNumber);
            client = socket.accept();

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
            writer = new OutputStreamWriter(client.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String res = reader.readLine();

            if (res.startsWith("START")) {
                System.out.println("Connected to Temporary Node!\n");
                writer.write("START 1 " + startingNodeName + "\n");
                writer.flush();
            }

            if (res.startsWith("PUT")) {

                writer.write("SUCCESS");
                writer.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return;
    }
}

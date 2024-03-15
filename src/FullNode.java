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
import java.net.UnknownHostException;

// DO NOT EDIT starts
interface FullNodeInterface {
    public boolean listen(String ipAddress, int portNumber);
    public void handleIncomingConnections(String startingNodeName, String startingNodeAddress);
}
// DO NOT EDIT ends


public class FullNode implements FullNodeInterface {

    public boolean listen(String ipAddress, int portNumber) {
	// Implement this!
	// Return true if the node can accept incoming connections
	// Return false otherwise
        try{
            InetAddress host = InetAddress.getByName(ipAddress);
            ServerSocket socket = new ServerSocket(portNumber);

            Socket clientSocket = socket.accept();

            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            Writer writer = new OutputStreamWriter(clientSocket.getOutputStream());

            clientSocket.close();

            return true;

        } catch (IOException e) {
            return false;
        }

    }
    
    public void handleIncomingConnections(String startingNodeName, String startingNodeAddress) {
	// Implement this!
	return;
    }
}

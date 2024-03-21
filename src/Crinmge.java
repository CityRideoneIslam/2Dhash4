public class Crinmge {

// IN2011 Computer Networks
// Coursework 2023/2024
//
// This is an example of how the FullNode object can be used.
// It should work with your submission without any changes.
// This should make your testing easier.

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage error!");
            System.err.println("DSTStoreCmdLine startingNodeName startingNodeAddress ipAddress portNumber");
            return;
        } else {


            // These give the IP Address and port for other nodes to contact this one
            String ipAddress = args[0];
            int portNumber;
            try {
                portNumber = Integer.parseInt(args[1]);
            } catch (Exception e) {
                System.err.println("Exception parsing the port number");
                System.err.println(e);
                return;
            }


            // Use a FullNode object to be a full participant in the 2D#4 network
            FullNode fn = new FullNode();

            // Full nodes need to be able to accept incoming connections
            if (fn.listen(ipAddress, portNumber)) {
                // Become part of the network
                //fn.handleIncomingConnections(startingNodeName, startingNodeAddress);

            } else {
                System.err.println("Could not listen for incoming connections");
            }

            return;
        }
    }
}


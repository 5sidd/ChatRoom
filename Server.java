import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    public static void main(String[] args) throws Exception {
        // Get host name and port number to start server from command line arguments
        String hostName = null;
        int portNumber = -1;
        if (args.length == 2) {
            try {
                hostName = args[0];
                portNumber = Integer.parseInt(args[1]);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }
        else {
            System.out.println("Error: Please provide server's hostname and port number");
            System.exit(-1);
        }

        // Create server socket
        // Functionality to set up ability for client to read/write from clients
        InetAddress hostAddress = InetAddress.getByName(hostName);
        ServerSocket server = new ServerSocket(portNumber, 50, hostAddress);
        String serverHost = server.getInetAddress().getHostName();
        System.out.println("Server is listening on " + serverHost + " at port " + server.getLocalPort());

        // Usernames storage
        HashMap<String, HandleRequest> users = new HashMap<String, HandleRequest>();

        // Start listening for incoming connections
        while (true) {
            Socket connectionSocket = server.accept(); // socket for each client
            System.out.println(connectionSocket.getInetAddress());
            // Create a new thread for each client connection
            HandleRequest requestHandler = new HandleRequest(connectionSocket, users);
            Thread thread = new Thread(requestHandler);
            thread.start();
        }
    }
}

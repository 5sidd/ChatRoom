import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws Exception {
        // Get the host name and port number of the server to connect to from command line arguments
        // Port number 0 means that the program will find an available port for you
        String hostName = null;
        int portNumber = -1;
        if (args.length == 2) {
            hostName = args[0];
            portNumber = Integer.parseInt(args[1]);
        }
        else {
            System.out.println("Error: Please provide server's hostname and port number");
            System.exit(-1);
        }

        // Variables that store client commands, server responses, and the registered username
        String clientRequest;
        String serverResponse;
        String userName = null;
        
        // Create client socket
        // Functionality to set up ability for client to read/write from server 
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = new Socket(hostName, portNumber);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        // Register a valid username
        while (true) {
            System.out.print("Enter username: ");
            clientRequest = "REG " + inFromUser.readLine();
            String[] clientRequestParts = clientRequest.split(" ");
            if (clientRequestParts.length >= 2) {
                userName = clientRequestParts[1];
            }
            outToServer.writeBytes(clientRequest + "\n");
            boolean toBreak = false;
            serverResponse = inFromServer.readLine();
            String[] serverResponseParts = serverResponse.split(" ");
            if (serverResponseParts.length > 0 && serverResponseParts[0].equals("ACK")) {
                toBreak = true;
            }
            System.out.println(serverResponse);
            if (toBreak) {
                break;
            }
        }

        // Create a separate thread for reading incoming server messages
        Thread readServerMessages = new Thread(() -> {
            try {
                String serverMessage;
                while ((serverMessage = inFromServer.readLine()) != null) {
                    System.out.println("\n" + serverMessage);
                    System.out.print("Enter request: ");
                }
                // Close client socket
                clientSocket.close();
                System.exit(1);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        readServerMessages.start();

        // Once the username is registered, user can enter requests
        System.out.print("Enter request: ");
        while (true) {
            clientRequest = inFromUser.readLine();
            outToServer.writeBytes(clientRequest + "\n");
            String[] requestParts = clientRequest.split(" ");
            if (requestParts[0].equals("MESG") || requestParts[0].equals("PMSG")) {
                System.out.print("Enter request: ");
            }
        }
    }
}

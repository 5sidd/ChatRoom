import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// Class that handles client requests for each connected client
final class HandleRequest implements Runnable {
    private Socket socket; // client socket
    private String userName; // registered username of client (initially null)
    private HashMap<String, HandleRequest> users; // list of all other registered users
    private DataOutputStream outToClient; // socket output stream to send responses to client

    public HandleRequest(Socket socket, HashMap<String, HandleRequest> users) throws Exception {
        this.socket = socket;
        this.userName = null;
        this.users = users;
        this.outToClient = new DataOutputStream(this.socket.getOutputStream());
    }

    // Implement run() method of Runnable interface
    public void run() {
        try {
            processRequest();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Process incoming request
    private void processRequest() throws Exception {
        String clientRequest;
        InputStream is = this.socket.getInputStream();
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(is));
        while ((clientRequest = inFromClient.readLine()) != null) {
            System.out.println(clientRequest);
            requestProcessor(clientRequest);
        }
    }

    // Breaks up the request received from the client
    // Processes it to make sure it is a valid request
    // If valid, it will pass off the remaining processing to the appropriate helper function
    private void requestProcessor(String request) throws Exception {
        String[] requestParts = request.split(" ");
        String requestType = "";
        if (requestParts.length < 2) {
            String response = "ERR 4: Unknown message format" + "\n";
            this.sendMessage(response);
        }
        else {
            requestType = requestParts[0];
        }

        if (requestType.equals("REG")) {
            this.handleREG(requestParts);
        }
        else if (requestType.equals("MESG")) {
            this.handleMESG(requestParts);
        }
        else if (requestType.equals("PMSG")) {
            this.handlePMSG(requestParts);
        }
        else if (requestType.equals("EXIT")) {
            this.handleEXIT(requestParts);
        }
        else {
            String response = "ERR 4: Unknown message format" + "\n";
            this.sendMessage(response);
        }
    }

    // Handles and processes REG requests
    // Checks if the username being registered is valid or not
    private void handleREG(String[] requestParts) throws Exception {
        String potentialUserName = requestParts[1];
        if (requestParts.length != 2) {
            String response = "ERR 2: Username contains spaces" + "\n";
            this.sendMessage(response);
        }
        else if (potentialUserName.length() > 32) {
            String response = "ERR 1: Username too long" + "\n";
            this.sendMessage(response);
        }
        else if (this.users.containsKey(potentialUserName)) {
            String response = "ERR 0: Username taken" + "\n";
            this.sendMessage(response);
        }
        else {
            // Username is valid so register this user
            // Since shared hashmap is being updated, enforce synchronization
            synchronized (this.users) {
                this.userName = potentialUserName;
                this.users.put(this.userName, this);
                int numberOfUsers = this.users.size();
                String allUsers = String.join(", ", this.users.keySet());
                String response = "ACK " + Integer.toString(numberOfUsers) + " " + allUsers + "\n";
                this.sendMessage(response);
                // Inform other users in chat room that this new user has joined
                String bMessage = "MSG SERVER " + this.userName + " has joined chat" + "\n";
                this.broadCastMessage(bMessage, this.userName);
            }
        }
    }

    // Handles and processes MESG requests
    // Check if user is registered and request is in the correct format
    // Send a broadcast message to all registered users in chat room except this user
    private void handleMESG(String[] requestParts) throws Exception {
        if (this.userName == null) {
            String response = "ERR 4: Unknown message format" + "\n";
            this.sendMessage(response);
        }
        else {
            String message = String.join(" ", Arrays.copyOfRange(requestParts, 1, requestParts.length));
            String response = "MSG " + this.userName + " " + message + "\n";
            this.broadCastMessage(response, this.userName);
        }
    }

    // Handles and processes PMSG requests
    // Check if user is registered, if request is in the correct format, and if target user exists
    // Send private message to target user
    private void handlePMSG(String[] requestParts) throws  Exception {
        if (this.userName == null || requestParts.length < 3) {
            String response = "ERR 4: Unknown message format" + "\n";
            this.sendMessage(response);
        }
        else {
            String targetUser = requestParts[1];
            if (!this.users.containsKey(targetUser)) {
                String response = "ERR 3: Unknown user for private message" + "\n";
                this.sendMessage(response);
            }
            else {
                String message = String.join(" ", Arrays.copyOfRange(requestParts, 2, requestParts.length));
                String response = "MSG " + this.userName + " " + message + "\n";
                HandleRequest targetUserHandler = this.users.get(targetUser);
                targetUserHandler.sendMessage(response);
            }
        }
    }

    // Handles and processes EXIT request
    // Checks if user is registered, if request is in the correct format, and if target user is the same as this user
    // Deregister user and close the socket connection
    private void handleEXIT(String[] requestParts) throws Exception {
        if (this.userName == null || requestParts.length != 2) {
            String response = "ERR 4: Unknown message format" + "\n";
            this.sendMessage(response);
        }
        else {
            String targetUser = requestParts[1];
            if (!targetUser.equals(this.userName)) {
                String response = "ERR 4: Unknown message format" + "\n";
                this.sendMessage(response);
            }
            else {
                synchronized (this.users) {
                    // Deregister user
                    this.users.remove(this.userName);
                    int numberOfUsers = this.users.size();
                    String allUsers = String.join(", ", this.users.keySet());
                    String response = "ACK " + Integer.toString(numberOfUsers) + " " + allUsers + "\n";
                    this.sendMessage(response);
                    // Inform other users in chat room that this user has left
                    String bMessage = "MSG SERVER " + this.userName + " has left chat" + "\n";
                    this.broadCastMessage(bMessage, null);
                    // Close connection
                    this.socket.close();
                }
            }
        }
    }

    // Broadcast a message to all registered users
    // Option to skip a specified user
    private void broadCastMessage(String message, String toSkip) throws Exception {
        for (Map.Entry<String, HandleRequest> item : this.users.entrySet()) {
            String userNameKey = item.getKey();
            HandleRequest handlerValue = item.getValue();
            if ((toSkip == null) || (!toSkip.equals(userNameKey))) {
                handlerValue.sendMessage(message);
            }
        }
    }

    // Send a message to the client that is being handled
    public void sendMessage(String message) throws Exception {
        this.outToClient.writeBytes(message);
    }
}

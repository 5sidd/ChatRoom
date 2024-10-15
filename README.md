# ChatRoom

### Overview
This is a Java program that implements a chat application using socket programming. It consists of two main components - a client and server. Clients can send requests to the server. The server will then process these requests and respond/perform an action accordingly.

In the context of this project, clients will chat/send messages to other clients. A client will first register its username by sending a request to the server. The server will then process the inputted username, making sure that it contains no spaces and that it is less than 33 characters and also make sure that it is unique from the usernames of the other registered users. If the username is valid, the server will register the client and its username, and the client can begin sending messages to the other registered users/clients. If the username is invalid, the server will respond with an error message.

Whenever a new client is successfully registered and admitted to the Chat Room, the newly admitted client receives a response from the server containing the number of clients currently in the Chat Room as well as the usernames of all clients in the Chat Room. The clients that were already in the Chat Room will receive a broadcast message from the server that indicates that a new client has been admitted to the Chat Room, as well as the username of the newly admitted client.

Once a client is registered, it has the option of sending either a broadcast message or a private message. If the client sends a broadcast request, then the message will be sent to all other registered clients except the client that is sending the broadcast message. In a private message, a client can send a message individually to another client by specifying that client's username in the request. Only the specified client will receive the message. If a client user attempts to send a private message to client whose username does not exist in the registry, then the server will respond with an error message.

Lastly, a client can leave the Chat Room by sending a request to the server. The server will then deregister the client and its username and respond with the updated number of clients in the Chat Room, as well as the usernames of the remaining clients.

### Running Instructions
1. Compile the Server.java file: `javac Server.java`
2. Run the Server.java file: `java Server <hostname> <port number>`
3. Compile the Client.java file: `javac Client.java`
4. Run the Client.java file: `java Client <server hostname> <server port number>`

### Running Instructions - Additional Notes
- When running the Server.java file, the `<hostname>` and `<port number>` command line arguments MUST be entered
- When running the Server.java file, inputting 0 as the `<port number>` argument means that the program will find a port number for you
- When running the Client.java file, the `<server hostname>` and `<server port number>` command line arguments MUST be entered
- The `<server hostname>` argument when running Client.java is the hostname of the server the client wants to connect to
- The `<server port number>` argument when running Client.java is the port number of the server the client wants to connect to

### Example Run
This is assuming the Server.java and Client.java files have already been compiled (see compilation instructions in Running Instructions section)

Run Server.java: `java Server net01.utdallas.edu 0` --> This is starting a server at hostname net01.utdallas.edu and it lets the program choose an open port to run on

Assume that the server is running on port number 35993

Run Client.java: `java Client net01.utdallas.edu 35993` --> This will connect the client to the server that is running on net01.utdallas.edu at port number 35993

The client can begin communicating with the server!

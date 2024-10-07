# ChatRoom

### Overview
This is a Java program that implements a chat application using socket programming.

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

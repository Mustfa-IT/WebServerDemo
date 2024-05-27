# Web Server Project

This project is a simple web server implemented in Java. It uses the NIO (Non-blocking I/O) package for handling network communication.

## Main Components

### Main.java
This is the entry point of the application. It creates an instance of the ServerManager class and starts the server.

### ServerManager.java
This class is responsible for managing the server. It uses the Singleton pattern to ensure that only one instance of the server is created. The server listens for incoming connections on a specified port and handles them in a non-blocking manner using a Selector and ServerSocketChannel.

### HttpHandler.java
This class is responsible for handling HTTP requests. It parses the request, determines the HTTP method (GET, POST, etc.), and calls the appropriate method to handle the request. For GET requests, it sends the requested file if it exists, or a 404 error page if it doesn't. POST requests are currently not supported.

### Http.java
This class represents an HTTP request. It contains the HTTP method, the request body, the request path, and the request headers.

### HttpRequestParser.java
This class is responsible for parsing raw HTTP requests into Http objects. It splits the request into lines, parses the request line to get the method, path, and protocol, parses the headers, and extracts the body.

## Running the Project
To run the project, simply run the Main class. The server will start and listen for incoming connections on port 8080.

## Future Improvements
- Implement support for POST requests in the HttpHandler class.
- Improve error handling and add more detailed logging.
- Add support for more HTTP methods and headers.
- Implement a more robust system for serving static files.

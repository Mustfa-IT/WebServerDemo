package com.example.web_server.core.http;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.example.web_server.core.http.utils.HttpRequestParser;

public class HttpHandler {
    private static final String INDEX_HTML_PATH = "/app/resources/index.html";
    private static final String NOT_FOUND_HTML_PATH = "/error/404.html";
    private static final String ROOT_PATH = "/app/resources";

    public void handle(String request, SocketChannel clientChannel) {
        try {
            long start = System.currentTimeMillis();
            // Parse the HTTP request
            System.out.println("Received request" + request);
            System.out.println();
            HttpRequestParser parser = new HttpRequestParser();
            Http httpRequest = parser.parse(request);

            switch (httpRequest.getMethod()) {
                case "GET":
                    System.out.println("Received GET request for " + httpRequest.getPath());
                    handleGetRequest(httpRequest, clientChannel);
                    break;
                case "POST":
                    System.out.println("Received POST request for " + httpRequest.getPath());
                    handlePostRequest(httpRequest);
                    break;
                default:
                    System.out.println("Received request for " + httpRequest.getPath());
                    break;
            }
            long end = System.currentTimeMillis();
            System.out.println("Request processed in " + (end - start) + "ms");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handlePostRequest(Http httpRequest) {
        throw new UnsupportedOperationException("Unimplemented method 'handlePostRequest'");
    }

    private static void handleGetRequest(Http httpRequest, SocketChannel clientChannel) {
        try {
            sendFileAtPath(httpRequest.getPath(), clientChannel);
        } catch (IllegalStateException | IOException e) {
            sendNotFoundHtml(clientChannel);
        }
    }

    private static void sendNotFoundHtml(SocketChannel clientChannel) {
        try {
            sendFileAtPath(NOT_FOUND_HTML_PATH, clientChannel);
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendFileAtPath(String path, SocketChannel clientChannel)
            throws IllegalStateException, IOException {
        if (clientChannel == null) {
            throw new IllegalStateException("Client channel is not set");
        }
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        if (path.equals("/favicon.ico")) {
            // Ignore requests for favicon
            // TODO: Find a better way to handle this
            System.out.println("Ignoring request for favicon");
            return;
        }
        Path filePath;

        if (!path.equals("/")) {
            filePath = Paths.get(ROOT_PATH + path);
        } else {
            filePath = Paths.get(INDEX_HTML_PATH);
        }

        byte[] fileBytes = Files.readAllBytes(filePath);
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        buffer.put("HTTP/1.1 200 OK\r\n".getBytes());
        buffer.put("Content-Type: text/html\r\n".getBytes());
        buffer.put(("Content-Length: " + fileBytes.length + "\r\n\r\n").getBytes());
        buffer.put(fileBytes);
        buffer.flip();

        while (buffer.hasRemaining()) {
            clientChannel.write(buffer);
        }

    }

}

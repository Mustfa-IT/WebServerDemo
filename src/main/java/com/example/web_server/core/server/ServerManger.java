package com.example.web_server.core.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.Key;
import java.util.Iterator;

import com.example.web_server.core.http.Http;
import com.example.web_server.core.http.HttpHandler;

public class ServerManger {
    public static final int PORT = 8080;

    private ServerManger() {
    }
    private static ServerManger instance;
    
    public static ServerManger getInstance() {
        if (instance == null) {
            instance = new ServerManger();
        }
        return instance;
    }
    public void start() throws IOException {
        System.out.println("Server started on port " + PORT);
        try (Selector selector = Selector.open();
                ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            serverChannel.bind(new InetSocketAddress(PORT));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    if (key.isAcceptable()) {
                        SocketChannel clientChannel = serverChannel.accept();
                        if (clientChannel != null) {
                            clientChannel.configureBlocking(false);
                            clientChannel.register(selector, SelectionKey.OP_READ);
                        }
                    } else if (key.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int bytesRead = clientChannel.read(buffer);

                        if (bytesRead != -1) {
                            String request = new String(buffer.array()).trim();
                            System.out.println("Connection opened "+clientChannel.getRemoteAddress());
                            HttpHandler.handle(request, clientChannel);                             
                            clientChannel.close();
                            System.out.println("Connection closed");

                            keyIterator.remove();
                        }
                    }else if(key.isWritable()){
                        // SocketChannel clientChannel = (SocketChannel) key.channel();
                        // ByteBuffer buffer = ByteBuffer.allocate(1024);
                        // buffer.put("HTTP/1.1 200 OK\r\n".getBytes());
                        // buffer.put("Content-Type: text/html\r\n".getBytes());
                        // buffer.put(("Content-Length: " + fileBytes.length + "\r\n\r\n").getBytes());
                        // buffer.put(fileBytes);
                        // buffer.flip();
                        // while (buffer.hasRemaining()) {
                        //     clientChannel.write(buffer);
                        // }
                        // clientChannel.close();
                        // System.out.println("Connection closed");
                        // keyIterator.remove();
                    }
                }
            }
        }
    }

}

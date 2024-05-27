package com.example.web_server.core.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import com.example.web_server.core.http.HttpHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerManager {
    public static final int PORT = 8080;
    private static final int BUFFER_SIZE = 1024;
    private ExecutorService executor;

    private ServerManager() {
        executor = Executors.newFixedThreadPool(4);
    }

    private static ServerManager instance;

    public static ServerManager getInstance() {
        if (instance == null) {
            instance = new ServerManager();
        }
        return instance;
    }

    public void start() {
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
                    keyIterator.remove();

                    try {
                        if (key.isAcceptable()) {
                            handleAcceptableKey(serverChannel, selector);
                        } else if (key.isReadable()) {
                            handleReadableKey(key);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAcceptableKey(ServerSocketChannel serverChannel, Selector selector) throws IOException {
        executor.submit(() -> {
            try {
                SocketChannel clientChannel = serverChannel.accept();
                clientChannel.configureBlocking(false);
                clientChannel.register(selector, SelectionKey.OP_READ);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void handleReadableKey(SelectionKey key) throws IOException {
        long startTime = System.currentTimeMillis();
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        int bytesRead = clientChannel.read(buffer);

        if (bytesRead != -1) {
            buffer.flip();
            String request = new String(buffer.array(), 0, bytesRead).trim();
            System.out.println("Connection opened " + clientChannel.getRemoteAddress());
            // Handle HTTP request
            HttpHandler httpHandler = new HttpHandler();
            httpHandler.handle(request, clientChannel);
            clientChannel.close();
            System.out.println("Connection closed");
            System.out.println("Server processed in " + (System.currentTimeMillis() - startTime) + "ms");
        }
    }
}

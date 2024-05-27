package com.example.web_server.core;

import com.example.web_server.core.server.ServerManager;

public class Main {

    public static void main(String[] args) {
        ServerManager server = ServerManager.getInstance();
        server.start();
    }

}
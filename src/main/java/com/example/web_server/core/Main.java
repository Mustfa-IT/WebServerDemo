package com.example.web_server.core;

import java.io.IOException;

import com.example.web_server.core.server.ServerManger;
public class Main {

    public static void main(String[] args) {
        ServerManger server = ServerManger.getInstance();
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   
}
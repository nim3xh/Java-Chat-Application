package org.chat;

import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost"; // Server IP address
    private static final int SERVER_PORT = 3800; // Server port

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            new ReadThread(socket).start(); // Thread to read messages from server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            String message;

            System.out.println("Connected to chat server");
            while ((message = userInput.readLine()) != null) {
                out.println(message); // Send message to server
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Thread to read messages from server
    private static class ReadThread extends Thread {
        private Socket socket;
        private BufferedReader in;

        public ReadThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message;

                while ((message = in.readLine()) != null) {
                    System.out.println(message); // Print server message
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


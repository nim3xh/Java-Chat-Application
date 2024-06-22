package org.chat;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static final int PORT = 3800;

    public static void main(String[] args) {
        System.out.println("Chat server started...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            // Start the thread for server to send messages
            new ServerInputHandler().start();

            while (true) {
                // Accept new client connections and start handler thread for each
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Inner class to handle server input and broadcast messages
    private static class ServerInputHandler extends Thread {
        @Override
        public void run() {
            try (BufferedReader serverInput = new BufferedReader(new InputStreamReader(System.in))) {
                String serverMessage;
                while ((serverMessage = serverInput.readLine()) != null) {
                    synchronized (clientWriters) {
                        for (PrintWriter writer : clientWriters) {
                            writer.println("Server: " + serverMessage);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Inner class to handle each client connection
    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // Initialize I/O streams
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Add the writer to the set of all client writers
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                String clientMessage;
                while ((clientMessage = in.readLine()) != null) {
                    System.out.println("Client says: " + clientMessage);
                    // Broadcast the message to all clients
                    synchronized (clientWriters) {
                        for (PrintWriter writer : clientWriters) {
                            writer.println(clientMessage);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
            }
        }
    }
}


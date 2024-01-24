package network;

import objects.TCPMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPListener extends Thread {

    private final ServerSocket serverSocket;
    private static final List<Observer> observers = new ArrayList<>();

    public TCPListener(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addObserver(Observer observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                new ClientHandler(serverSocket.accept()).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                ObjectInputStream objectIn = new ObjectInputStream(clientSocket.getInputStream());

                TCPMessage inputMessage;
                while ((inputMessage = (TCPMessage) objectIn.readObject()) != null) {
                    for (Observer observer : observers) {
                        observer.handle(inputMessage);
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

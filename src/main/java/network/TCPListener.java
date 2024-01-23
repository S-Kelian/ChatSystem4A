package network;

import objects.TCPMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPListener extends Thread {

    private final ServerSocket serverSocket;
    private final List<Observer> observers = new ArrayList<>();

    public TCPListener(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addObserver(Observer observer) {
        synchronized (this.observers) {
            this.observers.add(observer);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                new EchoClientHandler(serverSocket.accept(), observers).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class EchoClientHandler extends Thread {
        private final Socket clientSocket;
        private final List<Observer> observers;

        public EchoClientHandler(Socket socket, List<Observer> observers) {
            this.clientSocket = socket;
            this.observers = observers;
        }

        public void run() {
            try {
                ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream objectIn = new ObjectInputStream(clientSocket.getInputStream());

                TCPMessage inputMessage;
                while ((inputMessage = (TCPMessage) objectIn.readObject()) != null) {
                    for (Observer observer : observers) {
                        observer.handle(inputMessage);
                    }
                }

                objectIn.close();
                objectOut.close();
                clientSocket.close();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

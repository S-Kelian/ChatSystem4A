package network;

import objects.TCPMessage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPSender {
    
    private Socket clientSocket;
    private ObjectOutputStream objectOut;

    private static final List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public void startConnection(String ip, int port) throws IOException {
        if (ip.startsWith("/")) {
            ip = ip.substring(1);
        }
        clientSocket = new Socket(ip, port);
        objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
    }

    public void sendMessage(TCPMessage msg) throws IOException {
        objectOut.writeObject(msg);
        for (Observer observer : observers) {
            observer.handle(msg);
        }
        objectOut.flush();
    }

    public void stopConnection() throws IOException {
        objectOut.close();
        clientSocket.close();
    }

}
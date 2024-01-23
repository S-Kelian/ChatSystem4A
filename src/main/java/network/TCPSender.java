package network;

import objects.TCPMessage;

import java.io.*;
import java.net.Socket;

public class TCPSender {
    
    private Socket clientSocket;
    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
        objectIn = new ObjectInputStream(clientSocket.getInputStream());
    }

    public void sendMessage(TCPMessage msg) throws IOException {
        objectOut.writeObject(msg);
        objectOut.flush();
    }

    public void stopConnection() throws IOException {
        objectIn.close();
        objectOut.close();
        clientSocket.close();
    }

}
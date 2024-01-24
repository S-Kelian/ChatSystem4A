package network;

import objects.TCPMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * TCPSender (client) class
 * Sends TCP messages
 */
public class TCPSender {

    private static final Logger LOGGER = LogManager.getLogger(TCPSender.class);

    private Socket clientSocket;
    private ObjectOutputStream objectOut;

    private static final List<Observer> observers = new ArrayList<>();

    /**
     * Add an observer to the list of observers
     * @param observer observer to add
     */
    public synchronized void addObserver(Observer observer) {
        synchronized (observers) {
            LOGGER.info("New observer added");
            observers.add(observer);
        }
    }

    /**
     * Start a TCP connection
     * @param ip ip of the connection
     * @param port port of the connection
     */
    public synchronized void startConnection(String ip, int port) throws IOException {
        LOGGER.info("Starting TCP connection with " + ip + " on port " + port);
        if (ip.startsWith("/")) {
            ip = ip.substring(1);
        }
        clientSocket = new Socket(ip, port);
        objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
    }

    /**
     * Send a TCP message
     * @param msg message to send
     */
    public synchronized void sendMessage(TCPMessage msg) throws IOException {
        LOGGER.info("Sending message " + msg.toString());
        objectOut.writeObject(msg);
        for (Observer observer : observers) {
            observer.handle(msg);
        }
        objectOut.flush();
    }

    /**
     * Stop the TCP connection
     */
    public void stopConnection() throws IOException {
        LOGGER.info("Stopping TCP connection");
        objectOut.close();
        clientSocket.close();
    }

}
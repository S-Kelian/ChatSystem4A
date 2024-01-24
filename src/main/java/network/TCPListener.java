package network;

import objects.TCPMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * TCPListener (server) class
 * Listens to TCP messages
 */
public class TCPListener extends Thread {

    /**
     * Logger of the class TCPListener
     */
    private static final Logger LOGGER = LogManager.getLogger(TCPListener.class);

    /**
     * Socket of the TCPListener
     */
    private final ServerSocket serverSocket;

    /**
     * List of observers
     */
    private static final List<Observer> observers = new ArrayList<>();

    /**
     * Constructor of the TCPListener
     * @param port port of the listener
     */
    public TCPListener(int port) {
        try {
            serverSocket = new ServerSocket(port);
            LOGGER.info("TCPListener started on port " + port);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Add an observer to the list of observers
     * @param observer observer to add
     */
    public void addObserver(Observer observer) {
        synchronized (observers) {
            LOGGER.info("New observer added");
            observers.add(observer);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                new ClientHandler(serverSocket.accept()).start();
                LOGGER.info("New client connected");
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * ClientHandler class
     * Handles the client
     */
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
                    LOGGER.info("Received message: " + inputMessage.toString());
                    synchronized (observers) {
                        for (Observer observer : observers) {
                            observer.handle(inputMessage);
                        }
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                LOGGER.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}

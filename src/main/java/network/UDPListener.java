package network;

import objects.UDPMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * UDPListener (server) class
 * Listens to UDP messages
 */
public class UDPListener extends Thread{

    private static final Logger LOGGER = LogManager.getLogger(UDPListener.class);

    /**
     * Add an observer to the list of observers
     * @param observer observer to add
     */
    public void addObserver(Observer observer) {
        synchronized (this.observers) {
            LOGGER.info("New observer added");
            this.observers.add(observer);
        }
    }

    private final DatagramSocket socket;
    private final List<Observer> observers = new ArrayList<>();

    /**
     * Constructor of the UDPListener
     */
    public UDPListener() throws SocketException {
        int port = 49000;
        LOGGER.info("UDPListener started on port " + port);
        this.socket = new DatagramSocket(port);
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                UDPMessage message = (UDPMessage) objectInputStream.readObject();

                LOGGER.info("New message received: " + message.toString());

                synchronized (this.observers) {
                    for (Observer observer : observers) {
                        observer.handle(message);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                LOGGER.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

}

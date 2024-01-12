package network;

import objects.UDPMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;


public class UDPListener extends Thread{

    public void addObserver(Observer observer) {
        synchronized (this.observers) {
            this.observers.add(observer);
        }
    }

    static int port = 49000;
    private final DatagramSocket socket;

    private final List<Observer> observers = new ArrayList<>();

    public UDPListener() throws SocketException {
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

                synchronized (this.observers) {
                    for (Observer observer : observers) {
                        observer.handle(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

}

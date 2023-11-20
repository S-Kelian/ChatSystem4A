package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import objects.SystemApp;

public class UDPListener {
    Thread thread;
    static int port = 49000;

    private SystemApp app = SystemApp.getInstance();

    public static void log(Object o) {
        Thread thread = Thread.currentThread();
        System.out.println("[" + thread.getName() + "] " + o);
    }

    private class GreeterServer extends Thread {
        @Override
        public void run() {
            try (DatagramSocket socket = new DatagramSocket(port)) {
                boolean running = true;
                byte[] bufferRecv = new byte[1024];
                DatagramPacket packet = new DatagramPacket(bufferRecv, bufferRecv.length);
                while (running) {
                    socket.setBroadcast(true);
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    log("Received: " + message);
                    //envoie du message au SystemApp
                    app.receiveMessage(message, packet.getAddress());

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public UDPListener() throws SocketException, UnknownHostException {
        thread = new GreeterServer();
    }

    public void start() {
        thread.start();
    }

}

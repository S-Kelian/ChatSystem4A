package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import objects.SystemApp;

public class UDPListener {
    Thread thread;
    static int port = 49000;

    public static void log(Object o) {
        Thread thread = Thread.currentThread();
        System.out.println("[" + thread.getName() + "] " + o);
    }

    private static class GreeterServer extends Thread {
        @Override
        public void run() {
            try (DatagramSocket socket = new DatagramSocket(port)) {
                byte[] bufferRecv = new byte[1024];
                DatagramPacket packet = new DatagramPacket(bufferRecv, bufferRecv.length);
                while (true) {
                    socket.setBroadcast(true);
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    log("Received: " + message);
                    //envoie du message au SystemApp
                    SystemApp.receiveMessage(message);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public UDPListener() {
        thread = new GreeterServer();
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }
}

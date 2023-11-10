package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPListener {
    Thread thread;

    public static void log(Object o) {
        Thread thread = Thread.currentThread();
        System.out.println("[" + thread.getName() + "] " + o);
    }

    private static class GreeterServer extends Thread {
        @Override
        public void run() {
            boolean running = true;
            int port = 1789;
            try (DatagramSocket socket = new DatagramSocket(port)) {
                byte[] bufferRecv = new byte[1024];
                DatagramPacket packet = new DatagramPacket(bufferRecv, bufferRecv.length);
                while (running) {
                    socket.setBroadcast(true);
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    log("Greetings "+message+" !!!");
                    if (message.equals("bye")) {
                        running = false;
                    }
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
}

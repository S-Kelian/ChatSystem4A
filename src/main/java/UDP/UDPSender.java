package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPSender {

    private static DatagramSocket socket = null;

    public UDPSender( InetAddress adresse, int port) throws SocketException {
        socket = new DatagramSocket(port, adresse);
    }
    
    public void broadcast( String broadcastMessage, InetAddress address) throws IOException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);
        byte[] buffer = broadcastMessage.getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 1789);
        socket.send(packet);
        socket.close();
    }
}


package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPSender {

    private static DatagramSocket socket = null;

    //port de réception par défaut
    private final int otherPort = 49000;

    public UDPSender( InetAddress adresse) throws SocketException {
        //port d'envoi par défaut
        int myPort = 49001;
        socket = new DatagramSocket(myPort, adresse);
    }
    
    public void sendBroadcast( String broadcastMessage, InetAddress address) throws IOException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);
        byte[] buffer = broadcastMessage.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, otherPort);
        socket.send(packet);
        socket.close();
    }

    public void sendUnicast( String unicastMessage, InetAddress address) throws IOException {
        socket = new DatagramSocket();
        socket.setBroadcast(false);
        byte[] buffer = unicastMessage.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, otherPort);
        socket.send(packet);
        socket.close();
    }

}


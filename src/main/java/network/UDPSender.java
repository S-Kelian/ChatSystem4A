package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPSender {

    private static DatagramSocket socket = null;

    public UDPSender( InetAddress adresse) throws SocketException {
        //port d'envoi par défaut
        int myPort = 49001;
        socket = new DatagramSocket(myPort, adresse);
    }
    
    public void send( String broadcastMessage, InetAddress address, boolean broadcast) throws IOException {
        socket = new DatagramSocket();
        socket.setBroadcast(broadcast);
        byte[] buffer = broadcastMessage.getBytes();
        //port de réception par défaut
        int otherPort = 49000;
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, otherPort);
        socket.send(packet);
        socket.close();
    }

}


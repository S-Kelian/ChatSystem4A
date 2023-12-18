package network;

import objects.UDPMessage;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPSender {

    private static DatagramSocket socket = null;

    public UDPSender( InetAddress adresse) throws SocketException {
        //default sender port
        int myPort = 49001;
        socket = new DatagramSocket(myPort, adresse);
    }
    
    public void send(UDPMessage msg) throws IOException {
        socket = new DatagramSocket();
        socket.setBroadcast(msg.isBroadcast());

        InetAddress address = msg.getReceiver();
        final ByteArrayOutputStream byteArrayOutStream = new ByteArrayOutputStream(6400);
        final ObjectOutput objOut = new ObjectOutputStream(byteArrayOutStream);
        objOut.writeObject(msg);
        objOut.close();

        byte[] buffer = byteArrayOutStream.toByteArray();
        //default receiver port
        int otherPort = 49000;
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, otherPort);
        socket.send(packet);
        socket.close();
    }

}


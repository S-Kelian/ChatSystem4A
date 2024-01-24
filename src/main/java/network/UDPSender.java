package network;

import objects.UDPMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * UDPSender (client) class
 * Sends UDP messages
 */
public class UDPSender {

    /**
     * Logger of the class UDPSender
     */
    private static final Logger LOGGER = LogManager.getLogger(UDPSender.class);

    /**
     * Socket of the UDPSender
     */
    private static DatagramSocket socket = null;

    /**
     * Constructor of the UDPListener
     * @param adresse address of the listener
     */
    public UDPSender( InetAddress adresse) throws SocketException {
        LOGGER.info("UDPListener started on port " + 49001);
        int myPort = 49001;
        socket = new DatagramSocket(myPort, adresse);
    }

    /**
     * Send a UDP message
     * @param msg message to send
     */
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
        LOGGER.info("Sending message " + msg.toString());
        socket.send(packet);
        socket.close();
    }

}


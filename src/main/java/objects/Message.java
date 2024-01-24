package objects;

import java.net.InetAddress;

/**
 * This interface represents a message
 */
public interface Message {

    /**
     * Get the content of the message
     */
    String content = null;

    /**
     * Get the sender ip of the message
     */
    InetAddress sender = null;

    /**
     * Get the receiver ip of the message
     */
    InetAddress receiver = null;

    /**
     * Get the content of the message
     * @return content of the message
     */
    String getContent();

    /**
     * Get the sender ip of the message
     * @return ip of the sender
     */
    InetAddress getSender();

    /**
     * Get the receiver ip of the message
     * @return ip of the receiver
     */
    InetAddress getReceiver();

}

package objects;

import utils.TYPEUDPMESSAGE;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * This class represents a UDP message
 */
public class UDPMessage implements Message, Serializable {

    /**
     * Content of the message
     */
    private final String content;

    /**
     * Sender of the message
     */
    private final InetAddress sender;

    /**
     * Receiver of the message
     */
    private final InetAddress receiver;

    /**
     * Type of the message
     */
    private final boolean broadcast;

    /**
     * True if the message is a broadcast, false otherwise
     */
    private final TYPEUDPMESSAGE type;

    /**
     * Constructor
     * @param content content of the message
     * @param sender sender of the message
     * @param receiver receiver of the message
     * @param type type of the message
     * @param broadcast true if the message is a broadcast, false otherwise
     */
    public UDPMessage(String content, InetAddress sender, InetAddress receiver, TYPEUDPMESSAGE type, boolean broadcast) {
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.broadcast = broadcast;
    }

    /**
     * Get the content of the message
     * @return content of the message
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Get the sender ip of the message
     * @return ip of the sender
     */
    public InetAddress getSender() {
        return this.sender;
    }

    /**
     * Get the receiver ip of the message
     * @return ip of the receiver
     */
    public InetAddress getReceiver() {
        return this.receiver;
    }

    /**
     * Check if the message is a broadcast
     * @return true if the message is a broadcast, false otherwise
     */
    public boolean isBroadcast() {
        return this.broadcast;
    }

    /**
     * Get the type of the message
     * @return type of the message
     */
    public TYPEUDPMESSAGE getType() {
        return this.type;
    }

    /**
     * Get the string representation of the message
     * @return string representation of the message
     */
    public String toString() {
        return "UDPMessage{" +
                "content='" + content + "'" +
                ", type='" + type + "'" +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", broadcast=" + broadcast +
                '}';
    }
}

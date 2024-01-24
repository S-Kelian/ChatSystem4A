package objects;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;

/**
 * This class represents a TCP message
 */
public class TCPMessage implements Message, Serializable {

    /**
     * Content of the message
     */
    private final String content;

    /**
     * Sender ip of the message
     */
    private final InetAddress sender;

    /**
     * Receiver ip of the message
     */
    private final InetAddress receiver;

    /**
     * Date and time of the message
     */
    private final String date;

    /**
     * Type of the message (message, image, file, etc)
     * Not implemented yet but will be used to handle different types of messages
     */
    private final int type;

    /**
     * Constructor
     * @param content content of the message
     * @param sender sender of the message
     * @param receiver receiver of the message
     * @param date date of the message
     * @param type type of the message
     */
    public TCPMessage(String content, InetAddress sender, InetAddress receiver, String date, int type){
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.type = type;
    }

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public InetAddress getReceiver() {
        return this.receiver;
    }

    /**
     * Get the sender ip of the message
     * @return ip of the sender
     */
    public InetAddress getSender() {
        return this.sender;
    }

    /**
     * Get the date of the message
     * @return date of the message
     */
    public String getDate() {
        return this.date;
    }

    /**
     * Get the type of the message
     * @return type of the message
     */
    public int getType() {
        return this.type;
    }

    /**
     * Get the type of the message
     * @return type of the message
     */
    public String toString() {
        return "TCPMessage{" +
                "content='" + content + '\'' +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", date=" + date +
                ", type=" + type +
                '}';
    }
    
}

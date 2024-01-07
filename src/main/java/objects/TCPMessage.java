package objects;

import java.net.InetAddress;
import java.util.Date;

public class TCPMessage implements Message{

    private final String content;
    private final InetAddress sender;
    private final InetAddress receiver;
    private final String date; // date of the message
    private final int type; // type of the message

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

    public InetAddress getSender() {
        return this.sender;
    }

    public String getDate() {
        return this.date;
    }

    public int getType() {
        return this.type;
    }
    
}

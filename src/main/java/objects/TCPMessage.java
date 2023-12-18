package objects;

import java.net.InetAddress;

public class TCPMessage implements Message{

    private final String content;
    private final InetAddress sender;
    private final InetAddress receiver;

    public TCPMessage(String content, InetAddress sender, InetAddress receiver){
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public InetAddress getReceiver() {
        return this.receiver;
    }

    @Override
    public InetAddress getSender() {
        return this.sender;
    }
    
}

package objects;

import java.net.InetAddress;

public class UDPMessage implements Message{

private enum TYPEUDPMESSAGE {Request, Response, Disconection, Rename};

    private final String content;
    private final InetAddress sender;
    private final InetAddress receiver;
    private final boolean broadcast;
    private TYPEUDPMESSAGE type;

    public UDPMessage(String content, InetAddress sender, InetAddress receiver, TYPEUDPMESSAGE type, boolean broadcast) {
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.broadcast = broadcast;
    }

    public String getContent() {
        return content;
    }

    public InetAddress getSender() {
        return sender;
    }

    public InetAddress getReceiver() {
        return receiver;
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    public TYPEUDPMESSAGE getType() {
        return type;
    }

    public String toString() {
        return "UDPMessage{" +
                "content='" + content + '\'' +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", broadcast=" + broadcast +
                '}';
    }
}

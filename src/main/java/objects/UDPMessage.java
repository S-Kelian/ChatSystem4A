package objects;

import java.io.Serializable;
import java.net.InetAddress;

public class UDPMessage implements Message, Serializable {

    public enum TYPEUDPMESSAGE {
        REQUEST, RESPONSE, DISCONNECTION, RENAME, CHATREQUEST, CHATANSWER, STOPCHAT
    }

    private final String content;
    private final InetAddress sender;
    private final InetAddress receiver;
    private final boolean broadcast;
    private final TYPEUDPMESSAGE type;

    public UDPMessage(String content, InetAddress sender, InetAddress receiver, TYPEUDPMESSAGE type, boolean broadcast) {
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.broadcast = broadcast;
    }

    public String getContent() {
        return this.content;
    }

    public InetAddress getSender() {
        return this.sender;
    }

    public InetAddress getReceiver() {
        return this.receiver;
    }

    public boolean isBroadcast() {
        return this.broadcast;
    }

    public TYPEUDPMESSAGE getType() {
        return this.type;
    }

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

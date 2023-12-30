package objects;

import java.net.InetAddress;

public interface Message {

    String content = null;
    InetAddress sender = null;
    InetAddress receiver = null;

    public String getContent();
    public InetAddress getSender();
    public InetAddress getReceiver();

}

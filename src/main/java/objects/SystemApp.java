package objects;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import UDP.UDPListener;
import UDP.UDPSender;
import objects.User;

public class SystemApp {
    private User me;
    private ArrayList<User> contactList;
    private UDPListener listener;
    private UDPSender udpUnicast;
    private UDPSender udpBroadcast;

    public SystemApp() throws SocketException, UnknownHostException {
        this.me = new User("me", InetAddress.getLocalHost());
        this.contactList = new ArrayList();
        this.listener = new UDPListener();
        this.udpUnicast = new UDPSender(me.getIp(), me.getPort());
        this.udpBroadcast = new UDPSender(me.getIp(), me.getPort());
    }

    public void start() {
        listener.start();
    }



}

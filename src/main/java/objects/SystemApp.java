package main.java.objects;
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

    public SystemApp(User me) {
        this.me = me;
        this.contactList = new ArrayList<User>();
        this.listener = new UDPListener();
        this.udpUnicast = new UDPSender(me.getIp(), me.getPort());
        this.udpBroadcast = new UDPSender(me.getIp(), me.getPort());
    }

    public void start() {
        listener.start();
    }



}

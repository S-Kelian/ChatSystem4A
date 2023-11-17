package objects;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import UDP.UDPListener;
import UDP.UDPSender;

public class SystemApp {
    private User me;
    private ArrayList<User> contactList;
    private UDPListener listener;
    private UDPSender udpUnicast;
    private UDPSender udpBroadcast;

    private static SystemApp instance = null;

    private SystemApp() throws SocketException, UnknownHostException {
        this.me = new User("me", InetAddress.getLocalHost());
        this.contactList = new ArrayList();
        this.listener = new UDPListener();
        this.udpUnicast = new UDPSender(me.getIp(), me.getPort());
        this.udpBroadcast = new UDPSender(me.getIp(), me.getPort());
    }

    public static SystemApp getInstance() throws SocketException, UnknownHostException {
        if (instance == null) {
            instance = new SystemApp();
        }
        return instance;
    }

    public User getMe() {
        return me;
    }
    public ArrayList<User> getContactList() {
        return contactList;
    }

    public void start() {
        listener.start();
    }
    public void stop() {
        listener.stop();
    }

    public boolean setUsername(String nickname){
        for (User user : contactList) {
            if (user.getNickname().equals(nickname)) {
                return false;
            }
        }
        me.setNickname(nickname);
        return true;
    }

    public void sendBroadcast(String message) {
        try {
            udpBroadcast.broadcast(message, InetAddress.getByName("255.255.255.255"));
        } catch (IOException e) {
        }
    }
    public void sendUnicast(String message, InetAddress address) {
        try {
            udpUnicast.broadcast(message, address);
        } catch (IOException e) {
        }
    }
    public void addContact(User user) {
        contactList.add(user);
    }
    public void removeContact(User user) {
        contactList.remove(user);
    }
    public void receiveMessage(String message, InetAddress address, int port) {
        if (message.startsWith("update request from : ")) {
            String messageToSend = "update response from : " + me.getNickname();
            sendUnicast(messageToSend, address);
        }
    }
    public void contactListUpdateRoutine() {
        String updateMessage = "update request from : " + me.getNickname();
        sendBroadcast(updateMessage);

    }


}
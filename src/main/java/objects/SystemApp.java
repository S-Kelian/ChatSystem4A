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

    public SystemApp() throws SocketException, UnknownHostException {
        this.me = new User("me", InetAddress.getLocalHost());
        this.contactList = new ArrayList();
        this.listener = new UDPListener();
        this.udpUnicast = new UDPSender(me.getIp(), me.getPort());
        this.udpBroadcast = new UDPSender(me.getIp(), me.getPort());
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
    public static void receiveMessage(String message) {
        System.out.println("System app: " + message);
    }
    public void contactListUpdateRoutine() {
        String updateMessage = "update";
        sendBroadcast(updateMessage);

    }


}
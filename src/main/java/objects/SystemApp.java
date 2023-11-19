package objects;

import UDP.UDPSender;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class SystemApp {
    private final User me;
    private final ArrayList<User> usersOnline;

    private final UDPSender udpSender;
    private static SystemApp instance = null;

    private SystemApp() throws SocketException, UnknownHostException {
        this.me = new User("me", InetAddress.getLocalHost());
        this.usersOnline = new ArrayList<>();
        this.udpSender = new UDPSender(me.getIp());
        this.addUserOnline(me);
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
    public ArrayList<User> getUsersOnline() {
        return usersOnline;
    }

    public boolean setUsername(String nickname){
        usersOnlineUpdateRoutine();
        for (User user : usersOnline) {
            if (user.getNickname().equals(nickname)) {
                return false;
            }
        }
        me.setNickname(nickname);
        return true;
    }

    /**
     * Send a message to all users
     * @param message to send
     */
    public void sendBroadcast(String message) {
        try {
            udpSender.sendBroadcast(message, InetAddress.getByName("255.255.255.255"));
        } catch (IOException ignored) {
        }
    }

    /**
     * Send a message to a specific user
     * @param message to send
     * @param address of the user
     */
    public void sendUnicast(String message, InetAddress address) {
        try {
            udpSender.sendUnicast(message, address);
        } catch (IOException ignored) {
        }
    }
    public void addUserOnline(User user) {
        usersOnline.add(user);
    }
    public void removeUserOnline(User user) {
        usersOnline.remove(user);
    }

    /**
     * Treatment of the received message
     * @param message received
     * @param address of the sender
     */
    public void receiveMessage(String message, InetAddress address) {
        if (message.startsWith("update request from : ")) {
            String messageToSend = "update response from : " + me.getNickname();
            sendUnicast(messageToSend, address);
        } else if (message.startsWith("update response from : ")) {
            // get the nickname of the user and add it to the list of users online if it is not already in it
            String nickname = message.substring(23);
            if (!checkUsersOnlineByName(nickname)) {
                User user = new User(nickname, address);
                addUserOnline(user);
            }
        }
    }

    /**
     * Send a broadcast message to all users to update the list of users online
     */
    public void usersOnlineUpdateRoutine() {
        String updateMessage = "update request from : " + me.getNickname();
        sendBroadcast(updateMessage);
    }

    public boolean checkUsersOnlineByName(String name) {
        for (User user : usersOnline) {
            if (user.getNickname().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
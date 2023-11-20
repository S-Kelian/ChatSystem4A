package objects;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import UDP.UDPSender;

public class SystemApp {
    private final User me;
    private final ArrayList<User> usersList;

    private final UDPSender udpSender;
    private static SystemApp instance = null;

    private SystemApp() throws SocketException, UnknownHostException {
        this.me = new User("me", InetAddress.getLocalHost());
        this.usersList = new ArrayList<>();
        this.udpSender = new UDPSender(me.getIp());
        this.addUser(me);
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
    public ArrayList<User> getusersList() {
        return usersList;
    }

    public boolean setMyUsername(String nickname){
        usersListUpdateRoutine();
        for (User user : usersList) {
            if (user.getNickname().equals(nickname)) {
                return false;
            }
        }
        sendBroadcast("Nickname update : " + me.getNickname() + " -> " + nickname );
        me.setNickname(nickname);
        return true;
    }
    public boolean setSomeoneUsername(String previousNn, String newNn){
        usersListUpdateRoutine();
        User dude = getUserByName(previousNn);
        if (dude == null){
            return false;
        }
        dude.setNickname(newNn);
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
    public void addUser(User user) {
        usersList.add(user);
    }
    public void removeUserOnline(User user) {
        usersList.remove(user);
    }

    /**
     * Treatment of the received message
     * @param message received
     * @param address of the sender
     */
    public void receiveMessage(String message, InetAddress address) {
        if (address == me.getIp()) {
            return;
        }
        if (message.startsWith("update request from : ")) {
            String messageToSend = "update response from : " + me.getNickname();
            sendUnicast(messageToSend, address);
        } else if (message.startsWith("update response from : ")) {
            // get the nickname of the user and add it to the list of users online if it is not already in it
            String nickname = message.substring(23);
            if (!checkusersListByName(nickname)) {
                User user = new User(nickname, address);
                addUser(user);
            }
        } else if (message.startsWith("Nickname update : ")) {
            String[] nicknames = message.substring(18).split(" -> ");
            setSomeoneUsername(nicknames[0], nicknames[1]);
        }
    }

    /**
     * Send a broadcast message to all users to update the list of users online
     */
    public void usersListUpdateRoutine() {
        String updateMessage = "update request from : " + me.getNickname();
        sendBroadcast(updateMessage);
    }

    public boolean checkusersListByName(String name) {
        for (User user : usersList) {
            if (user.getNickname().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public User getUserByName(String name) {
        for (User user : usersList) {
            if (user.getNickname().equals(name)) {
                return user;
            }
        }
        return null;
    }

    public ArrayList<User> getUsersOnline(){
        ArrayList<User> usersOnline = new ArrayList<>();
        for (User user : usersList) {
            if (user.getStatus() != 0) {
                usersOnline.add(user);
            }
        }
        return usersOnline;
    }
}
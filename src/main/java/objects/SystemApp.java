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
    public ArrayList<User> getUsersList() {
        return usersList;
    }

    public String setMyUsername(String nickname){
        String returnStatus;

        if (nickname.equals("") || nickname == null) {
            returnStatus = "Username can't be empty";
            return returnStatus;
        }
        for (User user : usersList) {
            if (user.getNickname().equals(nickname)) {
                returnStatus = "Username already taken";
                return returnStatus;
            }
        }
        if (me.getNickname()!=null && !me.getNickname().equals(nickname)){
            sendBroadcast("Nickname update : " + nickname );
        }
        me.setNickname(nickname);
        returnStatus = "Success";
        return returnStatus;
    }
    public void setSomeoneUsername(InetAddress address, String newNn){
        User dude = getUserByIp(address);
        // if the user is not in the list, we add it
        if (dude == null){
            dude = new User(newNn, address);
            addUser(dude);
        }
        dude.setNickname(newNn);
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
    public void setUserOffline(User user) {
        user.setStatus(0);
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
            if (!getUserByIp(address).getNickname().equals(nickname) && !checkUsersListByName(nickname)) {
                User user = new User(nickname, address);
                addUser(user);
            } else {
                // if the user is already in the list, we update his nickname
                setSomeoneUsername(address, nickname);
            }
        } else if (message.startsWith("Nickname update : ")) {
            String nickname = message.substring(18);
            setSomeoneUsername(address, nickname);
        } else if (message.equals("disconnect")) {
            setUserOffline(getUserByIp(address));
        } else {
            // if the message is not a command, we display it
            System.out.println(message);
        }
    }

    /**
     * Send a broadcast message to all users to update the list of users online
     */
    public void usersListUpdateRoutine() {
        String updateMessage = "update request from : " + me.getNickname();
        sendBroadcast(updateMessage);
    }

    public boolean checkUsersListByName(String name) {
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

    public User getUserByIp(InetAddress ip) {
        for (User user : usersList) {
            if (user.getIp().equals(ip)) {
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

    public void disconnect() {
        sendBroadcast("disconnect");
        System.exit(0);
    }
}
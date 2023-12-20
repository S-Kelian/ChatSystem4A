package objects;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import customExceptions.UserNotFoundException;
import customExceptions.UsernameEmptyException;
import customExceptions.UsernameUsedException;
import network.UDPSender;

public class SystemApp {
    private final User me;
    private final UserList myUserList;
    private final UDPSender udpSender;
    private static SystemApp instance = null;

    private SystemApp() throws SocketException, UnknownHostException {
        InetAddress address = getMyIp();

        this.me = new User("me", address);
        this.udpSender = new UDPSender(me.getIp());
        myUserList = new UserList(me);

    }

    public static SystemApp getInstance() throws SocketException, UnknownHostException {
        if (instance == null) {
            instance = new SystemApp();
        }
        return instance;
    }

    /**
     * Get the user of the system
     * @return the user
     */
    public User getMe() {
        return me;
    }

    /**
     * Get the userlist of the system
     * @return the userlist
     */
    public UserList getMyUserList(){
        return myUserList;
    }
    /**
     * Set the nickname of the user and return an error code to let the user know if the operation was successful or not.
     * @param nickname new nickname of the user
     */
    public void setMyUsername(String nickname) throws UsernameEmptyException, UsernameUsedException { // the success of the operation is returned as an int equal to 0 if success and 1 if the nickname is already taken and 2 if the nickname is not valid
        // First we need to check if the user is using a valid nickname
        if (nickname.isEmpty()) {
            throw new UsernameEmptyException("Nickname is empty");
        }
        // Now that we know that the nickname us valid, we need to check if it is already taken
        if (myUserList.UserIsInListByNickmane(nickname)){
            throw new UsernameUsedException("Nickname already taken");
        }
        me.setNickname(nickname);
        myUserList.updateNickname(me.getIp(), me.getNickname());
        sendBroadcast("Nickname update : " + nickname, UDPMessage.TYPEUDPMESSAGE.RENAME);
    }

    /**
     * Set the nickname of a user of the userlist and return an error code to let the user know if the operation was successful or not.
     *
     * @param address of the user to update
     * @param newNn   new nickname of the user
     */
    public void setSomeoneUsername(InetAddress address, String newNn) throws UserNotFoundException, UsernameUsedException { // the success of the operation is returned as an int equal to 0 if success and 1 if the user is not in the list and 2 if the nickname is already taken
        // if the user is not in the list, we add him
        if (!myUserList.UserIsInListByIp(address)){
            myUserList.addUser(new User(newNn, address));
            throw new UserNotFoundException("error 404 user not found");
        }
        // if the user is in the list but with a different nickname, we update his nickname if it is not already taken
        if(!myUserList.UserIsInListByNickmane(newNn)){
            myUserList.updateNickname(address, newNn);
            return;
        }
        // if the user is in the list with the same nickname, we do nothing
        System.out.println("Nickname already taken");
        throw new UsernameUsedException("Nickname already taken");
    }

    /**
     * Send a message to all users
     * @param content of the message
     */
    public void sendBroadcast(String content, UDPMessage.TYPEUDPMESSAGE type) {
        try {
            UDPMessage message = new UDPMessage(content, me.getIp(), InetAddress.getByName("255.255.255.255"), type, true);
            udpSender.send(message);
        } catch (IOException ignored) {
        }
    }

    /**
     * Send a message to a specific user
     * @param content of the message
     * @param address of the receiver
     * @param type of the message
     */
    public void sendUnicast(String content, InetAddress address, UDPMessage.TYPEUDPMESSAGE type) {
        try {
            UDPMessage message = new UDPMessage(content, me.getIp(), address, type, false);
            udpSender.send(message);
        } catch (IOException ignored) {
        }
    }

    /**
     * Treatment of the received message
     * @param message received
     */
    public void receiveMessage(UDPMessage message) throws UserNotFoundException, UsernameUsedException {
        if (message.getSender().equals(me.getIp())) {
            return;
        }
        switch (message.getType()) {
            case REQUEST:
                String messageToSend = "update response from : " + me.getNickname();
                sendUnicast(messageToSend, message.getReceiver(), UDPMessage.TYPEUDPMESSAGE.RESPONSE);
                break;
            case RESPONSE:
                // get the nickname of the user and add it to the list of users online if it is not already in it
                String nickname = message.getContent().substring(23);
                InetAddress address = message.getSender();
                if (!myUserList.UserIsInListByIp(address)) {
                    User user = new User(nickname, address);
                    myUserList.addUser(user);
                } else {
                    // if the user is already in the list, we update his nickname if it is different and update his status
                    myUserList.updateUserStatus(address, 1);
                    if (myUserList.getUserByIp(address).getNickname().equals(nickname)) {
                        return;
                    }
                    setSomeoneUsername(address, nickname);
                }
                break;
            case DISCONNECTION:
                myUserList.updateUserStatus(message.getSender(), 0);
                break;
            case RENAME:
                setSomeoneUsername(message.getSender(), message.getContent().substring(18));
                break;
        }
    }

    /**
     * Send a broadcast message to all users to update the list of users online
     */
    public void usersListUpdateRoutine() {
        String updateMessage = "update request";
        sendBroadcast(updateMessage, UDPMessage.TYPEUDPMESSAGE.REQUEST);
    }

    /**
     * Disconnect the user from the chat
     */
    public void disconnect() {
        sendBroadcast("disconnect", UDPMessage.TYPEUDPMESSAGE.DISCONNECTION);
        System.exit(0);
    }

    public InetAddress getMyIp() throws SocketException, UnknownHostException {

        String os = System.getProperty("os.name");
        InetAddress address = null;
        if (os.equals("Linux")) {
            System.out.println("Linux");
            Enumeration<NetworkInterface> nics = NetworkInterface
                    .getNetworkInterfaces();
            while (nics.hasMoreElements()) {
                NetworkInterface nic = nics.nextElement();
                Enumeration<InetAddress> addrs = nic.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        System.out.println(addr.getHostAddress());
                        address = InetAddress.getByName(addr.getHostAddress());
                        break;
                    }
                }
            }
        } else if (os.startsWith("Windows")) {
            System.out.println("Windows");
            address = InetAddress.getLocalHost();
        } else {
            System.out.println("OS not supported");
        }

        return address;
    }

}
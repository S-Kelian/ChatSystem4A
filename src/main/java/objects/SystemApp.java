package objects;

import customExceptions.UserNotFoundException;
import customExceptions.UsernameEmptyException;
import customExceptions.UsernameUsedException;
import database.DbController;
import network.Observer;
import network.TCPSender;
import network.UDPSender;
import views.Chat;
import views.ChatRequest;

import javax.swing.*;
import java.io.IOException;
import java.net.*;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;

public class SystemApp {
    private final User me;
    private final UserList myUserList;
    private final UDPSender udpSender;
    private static SystemApp instance = null;

    private final DbController dbController = DbController.getInstance();

    private final HashMap<String, Chat> mapChat= new HashMap<>();

    private SystemApp() throws SocketException, UnknownHostException {
        InetAddress address = getMyIp();

        this.me = new User(address.toString(), address);
        System.out.println(me);
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
            if (type == UDPMessage.TYPEUDPMESSAGE.CHATANSWER && content.equals("RequestAccepted")){
                TCPSender tcpSender = new TCPSender();
                addObservers(tcpSender);
                try {
                    tcpSender.startConnection(address.toString(), 49002);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                myUserList.addOpenedChat(address, tcpSender);
                openChatView(myUserList.getUserByIp(address).getNickname(), false);
            }
            if (type == UDPMessage.TYPEUDPMESSAGE.STOPCHAT){
                myUserList.removeOpenedChat(address);
                stopChatView(myUserList.getUserByIp(address).getNickname());
            }
        } catch (IOException ignored) {
            System.out.println("Error while sending message");
        }
    }

    private void addObservers(TCPSender tcpSender) {

        Observer obsDB = (message) -> {
            try {
                dbController.insertMessage(message.getContent(), message.getSender().toString(), message.getReceiver().toString(), ((TCPMessage) message).getDate(), ((TCPMessage) message).getType());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
        tcpSender.addObserver(obsDB);
        Observer obsVue = (message) -> updateChatHistory((TCPMessage) message);
        tcpSender.addObserver(obsVue);
        Observer obsPrint = (message) -> System.out.println(" Send : "+message.toString());
        tcpSender.addObserver(obsPrint);
    }

    /**
     * Treatment of the received message
     * @param message received
     */
    public void receiveMessage(UDPMessage message) throws UserNotFoundException, UsernameUsedException, IOException {
        if (message.getSender().equals(me.getIp())) {
            return;
        }
        switch (message.getType()) {
            case REQUEST -> {
                String messageToSend = "update response from : " + me.getNickname();
                sendUnicast(messageToSend, message.getSender(), UDPMessage.TYPEUDPMESSAGE.RESPONSE);
            }
            case RESPONSE -> {
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
            }
            case DISCONNECTION -> myUserList.updateUserStatus(message.getSender(), 0);
            case RENAME -> setSomeoneUsername(message.getSender(), message.getContent().substring(18));
            case CHATREQUEST -> {
                if (myUserList.userIsInOpenedChats(message.getSender())) {
                    try {
                        udpSender.send(new UDPMessage("chat already opened", me.getIp(), message.getSender(), UDPMessage.TYPEUDPMESSAGE.CHATANSWER, false));
                    } catch (IOException ignored) {
                        // can potentially create a new exception for that
                    }
                } else {
                    String senderName = myUserList.getUserByIp(message.getSender()).getNickname();
                    ChatRequest chatRequest = new ChatRequest(senderName);
                    chatRequest.create();
                }
            }
            case CHATANSWER -> {
                if (message.getContent().equals("RequestAccepted")) {
                    TCPSender tcpSender = new TCPSender();
                    try {
                        tcpSender.startConnection(message.getSender().toString(), 49002);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    myUserList.addOpenedChat(message.getSender(), tcpSender);
                    String nicknameOfSender = myUserList.getUserByIp(message.getSender()).getNickname();
                    openChatView(nicknameOfSender, false);
                } else {
                    JOptionPane.showMessageDialog(null, message.getSender() + " refused your chat request.");
                }
            }
            case STOPCHAT -> {
                String nicknameOfSender = myUserList.getUserByIp(message.getSender()).getNickname();
                myUserList.getOpenedChats().get(message.getSender()).stopConnection();
                myUserList.removeOpenedChat(message.getSender());
                JOptionPane.showMessageDialog(null, nicknameOfSender + " stopped the chat. \n Mode history only activated.");
                mapChat.get(nicknameOfSender).updateMode(true);
                mapChat.remove(nicknameOfSender);
            }
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

    public void sendMessage(TCPMessage tcpMessage) {
        try {
            myUserList.getOpenedChats().get(tcpMessage.getReceiver()).sendMessage(tcpMessage);
            mapChat.get(myUserList.getUserByIp(tcpMessage.getReceiver()).getNickname()).makeChatHistory();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void openChatView(String nickname, boolean historyOnly) throws SocketException, UnknownHostException {
        Chat chat = new Chat(nickname, historyOnly);
        chat.create();
        mapChat.put(nickname, chat);
    }

    private void stopChatView(String nickname) {
        mapChat.get(nickname).getFrame().dispose();
        mapChat.remove(nickname);
    }

    public void updateChatHistory(TCPMessage message) {
        String nickname = myUserList.getUserByIp(message.getSender()).getNickname();
        mapChat.get(nickname).makeChatHistory();
    }
}
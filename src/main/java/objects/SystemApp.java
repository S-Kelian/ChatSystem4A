package objects;

import database.DbController;
import network.Observer;
import network.TCPSender;
import network.UDPSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.TYPEUDPMESSAGE;
import utils.customExceptions.OsNotSupportedException;
import utils.customExceptions.UserNotFoundException;
import utils.customExceptions.UsernameEmptyException;
import utils.customExceptions.UsernameUsedException;
import views.Chat;
import views.ChatRequest;

import javax.swing.*;
import java.io.IOException;
import java.net.*;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;

import static utils.TYPEUDPMESSAGE.*;

/**
 * SystemApp class
 * Main controller of the application
 */
public class SystemApp {

    /**
     * Logger of the class SystemApp
     */
    private static final Logger LOGGER = LogManager.getLogger(SystemApp.class);

    /**
     * User of the system
     */
    private final User me;

    /**
     * Userlist of the system
     */
    private final UserList myUserList;

    /**
     * UDPSender of the system
     */
    private final UDPSender udpSender;

    /**
     * Instance of the systemApp
     */
    private static SystemApp instance = null;

    /**
     * Instance of database controller
     */
    private final DbController dbController = DbController.getInstance();

    /**
     * Map of the chats opened
     */
    private final HashMap<String, Chat> mapChat= new HashMap<>();

    /**
     * Constructor of the systemApp
     */
    private SystemApp() throws SocketException, UnknownHostException {
        LOGGER.info("SystemApp started");
        InetAddress address = null;
        try {
            address = getMyIp();
        } catch (OsNotSupportedException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
        this.me = new User(address.toString(), address);
        this.udpSender = new UDPSender(me.getIp());
        myUserList = new UserList(me);

    }

    /**
     * Get the instance of the systemApp
     * @return the instance
     */
    public static SystemApp getInstance() throws SocketException, UnknownHostException {

        if (instance == null) {
            LOGGER.info("Creating new instance of SystemApp");
            instance = new SystemApp();
        }
        LOGGER.info("Returning instance of SystemApp");
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
            LOGGER.error("Nickname is empty");
            throw new UsernameEmptyException("Nickname is empty");
        }
        // Now that we know that the nickname us valid, we need to check if it is already taken
        if (myUserList.UserIsInListByNickmane(nickname)){
            LOGGER.error("Nickname already taken");
            throw new UsernameUsedException("Nickname already taken");
        }
        me.setNickname(nickname);
        myUserList.updateNickname(me.getIp(), me.getNickname());
        sendBroadcast("Nickname update : " + nickname, RENAME);
        LOGGER.info("Nickname updated");
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
            LOGGER.error("user not in list");
            throw new UserNotFoundException("error 404 user not found");
        }
        // if the user is in the list but with a different nickname, we update his nickname if it is not already taken
        if(!myUserList.UserIsInListByNickmane(newNn)){
            LOGGER.info("Nickname updated");
            myUserList.updateNickname(address, newNn);
            return;
        }
        // if the user is in the list with the same nickname, we do nothing
        LOGGER.error("Nickname already taken");
        throw new UsernameUsedException("Nickname already taken");
    }

    /**
     * Send a message to all users
     * @param content of the message
     */
    public void sendBroadcast(String content, TYPEUDPMESSAGE type) {
        try {
            UDPMessage message = new UDPMessage(content, me.getIp(), InetAddress.getByName("255.255.255.255"), type, true);
            udpSender.send(message);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Send a message to a specific user
     * @param content of the message
     * @param address of the receiver
     * @param type of the message
     */
    public void sendUnicast(String content, InetAddress address, TYPEUDPMESSAGE type) {
        try {
            LOGGER.info("Sending message : " + content + " to " + address.toString());
            UDPMessage message = new UDPMessage(content, me.getIp(), address, type, false);
            udpSender.send(message);
            if (type == CHATANSWER && content.equals("RequestAccepted")){
                TCPSender tcpSender = new TCPSender();
                addObservers(tcpSender);
                try {
                    tcpSender.startConnection(address.toString(), 49002);
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                    throw new RuntimeException(e);
                }
                myUserList.addOpenedChat(address, tcpSender);
                openChatView(myUserList.getUserByIp(address).getNickname(), false);
            }
            if (type == STOPCHAT){
                myUserList.removeOpenedChat(address);
                stopChatView(myUserList.getUserByIp(address).getNickname());
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Add observers to the TCPSender
     * @param tcpSender to which we add the observers
     */
    private void addObservers(TCPSender tcpSender) {
        LOGGER.info("Adding observers to TCPSender");
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
        LOGGER.info("Receiving message : " + message.toString());
        if (message.getSender().equals(me.getIp())) {
            return;
        }
        switch (message.getType()) {
            case REQUEST -> {
                String messageToSend = "update response from : " + me.getNickname();
                sendUnicast(messageToSend, message.getSender(), RESPONSE);
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
                        udpSender.send(new UDPMessage("chat already opened", me.getIp(), message.getSender(), CHATANSWER, false));
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage());
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
                        LOGGER.error(e.getMessage());
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
        LOGGER.info("Updating users list");
        String updateMessage = "update request";
        sendBroadcast(updateMessage, REQUEST);
    }

    /**
     * Disconnect the user from the chat
     */
    public void disconnect() {
        LOGGER.info("Disconnecting user");
        sendBroadcast("disconnect", DISCONNECTION);
        System.exit(0);
    }

    /**
     * Get the IP address of the user depending on the OS
     * @return the IP address
     * @throws SocketException if the socket is not valid
     * @throws UnknownHostException if the host is not valid
     * @throws OsNotSupportedException if the OS is not supported
     */
    public InetAddress getMyIp() throws SocketException, UnknownHostException, OsNotSupportedException {
        LOGGER.info("Getting IP address");
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
            LOGGER.error("OS not supported");
            throw new OsNotSupportedException("OS not supported");
        }
        return address;
    }

    /**
     * Send a message to a specific user
     * @param tcpMessage to send
     */
    public void sendMessage(TCPMessage tcpMessage) {
        LOGGER.info("Sending message : " + tcpMessage.toString());
        try {
            myUserList.getOpenedChats().get(tcpMessage.getReceiver()).sendMessage(tcpMessage);
            mapChat.get(myUserList.getUserByIp(tcpMessage.getReceiver()).getNickname()).makeChatHistory();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Open a chat view
     * @param nickname of the user
     * @param historyOnly true if the chat is in history only mode, false otherwise
     */
    public void openChatView(String nickname, boolean historyOnly) throws SocketException, UnknownHostException {
        LOGGER.info("Opening chat view");
        Chat chat = new Chat(nickname, historyOnly);
        chat.create();
        mapChat.put(nickname, chat);
    }

    /**
     * Stop a chat view
     * @param nickname of the user
     */
    private void stopChatView(String nickname) {
        LOGGER.info("Closing chat view");
        mapChat.get(nickname).getFrame().dispose();
        mapChat.remove(nickname);
    }

    /**
     * Update the chat history
     * @param message received
     */
    public void updateChatHistory(TCPMessage message) {
        LOGGER.info("Updating chat history");
        String nickname = myUserList.getUserByIp(message.getSender()).getNickname();
        mapChat.get(nickname).makeChatHistory();
    }

}
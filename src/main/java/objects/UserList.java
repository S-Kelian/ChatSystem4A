package objects;

import network.TCPSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a list of users
 */
public class UserList {

    /**
     * Logger of the class UserList
     */
  private static final Logger LOGGER = LogManager.getLogger(UserList.class);

    /**
     * List of users connected
     */
  private final ArrayList<User> contacts;

    /**
     * List of opened chats
     */
  private final Map<InetAddress, TCPSender> openedChats = new HashMap<>();

  /**
   * Constructor
   * @param me the user
   */
  public UserList(User me){
    contacts = new ArrayList<>();
    contacts.add(me);
  }

  /**
   * Return the list of users
   * @return the list of users
   */
  public ArrayList<User> getUserList(){
    return contacts;
  }

    /**
     * Add a user to the list
     * @param someone the user to add
     */
  public synchronized void addUser(User someone){
    LOGGER.info("Adding user " + someone.getNickname() + " to the user list");
    contacts.add(someone);
  }

  /**
   * Add a user to the list of opened chats
   * @param someone the user to add
   */
    public synchronized void addOpenedChat(InetAddress someone, TCPSender tcpSender){
      LOGGER.info("Adding user " + someone + " to the list of opened chats");
      openedChats.put(someone, tcpSender);
    }

  /**
   * Update the status of a user
   * @param someonesAddress the ip address of the user to update
   * @param status the new status of the user
   */
  public synchronized void updateUserStatus(InetAddress someonesAddress, int status){
    User someone = getUserByIp(someonesAddress);
    if (someone != null){
      someone.setStatus(status);
    } else {
      LOGGER.error("User " + someonesAddress + " not found in the list");
    }
  }

    /**
     * Check if a user is in the list
     * @param nickname the nickname of the user to check
     * @return true if the user is in the list, false otherwise
     */
  public synchronized boolean userIsInListByNickmane(String nickname) {
        for (User user : contacts) {
            if (user.getNickname().equals(nickname)) {
                return true;
            }
        }
        return false;
      }

  /**
   * Check if a user is in the list
   * @param ipAddress the ip address of the user to check
   * @return true if the user is in the list, false otherwise
   */
  public synchronized boolean userIsInListByIp(InetAddress ipAddress) {
    for (User user : contacts) {
        if (user.getIp().equals(ipAddress)) {
            return true;
        }
    }
    return false;
  }

    /**
     * Update the nickname of a user
     * @param someonesAddress the ip address of the user to update
     * @param newNickname the new nickname of the user
     * @return 1 if the user is not in the list, 2 if the nickname is already taken, 0 if the nickname is successfully changed
     */
  public synchronized int updateNickname(InetAddress someonesAddress, String newNickname){
    User someone = getUserByIp(someonesAddress);
    if (someone == null){
      return 1; // return code 1 is used when the user is not in the list
    }
    if (userIsInListByNickmane(newNickname)){
      return 2; // return code 2 is used when the nickname is already taken
    }
    someone.setNickname(newNickname);
    return 0; // return code 0 is used when the nickname is successfully changed
  }

  /**
   * Get a user by his ip address
   * @param ipAddress the ip address of the user to get
   * @return the user with the given ip address, null if the user is not in the list
   */
  public synchronized User getUserByIp(InetAddress ipAddress){
    for (User user : contacts) {
      if (user.getIp().equals(ipAddress)) {
          return user;
      }
  }
  return null;
  }

    /**
     * Get a user by his nickname
     * @param nickname the nickname of the user to get
     * @return the user with the given nickname, null if the user is not in the list
     */
  public synchronized User getUserByNickname(String nickname){
    User matchingUser = null;
    for (User user : contacts){
      if (nickname.equals(user.getNickname())){
        if (matchingUser == null){
          matchingUser = user;
        } else{
          LOGGER.error("Multiple users with the same nickname");
          return matchingUser; // so far the problem of multiple users with same nickname is noticed but not resolved
        }
      }
    }
    return matchingUser;
  }

  /**
   * Get the list of users online
   * @return the list of users online
   */
  public synchronized ArrayList<User> getUsersOnline(){
    ArrayList<User> usersOnline = new ArrayList<>();
    for (User user : getUserList()) {
        if (user.getStatus() != 0) {
            usersOnline.add(user);
        }
    }
    return usersOnline;
  }

  /**
  * Check if a user is in the list of opened chats
  */
  public synchronized boolean userIsInOpenedChats(InetAddress someone) {
    for (InetAddress user : openedChats.keySet()) {
      if (user.equals(someone)) {
        return true;
      }
    }
    return false;
  }

  public Map<InetAddress, TCPSender> getOpenedChats() {
    return openedChats;
  }

  public void removeOpenedChat(InetAddress sender) {
    openedChats.remove(sender);
  }
}
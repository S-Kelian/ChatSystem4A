package objects;

import java.util.ArrayList;
import java.net.InetAddress;

public class UserList {

  private ArrayList<User> contacts;
  
  public UserList(User me){
    contacts = new ArrayList<User>();
    contacts.add(me);
  }

  public ArrayList<User> getUserList(){
    return contacts;
  }
  
  public void addUser(User someone){
    contacts.add(someone);
  }
  public void removeUser(User someone){
    contacts.remove(someone);
  }
  public void updateUserStatus(InetAddress someonesAdress, int status){
    User someone = getUserByIp(someonesAdress);
    if (someone != null){
      someone.setStatus(status);
    } else {
      System.out.println("User not found");
    }
  }
  public boolean UserIsInListByNickmane(String nickname) {
        for (User user : contacts) {
            if (user.getNickname().equals(nickname)) {
                return true;
            }
        }
        return false;
      }
  public boolean UserIsInListByIp(InetAddress ipAddress) {
    for (User user : contacts) {
        if (user.getIp().equals(ipAddress)) {
            return true;
        }
    }
    return false;
  }
  public int updateNickname(InetAddress someonesAdress, String newNickname){
    User someone = getUserByIp(someonesAdress);
    if (someone == null){
      return 1; // return code 1 is used when the user is not in the list
    }
    if (UserIsInListByNickmane(newNickname)){
      return 2; // return code 2 is used when the nickname is already taken
    }
    someone.setNickname(newNickname);
    return 0; // return code 0 is used when the nickname is successfully changed
  }
  public User getUserByIp(InetAddress ipAdress){
    for (User user : contacts) {
      if (user.getIp().equals(ipAdress)) {
          return user;
      }
  }
  return null;
  }
  public User getUserByNickname(String nickname){
    User matchingUser = null;
    for (User user : contacts){
      if (nickname == user.getNickname()){
        if (matchingUser == null){
          matchingUser = user;
        } else{
          System.out.println("Multiple users with the same nickname");
          return matchingUser; // so far the problem of multiple users with same nickname is noticed but not resolved
        }
      }
    }
    return matchingUser;
  }
  public ArrayList<User> getUsersOnline(){
    ArrayList<User> usersOnline = new ArrayList<>();
    for (User user : getUserList()) {
        if (user.getStatus() != 0) {
            usersOnline.add(user);
        }
    }
    return usersOnline;
  }
}
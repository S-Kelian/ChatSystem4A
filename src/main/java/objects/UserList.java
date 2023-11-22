package objects;

import java.util.ArrayList;
import java.net.InetAddress;

public class UserList {
  private ArrayList<User> contacts;
  
  public ArrayList<User> getContacts(){
    return contacts;
  }

  public User getUserByIP(InetAddress ipAdress){
    User matchingUser = null;
    for (User user : contacts){
      if (ipAdress == user.getIp()){
        if (matchingUser == null){
          matchingUser = user;
        } else{
          System.out.println("Multiple users with the same IP adress");
          return matchingUser; // so far the problem of multiple users with same ip is noticed but not resolved
        }
      }
    }
    return matchingUser;
  }
  public void addUser(User someone){
    contacts.add(someone);
  }
  public void updateUserStatus(InetAddress someonesAdress, int status){
    User someone = getUserByIP(someonesAdress);
    if (someone != null){
      someone.setStatus(status);
    } else {
      System.out.println("User not found");
    }
  }
  
}

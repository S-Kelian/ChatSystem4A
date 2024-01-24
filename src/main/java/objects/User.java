package objects;

import java.net.InetAddress;

/**
 * This class represents a user
 */
public class User{

    /**
     * Nickname of the user
     */
    private String nickname;

    /**
     * Ip of the user
     */
    private final InetAddress ip;

    /**
     * Status of the user (0 = offline, 1 = online, 2 = busy, 3 = away)
     */
    private int status = 1;

    /**
     * Constructor
     * @param nickname nickname of the user
     * @param ip ip of the user
     */
    public User(String nickname, InetAddress ip){
        this.nickname = nickname;
        this.ip = ip;
    }

    /**
     * Set the nickname of the user
     * @param nickname new nickname of the user
     */
    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    /**
     * Set the status of the user
     * @param status new status of the user (0 = offline, 1 = online, 2 = busy, 3 = away)
     */
    public void setStatus(int status){
        if (status < 0 || status > 3){
            throw new IllegalArgumentException("Status must be between 0 and 3");
        }
        this.status = status;
    }

    /**
     * Get the nickname of the user
     * @return nickname of the user
     */
    public int getStatus(){
        return this.status;
    }

    /**
     * Get the status of the user
     * @return status of the user
     */
    public String getNickname(){
        return this.nickname;
    }

    /**
     * Get the ip of the user
     * @return ip of the user
     */
    public InetAddress getIp(){
        return this.ip;
    }

    /**
     * Get the string representation of the user
     * @return string representation of the user
     */
    public String toString(){
        return "Nickname: " + this.nickname + " IP: " + this.ip + " Status: " + this.status;
    }
}
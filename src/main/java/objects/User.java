package objects;

import java.net.InetAddress;

public class User{
    
    private String nickname;
    private int port;
    private InetAddress ip;

    // 0 = offline, 1 = online, 2 = busy, 3 = away
    private int status = 1;

    public User(String nickname, InetAddress ip){
        this.nickname = nickname;
        this.ip = ip;
    }

    public User(InetAddress ip){
        this.ip = ip;
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }
    
    public void setPort(int port){
        this.port = port;
    }
    public void setStatus(int status){
        if (status < 0 || status > 3){
            throw new IllegalArgumentException("Status must be between 0 and 3");
        }
        this.status = status;
    }
    
    // Exceptions in case of null values are handled
    public int getPort() {
        if (this.port == 0){
            throw new NullPointerException("Port is null");
        }
        return this.port;
    }
    public int getStatus(){
        return this.status;
    }
    public String getNickname(){
        if (this.nickname == null){
            return "Unknown";
        }
        return this.nickname;
    }
    public InetAddress getIp(){
        if (this.ip == null){
            throw new NullPointerException("IP is null");
        }
        return this.ip;
    }
    
}
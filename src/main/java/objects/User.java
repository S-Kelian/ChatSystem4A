package objects;

import java.net.InetAddress;

public class User{
    
    private String nickname;
    private int port;
    private InetAddress ip;

    // 0 = offline, 1 = online, 2 = busy, 3 = away
    private int status = 0;

    public User(String nickname, InetAddress ip){
        this.nickname = nickname;
        this.ip = ip;
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public void setPort(int port){
        this.port = port;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public String getNickname(){
        return this.nickname;
    }

    public InetAddress getIp(){
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }
    public int getStatus(){
        return this.status;
    }
}
package objects;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class User{
    
    private String nickname;
    private int port;
    private InetAddress ip;

    private boolean status = false;

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

    public void connect(){
        this.status = true;
    }

    public void disconnect(){
        this.status = false;
    }

    public InetAddress getIp(){
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }
}
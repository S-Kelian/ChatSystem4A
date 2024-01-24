package network;

import objects.Message;

import java.net.SocketException;
import java.net.UnknownHostException;

public interface Observer {
    void handle(Message message) throws UnknownHostException, SocketException;
}



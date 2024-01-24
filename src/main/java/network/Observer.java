package network;

import objects.Message;

import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Observer interface
 */
public interface Observer {

    /**
     * Handle a message
     * @param message message to handle
     */
    void handle(Message message) throws UnknownHostException, SocketException;
}



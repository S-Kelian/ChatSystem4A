package Vues;

import objects.SystemApp;

import javax.swing.*;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Chat {

    SystemApp app = new SystemApp();

    public Chat() throws SocketException, UnknownHostException {
    }

    public void create(){
        System.out.println("Ouverture de la fenetre de chat");
        JFrame frame = new JFrame("Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);

        JPanel panel = new JPanel();
        JPanel panelNickname = new JPanel();
        JLabel label = new JLabel("Welcome " + app.getMe().getNickname());
    }
}

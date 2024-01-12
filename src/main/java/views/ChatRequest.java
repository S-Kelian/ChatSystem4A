package views;

import objects.SystemApp;
import objects.UDPMessage;

import javax.swing.*;
import java.net.SocketException;
import java.net.UnknownHostException;

import static objects.UDPMessage.TYPEUDPMESSAGE.CHATANSWER;

public class ChatRequest {

    SystemApp app = SystemApp.getInstance();
    String nameOfAsker;

    public ChatRequest(String user) throws SocketException, UnknownHostException {
        this.nameOfAsker = user;
    }

    public void create(){
        JFrame frame = new JFrame("Chat Request");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(300, 300);

        JPanel mainPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel botPanel = new JPanel();
        JLabel label = new JLabel(nameOfAsker + " wants to chat with you \n Do you accept?");
        JButton yesButton = new JButton("Yes");
        JButton noButton = new JButton("No");

        mainPanel.add(centerPanel);
        mainPanel.add(botPanel);
        centerPanel.add(label);
        botPanel.add(yesButton);
        botPanel.add(noButton);

        frame.add(mainPanel);

        yesButton.addActionListener(e -> {
            app.sendUnicast("RequestAccepted", app.getMyUserList().getUserByNickname(nameOfAsker).getIp(), CHATANSWER);
            frame.setVisible(false);
            frame.dispose();
        });

        noButton.addActionListener(e -> {
            app.sendUnicast("RequestRefused", app.getMyUserList().getUserByNickname(nameOfAsker).getIp(), CHATANSWER);
            frame.setVisible(false);
            frame.dispose();
        });
    }
}

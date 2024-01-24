package views;

import objects.SystemApp;

import javax.swing.*;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import static utils.TYPEUDPMESSAGE.CHATANSWER;


/**
 * This class represents a chat request window view
 */
public class ChatRequest {

    /**
     * Logger of the class ChatRequest
     */
    private final Logger LOGGER = Logger.getLogger(ChatRequest.class.getName());

    /**
     * SystemApp instance
     */
    SystemApp app = SystemApp.getInstance();

    /**
     * Name of the user who asked for a chat
     */
    String nameOfAsker;

    /**
     * Constructor
     * @param user name of the user who asked for a chat
     */
    public ChatRequest(String user) throws SocketException, UnknownHostException {
        this.nameOfAsker = user;
    }

    /**
     * Create the chat request window
     */
    public void create(){
        LOGGER.info("Creating chat request window with " + nameOfAsker);
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
            LOGGER.info("Chat request accepted");
            app.sendUnicast("RequestAccepted", app.getMyUserList().getUserByNickname(nameOfAsker).getIp(), CHATANSWER);
            frame.setVisible(false);
            frame.dispose();
        });

        noButton.addActionListener(e -> {
            LOGGER.info("Chat request refused");
            app.sendUnicast("RequestRefused", app.getMyUserList().getUserByNickname(nameOfAsker).getIp(), CHATANSWER);
            frame.setVisible(false);
            frame.dispose();
        });

        frame.setVisible(true);
    }
}

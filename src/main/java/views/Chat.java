package views;

import database.DbController;
import objects.SystemApp;
import objects.TCPMessage;
import objects.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static utils.TYPEUDPMESSAGE.*;

/**
 * This class represents a chat window view
 */
public class Chat {

    /**
     * Logger of the class Chat
     */
    private static final Logger LOGGER = LogManager.getLogger(Chat.class);

    /**
     * SystemApp instance
     */
    private final SystemApp app = SystemApp.getInstance();

    /**
     * DbController instance
     */
    private final DbController dbController = DbController.getInstance();

    /**
     * User with whom the chat is
     */
    private final User receiver;

    /**
     * True if the chat is in history mode, false in chat mode
     */
    private boolean historyOnly;

    /**
     * JTextArea for displaying messages
     */
    private JTextArea messageTextArea;

    /**
     * JTextField for writing messages
     */
    private JTextField messageField;

    /**
     * JFrame of the chat window
     */
    private JFrame frame;

    /**
     * Constructor
     * @param receiverName userName with whom the chat is
     * @param historyOnly true if the chat is in history mode, false in chat mode
     */
    public Chat(String receiverName, boolean historyOnly) throws SocketException, UnknownHostException {
        this.receiver = app.getMyUserList().getUserByNickname(receiverName);
        this.historyOnly = historyOnly;
    }

    /**
     * Create the chat window
     */
    public void create() {
        LOGGER.info("Creating chat window with " + receiver.getNickname());
        frame = new JFrame("Chat with " + receiver.getNickname());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel(historyOnly ? "Historic with " + receiver.getNickname() : "Chat with " + receiver.getNickname());
        JButton topButton = new JButton(historyOnly ? "Start chat" : "Stop chat");
        messageField = new JTextField();
        JButton sendButton = new JButton("Send");

        JButton closeButton;
        if (historyOnly) {
            closeButton = new JButton("Close");
            closeButton.addActionListener(e -> frame.dispose());
            topPanel.add(closeButton, BorderLayout.PAGE_END);
        }

        // JTextArea for displaying messages
        messageTextArea = new JTextArea();
        messageTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageTextArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Create the list of messages
        makeChatHistory();

        // Events
        topButton.addActionListener(e -> handleTopButton());
        sendButton.addActionListener(e -> handleSendButton(messageField.getText()));

        // Add components to panels
        topPanel.add(titleLabel, BorderLayout.LINE_START);
        topPanel.add(topButton, BorderLayout.CENTER);

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel sendPanel = new JPanel(new BorderLayout());
        sendPanel.add(messageField, BorderLayout.CENTER);
        sendPanel.add(sendButton, BorderLayout.LINE_END);

        // Add panels to main panel
        mainPanel.add(topPanel, BorderLayout.PAGE_START);
        mainPanel.add(messagePanel, BorderLayout.CENTER);
        mainPanel.add(sendPanel, BorderLayout.PAGE_END);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    /**
     * Handle the top button
     */
    private void handleTopButton() {
        if (historyOnly) {
            // Start chat with the user
            JOptionPane.showMessageDialog(null, "Return to the contact list, a new chat window will appear when " + receiver.getNickname() + " will accept the chat request");
            app.sendUnicast("chat request", receiver.getIp(), CHATREQUEST);
            frame.dispose();
        } else {
            // Stop chat with the user
            int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to stop the chat with " + receiver.getNickname() + " ?", "Stop chat", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                app.sendUnicast("stop chat", receiver.getIp(), STOPCHAT);
                frame.dispose();
            }
        }
    }

    /**
     * Handle the send button
     * @param message message to send
     */
    private void handleSendButton(String message) {
        if (!historyOnly && !message.isEmpty()) {
            LocalDateTime date = LocalDateTime.now();
            String formattedDate = String.format("%02d/%02d/%d %02d:%02d", date.getDayOfMonth(), date.getMonthValue(), date.getYear(), date.getHour(), date.getMinute());
            TCPMessage tcpMessage = new TCPMessage(message, app.getMe().getIp(), receiver.getIp(), formattedDate, 0);
            app.sendMessage(tcpMessage);
            messageField.setText(""); // Clear the message field after sending
        }
    }

    /**
     * Create the chat history
     */
    public void makeChatHistory() {
        messageTextArea.setText("");
        try {
            ArrayList<TCPMessage> messages = dbController.getMessagesOf(receiver.getIp());
            for (TCPMessage message : messages) {
                appendMessageToTextArea(message);
            }
        } catch (SQLException | UnknownHostException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Append a message to the JTextArea
     * @param message message to append
     */
    private void appendMessageToTextArea(TCPMessage message) {
        String formattedMessage = String.format("[%s]%n%s: %s%n%n", message.getDate(), app.getMyUserList().getUserByIp(message.getSender()).getNickname(), message.getContent());
        if (message.getSender().equals(app.getMe().getIp())) {
            formattedMessage = String.format("[%s]%n%s: %s%n%n", message.getDate(), "Me", message.getContent());
        }
        messageTextArea.append(formattedMessage);
    }

    /**
     * Update the chat history mode
     */
    public void updateMode (boolean historyOnly) {
        this.historyOnly = historyOnly;
        frame.dispose();
        create();
    }

    /**
     * Get the JFrame of the chat window
     * @return JFrame of the chat window
     */
    public JFrame getFrame() {
        return frame;
    }
}

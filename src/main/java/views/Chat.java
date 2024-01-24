package views;

import database.DbController;
import objects.SystemApp;
import objects.TCPMessage;
import objects.UDPMessage;
import objects.User;

import javax.swing.*;
import java.awt.*;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Chat {

    private final SystemApp app = SystemApp.getInstance();
    private final DbController dbController = DbController.getInstance();
    private final User receiver;
    private boolean historyOnly = false;
    private JTextArea messageTextArea;
    private JTextField messageField;
    private JFrame frame;

    public Chat(String receiverName, boolean historyOnly) throws SocketException, UnknownHostException {
        this.receiver = app.getMyUserList().getUserByNickname(receiverName);
        this.historyOnly = historyOnly;
    }

    public void create() {
        frame = new JFrame("Chat with " + receiver.getNickname());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel(historyOnly ? "Historic with " + receiver.getNickname() : "Chat with " + receiver.getNickname());
        JButton topButton = new JButton(historyOnly ? "Start chat" : "Stop chat");
        messageField = new JTextField();
        JButton sendButton = new JButton("Send");

        JButton closeButton = null;
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

    private void handleTopButton() {
        if (historyOnly) {
            // Start chat with the user
            JOptionPane.showMessageDialog(null, "Return to the contact list, a new chat window will appear when " + receiver.getNickname() + " will accept the chat request");
            app.sendUnicast("chat request", receiver.getIp(), UDPMessage.TYPEUDPMESSAGE.CHATREQUEST);
            frame.dispose();
        } else {
            // Stop chat with the user
            int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to stop the chat with " + receiver.getNickname() + " ?", "Stop chat", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                app.sendUnicast("stop chat", receiver.getIp(), UDPMessage.TYPEUDPMESSAGE.STOPCHAT);
                frame.dispose();
            }
        }
    }

    private void handleSendButton(String message) {
        if (!historyOnly && !message.isEmpty()) {
            LocalDateTime date = LocalDateTime.now();
            TCPMessage tcpMessage = new TCPMessage(message, app.getMe().getIp(), receiver.getIp(), date.toString(), 0);
            app.sendMessage(tcpMessage);
            messageField.setText(""); // Clear the message field after sending
        }
    }

    public void makeChatHistory() {
        messageTextArea.setText("");
        try {
            ArrayList<TCPMessage> messages = dbController.getMessagesOf(receiver.getIp());
            for (TCPMessage message : messages) {
                appendMessageToTextArea(message);
            }
        } catch (SQLException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendMessageToTextArea(TCPMessage message) {
        String formattedMessage = String.format("[%s]%n%s: %s%n%n", message.getDate(), app.getMyUserList().getUserByIp(message.getSender()).getNickname(), message.getContent());
        if (message.getSender().equals(app.getMe().getIp())) {
            formattedMessage = String.format("[%s]%n%s: %s%n%n", message.getDate(), "Me", message.getContent());
        }
        messageTextArea.append(formattedMessage);
    }

    public void updateMode (boolean historyOnly) {
        this.historyOnly = historyOnly;
        frame.dispose();
        create();
    }

    public JFrame getFrame() {
        return frame;
    }
}

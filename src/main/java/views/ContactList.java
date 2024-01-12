package views;

import customExceptions.UsernameEmptyException;
import customExceptions.UsernameUsedException;
import objects.SystemApp;

import javax.swing.*;
import java.awt.*;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ContactList {

    private final SystemApp app = SystemApp.getInstance();
    private final List<JLabel> usersOnline = new ArrayList<>();

    public ContactList() throws SocketException, UnknownHostException {
    }

    public void create() {
        JFrame frame = new JFrame("Contact List");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel();
        JLabel titleLabel = new JLabel("Welcome " + app.getMe().getNickname());
        JPanel panelUsersOnline = new JPanel(new BorderLayout());
        JLabel labelNumberUsersOnline = new JLabel("Users online: " + app.getMyUserList().getUsersOnline().size());
        JButton refresh = new JButton("Refresh");
        JPanel botPanel = new JPanel();
        JButton rename = new JButton("Rename");
        JButton disconnect = new JButton("Disconnect");

        // Set properties
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(labelNumberUsersOnline, BorderLayout.PAGE_END);

        panelUsersOnline.add(refresh, BorderLayout.PAGE_END);

        botPanel.add(rename);
        botPanel.add(disconnect);

        mainPanel.add(topPanel, BorderLayout.PAGE_START);
        mainPanel.add(panelUsersOnline, BorderLayout.CENTER);
        mainPanel.add(botPanel, BorderLayout.PAGE_END);
        frame.add(mainPanel);

        // Create the list of online users
        createPanelsOfUsers(panelUsersOnline);

        // Events
        refresh.addActionListener(e -> {
            // Refresh the list of online users
            updateOnlineUsersList(panelUsersOnline, labelNumberUsersOnline);
        });

        rename.addActionListener(e -> {
            // Rename the user
            renameUser();
        });

        disconnect.addActionListener(e -> {
            // Disconnect the user and close the chat system
            app.disconnect();
            frame.dispose();
        });

        // Making the window visible
        frame.setVisible(true);
    }

    private void updateOnlineUsersList(JPanel panelUsersOnline, JLabel labelUsersOnline) {
        app.usersListUpdateRoutine();
        usersOnline.clear();
        panelUsersOnline.removeAll();
        labelUsersOnline.setText("Users online: " + app.getMyUserList().getUsersOnline().size());
        panelUsersOnline.add(labelUsersOnline, BorderLayout.PAGE_START);
        panelUsersOnline.add(new JButton("Refresh"), BorderLayout.PAGE_END);
        createPanelsOfUsers(panelUsersOnline);
        panelUsersOnline.revalidate();
        panelUsersOnline.repaint();
    }

    private void renameUser() {
        app.usersListUpdateRoutine();
        String newNickname = JOptionPane.showInputDialog(null, "Enter your new nickname");
        try {
            app.setMyUsername(newNickname);
            // Update UI with new nickname
        } catch (UsernameEmptyException | UsernameUsedException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void createPanelsOfUsers(JPanel panelUsersOnline) {
        for (int i = 0; i < app.getMyUserList().getUsersOnline().size(); i++) {
            JPanel userPanel = new JPanel();
            JLabel userLabel = new JLabel(app.getMyUserList().getUsersOnline().get(i).getNickname());
            JButton connectButton = new JButton("Connect");
            JButton historyButton = new JButton("History");

            // Add "(you)" to the label of the current user and add an event to the button for other users
            if (app.getMyUserList().getUsersOnline().get(i).getNickname().equals(app.getMe().getNickname())) {
                userLabel.setText(userLabel.getText() + " (you)");
            } else {
                addEventListenersForOtherUsers(connectButton, historyButton, i);
            }
            userPanel.add(userLabel);
            userPanel.add(connectButton);
            userPanel.add(historyButton);
            panelUsersOnline.add(userPanel);
        }
    }

    private void addEventListenersForOtherUsers(JButton connectButton, JButton historyButton, int index) {
        connectButton.addActionListener(e -> {
            // Ask the user if they want to open a chat with the selected user
            int askResponse = ((int) (Math.random() * 10) % 2);
            if (askResponse == 0) {
                // Open a chat with the selected user
            } else if (askResponse == 1) {
                JOptionPane.showMessageDialog(null, "Refused to chat with you", "Refused", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        historyButton.addActionListener(e -> {
            // Open the history of the selected user
            String ip = app.getMyUserList().getUsersOnline().get(index).getIp().toString();
            try {
                Chat chat = new Chat(ip, true);
                chat.create();
            } catch (SocketException | UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}

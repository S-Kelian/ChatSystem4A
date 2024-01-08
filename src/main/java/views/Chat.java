package views;

import java.awt.BorderLayout;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import customExceptions.UsernameEmptyException;
import customExceptions.UsernameUsedException;
import objects.SystemApp;

public class Chat {

    // Instance of the application system
    private final SystemApp app = SystemApp.getInstance();

    // List of buttons representing online users
    private final List<JButton> usersOnline = new ArrayList<>();

    // Constructor
    public Chat() throws SocketException, UnknownHostException {
    }

    // Method to create and display the chat window
    public void create() {

        JFrame frame = new JFrame("Chat");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //set max size
        frame.setSize(500, 500);

        JPanel mainPanel = new JPanel();
        JPanel panelNickname = new JPanel();
        JLabel titleLabel = new JLabel("Welcome " + app.getMe().getNickname());
        JPanel panelUsersOnline = new JPanel();
        JLabel labelUsersOnline = new JLabel("Users online : " + app.getMyUserList().getUsersOnline().size());
        JButton refresh = new JButton("Refresh");
        JPanel profilePanel = new JPanel();
        JButton rename = new JButton("Rename");
        JButton disconnect = new JButton("Disconnect");

        // Set properties
        mainPanel.setLayout(new BorderLayout());
        panelUsersOnline.setLayout(new BorderLayout());

        //add components
        panelUsersOnline.add(labelUsersOnline , BorderLayout.PAGE_START);
        panelUsersOnline.add(refresh, BorderLayout.PAGE_END);
        panelNickname.add(titleLabel);
        profilePanel.add(rename);
        profilePanel.add(disconnect);

        mainPanel.add(panelNickname, BorderLayout.PAGE_START);
        mainPanel.add(panelUsersOnline, BorderLayout.LINE_END);
        mainPanel.add(profilePanel, BorderLayout.PAGE_END);
        frame.add(mainPanel);

        // Create the list of online users
        createButtonsOfUsers(panelUsersOnline);

        // Events
        refresh.addActionListener(e -> {
            // Refresh the list of online users
            app.usersListUpdateRoutine();
            usersOnline.clear();
            panelUsersOnline.removeAll();
            labelUsersOnline.setText("Users online : " + app.getMyUserList().getUsersOnline().size());
            panelUsersOnline.add(labelUsersOnline , BorderLayout.PAGE_START);
            panelUsersOnline.add(refresh, BorderLayout.PAGE_END);
            createButtonsOfUsers(panelUsersOnline);
            panelUsersOnline.revalidate();
            panelUsersOnline.repaint();
        });

        rename.addActionListener(e -> {
            // Rename the user
            app.usersListUpdateRoutine();
            String newNickname = JOptionPane.showInputDialog(frame, "Enter your new nickname");
            try {
                app.setMyUsername(newNickname);
                titleLabel.setText("Welcome " + app.getMe().getNickname());
                for (JButton userButton : usersOnline) {
                    if (userButton.getText().contains("(you)")) {
                        userButton.setText(app.getMe().getNickname() + " (you)");
                    }
                }
            } catch (UsernameEmptyException | UsernameUsedException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });

        disconnect.addActionListener(e -> {
            // Disconnect the user and close chat system
            app.disconnect();
            frame.dispose();
        });

        // Making the window visible
        frame.setVisible(true);
    }

    /**
     * Method to create user buttons and add them to the list and the panel
     */
    private void createButtonsOfUsers(JPanel panelUsersOnline) {

        JPanel usersOnlineList= new JPanel();
        usersOnlineList.setLayout(new BoxLayout(usersOnlineList, BoxLayout.Y_AXIS));
        for (int i = 0; i < app.getMyUserList().getUsersOnline().size(); i++) {
            JButton userButton = new JButton(app.getMyUserList().getUsersOnline().get(i).getNickname());

            // Add "(you)" to the label of the current user
            if (app.getMyUserList().getUsersOnline().get(i).getNickname().equals(app.getMe().getNickname())) {
                userButton.setText(userButton.getText() + " (you)");
            }

            // Add the label to the list and the panel
            usersOnline.add(userButton);
        }
        for (JButton userButton : usersOnline) {
            usersOnlineList.add(userButton);
        }
        panelUsersOnline.add(usersOnlineList, BorderLayout.CENTER);
    }
}

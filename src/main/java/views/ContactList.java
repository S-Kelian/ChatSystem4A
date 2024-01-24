package views;

import customExceptions.UsernameEmptyException;
import customExceptions.UsernameUsedException;
import objects.SystemApp;
import objects.UDPMessage;

import javax.swing.*;
import java.awt.*;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ContactList {

    private final SystemApp app = SystemApp.getInstance();
    private final DefaultListModel<String> usersListModel = new DefaultListModel<>();
    JPanel mainPanel;
    JPanel topPanel;
    JPanel panelUsersOnline;
    JPanel botPanel;
    private JButton refreshButton;
    private JButton renameButton;
    private JButton disconnectButton;

    public ContactList() throws SocketException, UnknownHostException {
    }

    public void create() {
        JFrame frame = new JFrame("Contact List");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1000, 1000);

        mainPanel = new JPanel(new BorderLayout());
        topPanel = createTopPanel();
        panelUsersOnline = createPanelUsersOnline();
        botPanel = createBotPanel();

        mainPanel.add(topPanel, BorderLayout.PAGE_START);
        mainPanel.add(new JScrollPane(panelUsersOnline), BorderLayout.CENTER);
        mainPanel.add(botPanel, BorderLayout.PAGE_END);
        frame.add(mainPanel);

        // Events
        refreshButton.addActionListener(e -> updateOnlineUsersList());
        renameButton.addActionListener(e -> renameUser());
        disconnectButton.addActionListener(e -> disconnectUser(frame));

        // Making the window visible
        frame.setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();

        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

        JLabel titleLabel = new JLabel("Welcome " + app.getMe().getNickname());
        JLabel labelNumberUsersOnline = new JLabel("Users online: " + app.getMyUserList().getUsersOnline().size());
        refreshButton = new JButton("Refresh");

        topPanel.add(titleLabel);
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(labelNumberUsersOnline);
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(refreshButton);

        return topPanel;
    }

    private JPanel createPanelUsersOnline() {
        JPanel panelUsersOnline = new JPanel(new GridLayout(0, 1));
        JList<String> usersList = new JList<>(usersListModel);
        usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersList.setLayoutOrientation(JList.VERTICAL);
        panelUsersOnline.add(usersList);

        // Create the list of online users
        createPanelsOfUsers(usersList);

        return panelUsersOnline;
    }

    private JPanel createBotPanel() {
        JPanel botPanel = new JPanel();
        renameButton = new JButton("Rename");
        disconnectButton = new JButton("Disconnect");

        botPanel.add(renameButton);
        botPanel.add(disconnectButton);

        return botPanel;
    }

    private void updateOnlineUsersList() {
        app.usersListUpdateRoutine();
        usersListModel.clear();
        app.getMyUserList().getUsersOnline().forEach(user -> {
            if (!user.getNickname().equals(app.getMe().getNickname()))
                usersListModel.addElement(user.getNickname());
        });
    }

    private void renameUser() {
        app.usersListUpdateRoutine();
        String newNickname = JOptionPane.showInputDialog(null, "Enter your new nickname");
        try {
            app.setMyUsername(newNickname);
            updateTopPanel();
        } catch (UsernameEmptyException | UsernameUsedException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void updateTopPanel() {
        JLabel titleLabel = (JLabel) topPanel.getComponent(0);
        titleLabel.setText("Welcome " + app.getMe().getNickname());
    }

    private void disconnectUser(JFrame frame) {
        app.disconnect();
        frame.dispose();
    }

    private void createPanelsOfUsers(JList<String> usersList) {
        usersList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JList<String> list = (JList<String>) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    if (index != -1) {
                        openChatWithUser(index);
                    }
                }
            }
        });
    }

    private void openChatWithUser(int index) {

        String selectedUserNickname = usersListModel.getElementAt(index);
        String[] options = {"Open History", "Start New Conversation"};
        int choice = JOptionPane.showOptionDialog(mainPanel,
                "Choose an action for " + selectedUserNickname,
                "Chat Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            openHistory(selectedUserNickname);
        } else if (choice == 1) {
            openNewConversation(selectedUserNickname);
        }
    }

    private void openHistory(String nickname) {
        try {
            Chat chat = new Chat(nickname, true);
            chat.create();
        } catch (SocketException | UnknownHostException e) {
            System.err.println(e.getMessage() + "User not found");
            e.printStackTrace();
        }
    }

    private void openNewConversation(String nickname) {
        app.getMyUserList().userIsInOpenedChats(app.getMyUserList().getUserByNickname(nickname).getIp());
        app.sendUnicast("chat request", app.getMyUserList().getUserByNickname(nickname).getIp(), UDPMessage.TYPEUDPMESSAGE.CHATREQUEST);
        JOptionPane.showMessageDialog(mainPanel, "Return to the contact list, a new chat window will appear when " + nickname + " will accept the chat request");
    }


}

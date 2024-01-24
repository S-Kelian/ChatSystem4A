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
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

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
        JLabel titleLabel = new JLabel("Welcome " + app.getMe().getNickname());
        JLabel labelNumberUsersOnline = new JLabel("Users online: " + app.getMyUserList().getUsersOnline().size());
        refreshButton = new JButton("Refresh");

        topPanel.add(titleLabel);
        topPanel.add(labelNumberUsersOnline);
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
            usersListModel.addElement(user.getNickname());
        });
    }

    private void renameUser() {
        app.usersListUpdateRoutine();
        String newNickname = JOptionPane.showInputDialog(null, "Enter your new nickname");
        try {
            app.setMyUsername(newNickname);
            topPanel.getComponent(0).setName("Welcome " + app.getMe().getNickname());
        } catch (UsernameEmptyException | UsernameUsedException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
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
            openHistory(index);
        } else if (choice == 1) {
            openNewConversation(index);
        }
    }

    private void openHistory(int index) {
        String ip = app.getMyUserList().getUsersOnline().get(index).getIp().toString();
        try {
            Chat chat = new Chat(ip, true);
            chat.create();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void openNewConversation(int index) {
        app.getMyUserList().userIsInOpenedChats(app.getMyUserList().getUsersOnline().get(index).getIp());
        app.sendUnicast("chat request", app.getMyUserList().getUsersOnline().get(index).getIp(), UDPMessage.TYPEUDPMESSAGE.CHATREQUEST);
        JOptionPane.showMessageDialog(mainPanel, "Return to the contact list, a new chat window will appear when " + app.getMyUserList().getUsersOnline().get(index).getNickname() + " will accept the chat request");
    }


}

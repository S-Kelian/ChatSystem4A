package views;

import objects.SystemApp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.customExceptions.UsernameEmptyException;
import utils.customExceptions.UsernameUsedException;

import javax.swing.*;
import java.awt.*;
import java.net.SocketException;
import java.net.UnknownHostException;

import static utils.TYPEUDPMESSAGE.CHATREQUEST;

/**
 * This class represents a contact list window view
 */
public class ContactList {

    /**
     * Logger of the class ContactList
     */
    private static final Logger LOGGER = LogManager.getLogger(ContactList.class);

    /**
     * SystemApp instance
     */
    private final SystemApp app = SystemApp.getInstance();

    /**
     * DefaultListModel of the users
     */
    private final DefaultListModel<String> usersListModel = new DefaultListModel<>();

    /**
     * JPanel of the main panel
     */
    JPanel mainPanel;

    /**
     * JPanel of the top panel
     */
    JPanel topPanel;

    /**
     * JPanel of the panel of online users
     */
    JPanel panelUsersOnline;

    /**
     * JPanel of the bottom panel
     */
    JPanel botPanel;

    /**
     * JButton of the refresh button
     */
    private JButton refreshButton;

    /**
     * JButton of the rename button
     */
    private JButton renameButton;

    /**
     * JButton of the disconnect button
     */
    private JButton disconnectButton;

    /**
     * Constructor of contact list view
     */
    public ContactList() throws SocketException, UnknownHostException {
    }

    /**
     * Create the view of the contact list
     */
    public void create() {
        LOGGER.info("Creating contact list window");
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

    /**
     * Create the top panel of the contact list
     * @return the top panel
     */
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

    /**
     * Create the panel of online users
     * @return the panel of online users
     */
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

    /**
     * Create the bottom panel of the contact list
     * @return the bottom panel
     */
    private JPanel createBotPanel() {
        JPanel botPanel = new JPanel();
        renameButton = new JButton("Rename");
        disconnectButton = new JButton("Disconnect");

        botPanel.add(renameButton);
        botPanel.add(disconnectButton);

        return botPanel;
    }

    /**
     * Update the list of online users
     */
    private void updateOnlineUsersList() {
        LOGGER.info("Updating online users list");
        app.usersListUpdateRoutine();
        usersListModel.clear();
        app.getMyUserList().getUsersOnline().forEach(user -> {
            if (!user.getNickname().equals(app.getMe().getNickname()))
                usersListModel.addElement(user.getNickname());
        });
    }

    /**
     * Rename the user
     */
    private void renameUser() {
        app.usersListUpdateRoutine();
        String newNickname = JOptionPane.showInputDialog(null, "Enter your new nickname");
        try {
            app.setMyUsername(newNickname);
            updateTopPanel();
        } catch (UsernameEmptyException | UsernameUsedException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    /**
     * Update the top panel of the contact list
     */
    private void updateTopPanel() {
        JLabel titleLabel = (JLabel) topPanel.getComponent(0);
        titleLabel.setText("Welcome " + app.getMe().getNickname());
        JLabel labelNumberUsersOnline = (JLabel) topPanel.getComponent(2);
        labelNumberUsersOnline.setText("Users online: " + app.getMyUserList().getUsersOnline().size());
    }

    /**
     * Disconnect the user
     * @param frame the frame of the contact list
     */
    private void disconnectUser(JFrame frame) {
        app.disconnect();
        frame.dispose();
    }

    /**
     * Create the panels of users
     * @param usersList the list of users
     */
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

    /**
     * Open the chat with the user
     * @param index the index of the user
     */
    private void openChatWithUser(int index) {
        LOGGER.info("Opening chat with user");
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

    /**
     * Open the history with the user
     * @param nickname the nickname of the user
     */
    private void openHistory(String nickname) {
        try {
            Chat chat = new Chat(nickname, true);
            chat.create();
        } catch (SocketException | UnknownHostException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Open a new conversation with the user
     * @param nickname the nickname of the user
     */
    private void openNewConversation(String nickname) {
        app.getMyUserList().userIsInOpenedChats(app.getMyUserList().getUserByNickname(nickname).getIp());
        app.sendUnicast("chat request", app.getMyUserList().getUserByNickname(nickname).getIp(), CHATREQUEST);
        JOptionPane.showMessageDialog(mainPanel, "Return to the contact list, a new chat window will appear when " + nickname + " will accept the chat request");
    }


}

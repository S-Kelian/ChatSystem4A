package views;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utils.customExceptions.UsernameEmptyException;
import utils.customExceptions.UsernameUsedException;
import objects.SystemApp;

/**
 * This class represents a log in window view
 */
public class LogIn {

    /**
     * Logger of the class LogIn
     */
    private final Logger LOGGER = Logger.getLogger(LogIn.class.getName());

    /**
     * SystemApp instance
     */
    SystemApp app = SystemApp.getInstance();

    /**
     * Constructor
     */
    public LogIn() throws SocketException, UnknownHostException {
    }

    /**
     * Create the log in window
     */
    public void create() {

        JFrame frame = new JFrame("Log In");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel();
        JLabel label = new JLabel("Enter your nickname");
        JTextField tf = new JTextField(15);
        JButton send = new JButton("Log In");

        inputPanel.add(label);
        inputPanel.add(tf);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(send);

        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);

        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogIn(tf, frame);
            }
        });

        tf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogIn(tf, frame);
            }
        });

        frame.setVisible(true);
    }

    /**
     * Handle the log in
     * @param tf the text field
     * @param frame the frame
     */
    private void handleLogIn(JTextField tf, JFrame frame) {
        app.usersListUpdateRoutine();
        String nickname = tf.getText();
        if (nickname.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a nickname");
            return;
        }
        try {
            app.setMyUsername(nickname);
            frame.setVisible(false);
            frame.dispose();
            ContactList contactList = new ContactList();
            contactList.create();
        } catch (UsernameEmptyException | UsernameUsedException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage());
        } catch (SocketException | UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
    }
}

package views;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import customExceptions.UsernameEmptyException;
import customExceptions.UsernameUsedException;
import objects.SystemApp;

public class LogIn {

    SystemApp app = SystemApp.getInstance();

    public LogIn() throws SocketException, UnknownHostException {
    }
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

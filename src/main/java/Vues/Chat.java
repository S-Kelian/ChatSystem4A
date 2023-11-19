package Vues;

import objects.SystemApp;

import javax.swing.*;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Chat {

    SystemApp app = SystemApp.getInstance();

    public Chat() throws SocketException, UnknownHostException {
    }

    public void create(){
        System.out.println("Ouverture de la fenetre de chat");
        JFrame frame = new JFrame("Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);

        JPanel panel = new JPanel();
        JPanel panelNickname = new JPanel();
        JLabel label = new JLabel("Welcome " + app.getMe().getNickname());

        panelNickname.add(label);
        panel.add(panelNickname);
        panel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        frame.add(panel);


        JPanel panelUsersOnline = new JPanel();
        // get users online list
        JLabel labelUsersOnline = new JLabel("Users online : " + app.getUsersOnline().size());
        panelUsersOnline.add(labelUsersOnline);
        panelUsersOnline.setAlignmentY(JPanel.CENTER_ALIGNMENT);
        JLabel[] usersOnline = new JLabel[app.getUsersOnline().size()];
        for (int i = 0; i < app.getUsersOnline().size(); i++) {
            usersOnline[i] = new JLabel(app.getUsersOnline().get(i).getNickname());
            if (app.getUsersOnline().get(i).getNickname().equals(app.getMe().getNickname())) {
                usersOnline[i].setText(usersOnline[i].getText() + " (you)");
            }
            panelUsersOnline.add(usersOnline[i]);
        }
        panelUsersOnline.setAlignmentX(JPanel.CENTER_ALIGNMENT);


        panel.add(panelUsersOnline);



        frame.setVisible(true);
    }
}

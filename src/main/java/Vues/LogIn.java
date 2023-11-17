package Vues;

import javax.swing.*;

public class LogIn {

    public static void create(){

        JFrame frame = new JFrame("LogIn");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);


        JPanel panelNickname = new JPanel();
        JLabel label = new JLabel("Enter your nickname");
        JTextField tf = new JTextField(10);
        JPanel
        JButton send = new JButton("Send");
        panelNickname.add(label);
        panelNickname.add(tf);
        panelNickname.add(send);
        frame.add(panelNickname);



        send.addActionListener(e -> {
            String text = tf.getText();
            System.out.println(text);
        });

        frame.setVisible(true);
    }

}

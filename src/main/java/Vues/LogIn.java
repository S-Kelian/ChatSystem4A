package Vues;

import javax.swing.*;

public class LogIn {

    public static void create(){

        JFrame frame = new JFrame("LogIn");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setVisible(true);

        JPanel panel = new JPanel();
        JLabel label = new JLabel("Enter your nickname");
        JTextField tf = new JTextField(10);
        JButton send = new JButton("Send");
        panel.add(label);
        frame.add(panel);

        send.addActionListener(e -> {
            String text = tf.getText();
            System.out.println(text);
        });

    }

}

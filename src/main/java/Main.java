import java.io.IOException;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import customExceptions.UserNotFoundException;
import customExceptions.UsernameUsedException;
import database.DbController;
import network.TCPListener;
import network.UDPListener;
import objects.SystemApp;
import objects.TCPMessage;
import objects.UDPMessage;
import objects.User;
import views.LogIn;

public class Main {
    public static void main(String[] args) throws IOException {

        System.out.println("Lancement de l'application");
        SystemApp app = SystemApp.getInstance();
        DbController dbController = DbController.getInstance();
        dbController.connect();
        app.usersListUpdateRoutine();
        UDPListener udpListener = new UDPListener();
        TCPListener tcpListener = new TCPListener(49002); // on en a tout le temps besoin donc autant le créer ici pour éviter d en avoir plusieurs à la fin
        
        udpListener.addObserver((message) -> System.out.println(message.toString()));
        udpListener.addObserver((message) -> {
            try {
                app.receiveMessage((UDPMessage) message);
            } catch (UserNotFoundException | UsernameUsedException | SocketException e) {
                System.err.println(e.getMessage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        tcpListener.addObserver((message) -> System.out.println(message.toString()));
        tcpListener.addObserver((message) -> {
            try {
                User sender = app.getMyUserList().getUserByIp(message.getSender());
                User receiver = app.getMyUserList().getUserByIp(message.getReceiver());
                dbController.insertMessage(message.getContent(), sender.getIp().toString(), receiver.getIp().toString(), ((TCPMessage) message).getDate(), ((TCPMessage) message).getType());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        tcpListener.addObserver((message)-> {
                app.updateChatHistory((TCPMessage) message);
        });

        udpListener.start();
        tcpListener.start();
        LogIn logIn = new LogIn();
        logIn.create();

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                app.usersListUpdateRoutine();
            }
        }, 0, 60000);
    }
}

import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import utils.customExceptions.UserNotFoundException;
import utils.customExceptions.UsernameUsedException;
import database.DbController;
import network.TCPListener;
import network.UDPListener;
import objects.SystemApp;
import objects.TCPMessage;
import objects.UDPMessage;
import objects.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import views.LogIn;

/**
 * This class represents the main class of the application
 */
public class Main {

    /**
     * Logger of the class Main
     */
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    /**
     * Main method
     * @param args arguments
     */
    public static void main(String[] args) throws IOException {

        LOGGER.info("Starting ChatSystem application");
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
            } catch (UserNotFoundException | UsernameUsedException | IOException e) {
                LOGGER.error(e.getMessage());
            }
        });

        tcpListener.addObserver((message) -> System.out.println(message.toString()));
        tcpListener.addObserver((message) -> {
            try {
                User sender = app.getMyUserList().getUserByIp(message.getSender());
                User receiver = app.getMyUserList().getUserByIp(message.getReceiver());
                dbController.insertMessage(message.getContent(), sender.getIp().toString(), receiver.getIp().toString(), ((TCPMessage) message).getDate(), ((TCPMessage) message).getType());
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
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
                LOGGER.info("Automatic users list update");
            }
        }, 0, 60000);
    }
}

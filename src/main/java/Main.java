import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import customExceptions.UserNotFoundException;
import customExceptions.UsernameUsedException;
import database.DbController;
import network.TCPListener;
import network.UDPListener;
import objects.SystemApp;
import views.LogIn;

public class Main {
    public static void main(String[] args) throws IOException {

        System.out.println("Lancement de l'application");
        SystemApp app = SystemApp.getInstance();
        DbController dbController = DbController.getInstance();
        dbController.connect();
        app.usersListUpdateRoutine();
        UDPListener udpListener = new UDPListener();
        TCPListener tcpListener = new TCPListener(49001); // on en a tout le temps besoin donc autant le créer ici pour éviter d en avoir plusieurs à la fin
        
        udpListener.addObserver((message) -> System.out.println(message.toString()));
        udpListener.addObserver((message) -> {
            try {
                app.receiveMessage(message);
            } catch (UserNotFoundException | UsernameUsedException e) {
                System.err.println(e.getMessage());
            }
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

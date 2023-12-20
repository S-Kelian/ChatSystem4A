import customExceptions.UserNotFoundException;
import customExceptions.UsernameUsedException;
import network.UDPListener;
import objects.SystemApp;
import views.LogIn;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) throws IOException {

        System.out.println("Lancement de l'application");
        SystemApp app = SystemApp.getInstance();
        app.usersListUpdateRoutine();
        UDPListener listener = new UDPListener();
        listener.addObserver((message) -> System.out.println(message.toString()));

        listener.addObserver((message) -> {
            try {
                app.receiveMessage(message);
            } catch (UserNotFoundException | UsernameUsedException e) {
                System.err.println(e.getMessage());
            }
        });
        listener.start();

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

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import UDP.UDPListener;
import Vues.LogIn;
import objects.SystemApp;

public class Main {
    public static void main(String[] args) throws IOException {
        Timer timer = new Timer();
        System.out.println("Lancement de l'application");
        SystemApp app = SystemApp.getInstance();
        app.usersListUpdateRoutine();
        UDPListener listener = new UDPListener();
        listener.start();
        // Delay to let the listener start
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LogIn logIn = new LogIn();
        logIn.create();
        /*
        switch (args[0]){
            case "s":
                UDPListener listener = new UDPListener();
                listener.start();
                break;
            case "c":
                int port = 4889;
                UDPSender sender = new UDPSender (InetAddress.getLocalHost(), port);
                String msg = "Connection from "+ InetAddress.getLocalHost() + " via le port " + port;
                sender.broadcast(msg, InetAddress.getByName("255.255.255.255"));
                break;
        }
        */
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                app.usersListUpdateRoutine();
            }
        }, 0, 60000);
    }
}

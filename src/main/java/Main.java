import Vues.LogIn;
import objects.SystemApp;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {


        SystemApp app = new SystemApp();
        app.start();
        System.out.println("Recupération de la contact list");

        //delay 1s
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Ouverture de la fenetre de connexion");
        LogIn.create();


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
    }

}

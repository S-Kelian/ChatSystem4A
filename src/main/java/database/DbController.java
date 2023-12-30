package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbController {
    private static DbController instance = null;
    private Connection conn = null;

 
    public void connect () {
        String url = "jdbc:sqlite:database.db";
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println("oopsie doopsie");
        } catch (ClassNotFoundException e) {
            System.out.println("doopsie oopsie");
            System.out.println(e.getMessage());
            // always catch the ClassNotFoundException for now reason is TBD (maybe dependency problem)
        }       
    }

    public void disconnect () {
        try {
            conn.close();
            System.out.println("Connection to SQLite has been closed.");
        } catch (SQLException e) {
        
        }
    }
    public static DbController getInstance() {
        if (instance == null) {
            instance = new DbController();
        }
        return instance;
    }
}

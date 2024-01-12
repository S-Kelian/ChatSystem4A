package database;

import objects.TCPMessage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;

public class DbController {
    private static DbController instance = null;
    private Connection conn = null;

 
    public void connect () {
        String url = "jdbc:sqlite:messages.db";
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
            String sql = "Create table if not exists messages (id int primary key, content varchar(255), sender varchar(255), receiver varchar(255), date varchar(255), type int)";
            Statement statement = conn.createStatement();
            statement.executeUpdate(sql);
            System.out.println("Table created");
        } catch (SQLException e) {
            System.out.println("oopsie doopsie");
            System.out.println(e.getMessage());
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

    public void insertMessage(String content, String sender, String receiver, String date, int type) throws SQLException {
        String sql = "INSERT INTO messages (content, sender, receiver, date, type) VALUES ('" + content + "', '" + sender + "', '" + receiver + "', '" + date + "', " + type + ")";
        Statement statement = conn.createStatement();
        statement.executeUpdate(sql);
    }

    public void createTable() throws SQLException {
        String sql = "Create table if not exists messages (id int primary key, content varchar(255), sender varchar(255), receiver varchar(255), date varchar(255), type int)";
        Statement statement = conn.createStatement();
        statement.executeUpdate(sql);
    }

    public ArrayList<TCPMessage> getMessagesOf(InetAddress ip) throws SQLException, UnknownHostException {
        String sql = "SELECT * FROM messages WHERE sender = '" + ip.toString() + "' OR receiver = '" + ip.toString() + "'";
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        ArrayList<TCPMessage> messages = new ArrayList<>();
        while (rs.next()) {
            InetAddress sender = InetAddress.getByName(rs.getString("sender"));
            InetAddress receiver = InetAddress.getByName(rs.getString("receiver"));
            messages.add(new TCPMessage(rs.getString("content"),sender, receiver, rs.getString("date"), rs.getInt("type")));
        }
        return messages;
    }
}

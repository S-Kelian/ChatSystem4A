package database;

import objects.TCPMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;

public class DbController {

    /**
     * Logger of the class DbController
     */
    private static final Logger LOGGER = LogManager.getLogger(DbController.class);

    /**
     * Instance of the database controller
     */
    private static DbController instance = null;

    /**
     * Connection to the database
     */
    private Connection conn = null;

    /**
     * Connect to the database
     */
    public void connect () {
        String url = "jdbc:sqlite:messages.db";
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
            String sql = "Create table if not exists messages (id int primary key, content varchar(255), sender varchar(255), receiver varchar(255), date varchar(255), type int)";
            Statement statement = conn.createStatement();
            statement.executeUpdate(sql);
            LOGGER.info("Database created");
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Disconnect from the database
     */
    public void disconnect () {
        try {
            conn.close();
            LOGGER.info("Database closed");
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Get the instance of the database controller
     * @return instance of the database controller
     */
    public static DbController getInstance() {
        if (instance == null) {
            instance = new DbController();
        }
        return instance;
    }

    /**
     * Insert a message in the database
     * @param content content of the message
     * @param sender sender of the message
     * @param receiver receiver of the message
     * @param date date of the message
     * @param type type of the message
     */
    public synchronized void insertMessage(String content, String sender, String receiver, String date, int type) throws SQLException {
        LOGGER.info("Inserting message : " + content + " from " + sender + " to " + receiver + " at " + date + " of type " + type + " in database");
        String sql = "INSERT INTO messages (content, sender, receiver, date, type) VALUES ('" + content + "', '" + sender + "', '" + receiver + "', '" + date + "', " + type + ")";
        Statement statement = conn.createStatement();
        statement.executeUpdate(sql);

        LOGGER.info("Message inserted");
    }

    /**
     * Get the messages with a user
     * @param ip ip of the user
     * @return list of messages with the user
     */
    public synchronized ArrayList<TCPMessage> getMessagesOf(InetAddress ip) throws SQLException, UnknownHostException {
        LOGGER.info("Getting messages of " + ip.toString() + " from database");
        String sql = "SELECT * FROM messages WHERE sender = '" + ip.toString() + "' OR receiver = '" + ip + "'";
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        ArrayList<TCPMessage> messages = new ArrayList<>();
        while (rs.next()) {
            InetAddress sender = InetAddress.getByName(rs.getString("sender").substring(1));
            InetAddress receiver = InetAddress.getByName(rs.getString("receiver").substring(1));
            messages.add(new TCPMessage(rs.getString("content"),sender, receiver, rs.getString("date"), rs.getInt("type")));
        }
        return messages;
    }
}

package objects;

import utils.customExceptions.OsNotSupportedException;
import utils.customExceptions.UsernameEmptyException;
import utils.customExceptions.UsernameUsedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class SystemAppTest {

    SystemApp appTest;
    InetAddress address;

    @BeforeEach
    void setUp() throws SocketException, UnknownHostException, OsNotSupportedException {
        appTest = SystemApp.getInstance();
        address = appTest.getMyIp();
    }

    @Test
    void getMyUserListTest() {
        UserList userListTest = appTest.getMyUserList();
        assertEquals(1, userListTest.getUserList().size());
    }

    @Test
    void setMyUsernameTest() throws UsernameEmptyException, UsernameUsedException {
        appTest.setMyUsername("test");
        assertEquals("test", appTest.getMe().getNickname());
        assertThrows(UsernameEmptyException.class, () -> appTest.setMyUsername(""));
    }

    @Test
    void getMeTest() throws UnknownHostException {
        assertEquals("test", appTest.getMe().getNickname());
    }

    /*
    @Test
    void setSomeoneUsername() {
    }

    @Test
    void sendBroadcast() {
    }

    @Test
    void sendUnicast() {
    }

    @Test
    void receiveMessage() {
    }

    @Test
    void usersListUpdateRoutine() {
    }

    @Test
    void disconnect() {
    }

    @Test
    void getMyIpTest() {
    }
    */
}
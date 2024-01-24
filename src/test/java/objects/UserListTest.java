package objects;

import network.TCPSender;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class UserListTest {

    UserList userListTest;

    UserListTest() throws UnknownHostException {
    }

    @BeforeEach
    void setUp() {
        userListTest = new UserList(new User("test", InetAddress.getLoopbackAddress()));
    }

    @Test
    void getUserList() {
        assertEquals(1, userListTest.getUserList().size());
    }

    @Test
    void addUser() throws UnknownHostException {
        assertEquals(1, userListTest.getUserList().size());
        userListTest.addUser(new User("User2", InetAddress.getLocalHost()));
        assertEquals(2, userListTest.getUserList().size());
    }

    @Test
    void addOpenedChat() {
        assertEquals(0, userListTest.getOpenedChats().size());
        TCPSender tcpSender = new TCPSender();
        userListTest.addOpenedChat(InetAddress.getLoopbackAddress(), tcpSender);
        assertEquals(1, userListTest.getOpenedChats().size());
    }

    @Test
    void updateUserStatus() {
        assertEquals(1, userListTest.getUserList().get(0).getStatus());
        userListTest.updateUserStatus(InetAddress.getLoopbackAddress(), 0);
        assertEquals(0, userListTest.getUserList().get(0).getStatus());
    }

    @Test
    void userIsInListByNickmane() {
        assertTrue(userListTest.userIsInListByNickmane("test"));
        assertFalse(userListTest.userIsInListByNickmane("test2"));
    }

    @Test
    void userIsInListByIp() throws UnknownHostException {
        assertTrue(userListTest.userIsInListByIp(InetAddress.getLoopbackAddress()));
        assertFalse(userListTest.userIsInListByIp(InetAddress.getLocalHost()));
    }

    @Test
    void updateNickname() {
        assertEquals("test", userListTest.getUserList().get(0).getNickname());
        userListTest.updateNickname(InetAddress.getLoopbackAddress(), "newTest");
        assertEquals("newTest", userListTest.getUserList().get(0).getNickname());
    }

    @Test
    void getUserByIp() {
        assertEquals("test", userListTest.getUserByIp(InetAddress.getLoopbackAddress()).getNickname());
    }

    @Test
    void getUserByNickname() {
        assertEquals("test", userListTest.getUserByNickname("test").getNickname());
    }

    @Test
    void getUsersOnline() {
        assertEquals(1, userListTest.getUsersOnline().size());
    }

    @Test
    void userIsInOpenedChats() {
        assertFalse(userListTest.userIsInOpenedChats(InetAddress.getLoopbackAddress()));
        userListTest.addOpenedChat(InetAddress.getLoopbackAddress(), new TCPSender());
        assertTrue(userListTest.userIsInOpenedChats(InetAddress.getLoopbackAddress()));
    }

    @Test
    void getOpenedChats() {
        assertEquals(0, userListTest.getOpenedChats().size());
    }

    @Test
    void removeOpenedChat() {
        userListTest.addOpenedChat(InetAddress.getLoopbackAddress(), new TCPSender());
        assertEquals(1, userListTest.getOpenedChats().size());
        userListTest.removeOpenedChat(InetAddress.getLoopbackAddress());
        assertEquals(0, userListTest.getOpenedChats().size());
    }
}
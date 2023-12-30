package objects;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    User userTest = new User("test", InetAddress.getByName("127.0.0.1"));

    UserTest() throws UnknownHostException {
    }

    @Test
    void getNicknameTest() {
        assertEquals("test", userTest.getNickname());
    }

    @Test
    void setNicknameTest() {
        assertEquals("test", userTest.getNickname());
        userTest.setNickname("newTest");
        assertEquals("newTest", userTest.getNickname());
    }

    @Test
    void getStatusTest() {
        assertEquals(1, userTest.getStatus());
    }

    @Test
    void setStatusTest() {
        assertEquals(1, userTest.getStatus());
        userTest.setStatus(0);
        assertEquals(0, userTest.getStatus());
        assertThrows(IllegalArgumentException.class, () -> userTest.setStatus(-1));
    }

    @Test
    void setPortTest() {
        userTest.setPort(1234);
        assertEquals(1234, userTest.getPort());
    }

    @Test
    void getIpTest() {
        assertEquals("/127.0.0.1", userTest.getIp().toString());
    }

}
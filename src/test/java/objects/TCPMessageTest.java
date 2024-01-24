package objects;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class TCPMessageTest {

    TCPMessage msgTest = new TCPMessage("test", InetAddress.getLocalHost(), InetAddress.getLocalHost(), "date", 0);

    TCPMessageTest() throws UnknownHostException {
    }

    @Test
    void getContent() {
        assertEquals("test", msgTest.getContent());
    }

    @Test
    void getReceiver() throws UnknownHostException {
        assertEquals(msgTest.getReceiver(),InetAddress.getLocalHost());
    }

    @Test
    void getSender() throws UnknownHostException {
        assertEquals(msgTest.getSender(),InetAddress.getLocalHost());
    }

    @Test
    void getDate() {
        assertEquals("date", msgTest.getDate());
    }

    @Test
    void getType() {
        assertEquals(0, msgTest.getType());
    }

    @Test
    void testToString() {
        assertEquals("TCPMessage{content='test', sender=" + msgTest.getSender() + ", receiver=" + msgTest.getReceiver() + ", date=" + msgTest.getDate() + ", type=" + msgTest.getType() + "}", msgTest.toString());
    }
}
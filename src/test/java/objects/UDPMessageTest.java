package objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class UDPMessageTest {
    UDPMessage msgTest = new UDPMessage("test", InetAddress.getLocalHost(), InetAddress.getLocalHost(), UDPMessage.TYPEUDPMESSAGE.RESPONSE, false);
    UDPMessage msgTest2 = new UDPMessage("test2", InetAddress.getLocalHost(), InetAddress.getLocalHost(), UDPMessage.TYPEUDPMESSAGE.REQUEST, true);

    UDPMessageTest() throws UnknownHostException {
    }

    @Test
    void getContentTest() {
        assertEquals("test", msgTest.getContent());
        assertEquals("test2", msgTest2.getContent());
    }

    @Test
    void getSenderTest() throws UnknownHostException {
        assertEquals(InetAddress.getLocalHost(), msgTest.getSender());
        assertEquals(InetAddress.getLocalHost(), msgTest2.getSender());
    }

    @Test
    void getReceiverTest() throws UnknownHostException {
        assertEquals(InetAddress.getLocalHost(), msgTest.getReceiver());
        assertEquals(InetAddress.getLocalHost(), msgTest2.getReceiver());
    }

    @Test
    void isBroadcastTest() {
        assertFalse(msgTest.isBroadcast());
        assertTrue(msgTest2.isBroadcast());
    }

    @Test
    void getTypeTest() {
        assertEquals(UDPMessage.TYPEUDPMESSAGE.RESPONSE, msgTest.getType());
        assertEquals(UDPMessage.TYPEUDPMESSAGE.REQUEST, msgTest2.getType());
    }

    @Test
    void testToStringTest() {
        assertEquals("UDPMessage{content='test', type='RESPONSE', sender=" + msgTest.getSender() + ", receiver=" + msgTest.getReceiver() + ", broadcast=false}", msgTest.toString());
        assertEquals("UDPMessage{content='test2', type='REQUEST', sender=" + msgTest2.getSender() + ", receiver=" + msgTest2.getReceiver() + ", broadcast=true}", msgTest2.toString());
    }
}
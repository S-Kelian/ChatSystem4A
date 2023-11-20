package objects;

import org.junit.jupiter.api.Test;

import java.net.SocketException;
import java.net.UnknownHostException;

public class SystemAppTest {

    SystemApp appTest = SystemApp.getInstance();


    public SystemAppTest() throws SocketException, UnknownHostException {

    }

    @Test
    public void testAddUserOnline(){
        User user = new User("test", appTest.getMe().getIp());
        appTest.addUserOnline(user);
        assert appTest.getUsersOnline().contains(user);
    }

    @Test
    public void testRemoveUserOnline(){
        User user = new User("test", appTest.getMe().getIp());
        appTest.addUserOnline(user);
        appTest.removeUserOnline(user);
        assert !appTest.getUsersOnline().contains(user);
    }

    @Test
    public void testCheckUsersOnlineByName(){
        User user = new User("test", appTest.getMe().getIp());
        appTest.addUserOnline(user);
        assert appTest.checkUsersOnlineByName("test");
    }
}

package objects;

import org.junit.jupiter.api.Test;

import java.net.SocketException;
import java.net.UnknownHostException;

public class SystemAppTest {

    SystemApp appTest = SystemApp.getInstance();


    public SystemAppTest() throws SocketException, UnknownHostException {

    }

    @Test
    public void testSetMyNickname(){
        assert appTest.setMyUsername("test0");
    }

    @Test
    public void testAddUser(){
        User user = new User("test1", appTest.getMe().getIp());
        appTest.addUser(user);
        assert appTest.getUsersOnline().contains(user);
    }

    @Test
    public void testRemoveUserOnline(){
        User user = new User("test2", appTest.getMe().getIp());
        appTest.addUser(user);
        appTest.removeUserOnline(user);
        assert !appTest.getUsersOnline().contains(user);
    }

    @Test
    public void testGetUserByName(){
        User user = new User("test3", appTest.getMe().getIp());
        appTest.addUser(user);
        assert appTest.getUserByName("test3").equals(user);
    }
}

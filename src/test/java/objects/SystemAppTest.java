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
        assert appTest.setMyUsername("test0") == 0;
    }

    @Test
    public void testAddUser(){
        User user = new User("test1", appTest.getMe().getIp());
        appTest.getMyUserList().addUser(user);
        assert appTest.getMyUserList().getUsersOnline().contains(user);
    }

    @Test
    public void testSetUserOffline(){
        User user = new User("test2", appTest.getMe().getIp());
        appTest.getMyUserList().addUser(user);
        appTest.setUserOffline(user);
        assert !appTest.getMyUserList().getUsersOnline().contains(user);
    }

    @Test
    public void testGetUserByName(){
        User user = new User("test3", appTest.getMe().getIp());
        appTest.getMyUserList().addUser(user);
        assert appTest.getMyUserList().UserIsInListByNickmane("test3") == true;
    }

}

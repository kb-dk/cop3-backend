package dk.kb.cop3.backend.crud.test;

import dk.kb.cop3.backend.crud.database.UserDao;
import dk.kb.cop3.backend.crud.database.hibernate.User;
import dk.kb.cop3.backend.crud.database.hibernate.UserPermissions;
import dk.kb.cop3.backend.crud.database.hibernate.UserRole;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.util.Set;

/**
 * Unit test class for the UserDao
 */
public class UserDaoTest {

    private static Logger LOGGER = Logger.getLogger(UserDaoTest.class);

    @Autowired
    private UserDao userDao;

    /**
     * Test that a new user can be saved to the database
    */
    @Test
    public void testCreateUser() {

        String userId = userDao.addUser(getTestUser());
        Assert.notNull(userId);
        LOGGER.debug("User ID = " + userId);
    }

    /**
     * Test that a user can be retrieved from the database
     */
    @Test
    public void testGetUserByPid(){

        User user = userDao.getUser("PID000999999");
        Assert.notNull(user);
        Assert.isTrue(user.getCommonName().equals("John"));

        Set<UserPermissions> userPermissions = user.getRole().getPermissions();
        Assert.isTrue(userPermissions.size() == 9);
        for (UserPermissions userPermission : userPermissions) {
            System.out.println("userPermission.toString() = " + userPermission.toString());
        }
    }

    /**
     * Test that a user retrieved from the database can be modified and the change saved to the database
    */
    @Test
    public void testModifyUser() {

        User user = userDao.getUser("PID000999999");
        UserRole userRole = new UserRole();
        userRole.setRoleId(3);
        userRole.setRoleName("GUEST_USER");
        user.setRole(userRole);
        userDao.modifyUser(user);
        user = userDao.getUser("PID000999999");
        System.out.println("user.getRole() = " + user.getRole());
        Assert.isTrue(user.getRole().getRoleId() == 3);
    }

    /**
     * Test that a user can be deleted from the database
    */
    @Test
    public void testDeleteUser() {

        User user = userDao.getUser("PID000999999");
        userDao.deleteUser(user);
       // Assert.isTrue((userDao.getUser("PID000999999"));


    }

    private User getTestUser() {

        User user = new User();
        user.setId("08011981-3175");
        user.setPid("PID000999999");
        user.setCommonName("John");
        user.setGivenName("Joseph");
        user.setSurName("Doherty");
        user.setEmail("jjdo@kb.dk");
        user.setRoleId(1);
        UserRole userRole = new UserRole();
        userRole.setRoleId(1);
        userRole.setRoleName("KB_USER");
        user.setRole(userRole);

        user.setLastActive(new Timestamp(System.currentTimeMillis()));

        return user;
    }
}

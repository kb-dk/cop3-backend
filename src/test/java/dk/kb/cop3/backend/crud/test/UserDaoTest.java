package dk.kb.cop3.backend.crud.test;

import dk.kb.cop3.backend.crud.database.UserDaoImpl;
import dk.kb.cop3.backend.crud.database.hibernate.User;
import dk.kb.cop3.backend.crud.database.hibernate.UserPermissions;
import dk.kb.cop3.backend.crud.database.hibernate.UserRole;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.util.Set;

/**
 * Unit test class for the UserDao
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserDaoTest {

    private static Logger LOGGER = Logger.getLogger(UserDaoTest.class);
    private static UserDaoImpl userDao;

    @BeforeClass
    public static void initDao(){
        userDao = new UserDaoImpl();
    }



    /**
     * Test that a new user can be saved to the database
    */
    @Test
    public void test1CreateUser() {

        String userId = userDao.addUser(getTestUser());
        Assert.notNull(userId);
        LOGGER.debug("User ID = " + userId);
    }

    /**
     * Test that a user can be retrieved from the database
     */
    @Test
    public void test2GetUserByPid(){

        User user = userDao.getUser("PID000999999");
        Assert.notNull(user);
        Assert.isTrue(user.getCommonName().equals("John"));

        final UserRole role = user.getRole();
        Set<UserPermissions> userPermissions = role.getPermissions();
        Assert.isTrue(userPermissions.size() == 9);
        for (UserPermissions userPermission : userPermissions) {
            System.out.println("userPermission.toString() = " + userPermission.toString());
        }
    }

    /**
     * Test that a user retrieved from the database can be modified and the change saved to the database
    */
    @Test
    public void test3ModifyUser() {

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
    public void test4DeleteUser() {

        User user = userDao.getUser("PID000999999");
        userDao.deleteUser(user);
       // Assert.isTrue((userDao.getUser("PID000999999"));


    }

    public static User getTestUser() {

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

package dk.kb.cop3.backend.crud.test;

/**
 * kb.dk
 *
 * @author jatr
 *         Date: 22/12/11
 *         Time: 15:02
 */

import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.crud.database.UserRoleDao;
import dk.kb.cop3.backend.crud.database.UserRoleDaoImpl;
import dk.kb.cop3.backend.crud.database.hibernate.UserRole;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Unit test class for the UserRoleDao
 */
@Transactional
public class UserRoleDaoTest {

    static final int KB_USER_ROLE_ID = 1;

    private static Logger LOGGER = Logger.getLogger(UserDaoTest.class);

    private static UserRoleDao userRoleDao;

    @BeforeClass
    public static void initTest() throws FileNotFoundException {
        CopBackendProperties.initialize(new FileInputStream("src/test/resources/cop_config.xml"));
        userRoleDao = new UserRoleDaoImpl();
    }

    @Test
    public void testGetUserRole() {
        UserRole userRole = userRoleDao.findUserRoleById(KB_USER_ROLE_ID);
        Assert.notNull(userRole);
        Assert.notEmpty(userRole.getPermissions());
    }
}

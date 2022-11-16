package dk.kb.cop3.backend.crud.test;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.constants.DSFLAreas;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.hibernate.User;
import dk.kb.cop3.backend.crud.services.UserProvisioningService;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;

public class UserProvisioningServiceTest {

    private static final String testUserPid = "PID123";
    Session session;
    Transaction transaction;

    UserProvisioningService userProvisioningService;

    @BeforeClass
    public static void initilizeConstants() throws FileNotFoundException {
        CopBackendProperties.initialize(new FileInputStream("src/test/resources/cop_config.xml"));
    }

    @Before
    public void init() {
        session = HibernateUtil.getSessionFactory().openSession();
        userProvisioningService = new UserProvisioningService(session);
        transaction = session.beginTransaction();
        deleteTestUserIfExists();
    }

    @After
    public void shutdown() {
        deleteTestUserIfExists();
        transaction.commit();
        session.close();
    }

    private void deleteTestUserIfExists() {
        User testUser = session.get(User.class,testUserPid);
        if (testUser != null) {
            session.delete(session.get(User.class, testUserPid));
        }
    }

    @Test
    public void testCreateUser() throws JSONException {
        JSONObject jsonObject = createTestUser();
        Assert.assertEquals(testUserPid,jsonObject.get("pid"));
        Assert.assertEquals("1234567890",jsonObject.get("id"));
        Assert.assertEquals("Anders",jsonObject.get("givenName"));
        Assert.assertEquals("And",jsonObject.get("surName"));
        Assert.assertEquals("Anders And",jsonObject.get("commonName"));
        // try to create user again to see that it does not fail by trying to create more users with same pid
        jsonObject = createTestUser();
        Assert.assertEquals(testUserPid,jsonObject.get("pid"));
        Assert.assertEquals("1234567890",jsonObject.get("id"));
        Assert.assertEquals("Anders",jsonObject.get("givenName"));
        Assert.assertEquals("And",jsonObject.get("surName"));
        Assert.assertEquals("Anders And",jsonObject.get("commonName"));
    }

    private JSONObject createTestUser() throws JSONException {
        String userJson = userProvisioningService.fetchOrCreateUserReturnUserJson(testUserPid,"1234567890","Anders","And","Anders And");
        JSONObject jsonObject = new JSONObject(userJson);
        return jsonObject;
    }

    @Test
    public void testGetUserName() throws JSONException {
        createTestUser();
        Assert.assertEquals("Anders And",userProvisioningService.getUserNameByPid(testUserPid));
    }

    @Test
    public void testAddToScore() throws JSONException {
        createTestUser();
        userProvisioningService.updateScoreInArea(testUserPid,"3", DSFLAreas.Fyn);
        User modifiedUser = session.get(User.class,testUserPid);
        Assert.assertNotNull(modifiedUser);
        Assert.assertEquals(BigInteger.valueOf(3),modifiedUser.getUserScore());
        Assert.assertEquals(BigInteger.valueOf(3),modifiedUser.getUserScore1());
    }
}

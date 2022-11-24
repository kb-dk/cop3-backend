package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.crud.util.TestUtil;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by IntelliJ IDEA.
 * User: dgj
 * Date: 04-08-11
 * Time: 13:13
 * To change this template use File | Settings | File Templates.
 */
public class HibernateMetadataSourceTest{
    private static Logger logger = Logger.getLogger(HibernateMetadataSourceTest.class);
    private static Session session;
    private static HibernateMetadataSource mds;


    @BeforeAll
    public static void initTest() throws FileNotFoundException {
        CopBackendProperties.initialize(new FileInputStream("src/test/resources/cop_config.xml"));
        session = TestUtil.openDatabaseSession().getSession();
        mds = new HibernateMetadataSource(session);
    }

    @Test
    public void testSearch() {
        HibernateMetadataWriter metadataWriter = new HibernateMetadataWriter(session);
        String testMods = TestUtil.getTestMods();
        TestUtil.createAndSaveDefaultTestCobject(TestUtil.TEST_ID, metadataWriter, session);
        mds = new HibernateMetadataSource(session);
        mds.setSearchterms("id",TestUtil.TEST_ID);
        mds.execute();
        final Long numberOfHits = mds.getNumberOfHits();
        assertTrue(numberOfHits == 1);
        final Object cobject = mds.getAnother();
        assertEquals(TestUtil.TEST_ID,cobject.getId());
        TestUtil.deleteFromDatabase(Object.class,TestUtil.TEST_ID, session);
    }


    @AfterAll
    public static void close() {
        session.close();
    }

}

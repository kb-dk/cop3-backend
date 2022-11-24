package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.crud.database.hibernate.Type;
import dk.kb.cop3.backend.crud.util.TestUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.math.BigDecimal;

public class SolrMetadataSourceTest {

    private static Session session;

    @BeforeEach
    public void initTest() throws FileNotFoundException {
         session = TestUtil.openDatabaseSession();
         createAndSaveTestData();
     }

     @AfterEach
     public void deleteTestData() {
         TestUtil.deleteFromDatabase(Object.class, "test1234", session);
         TestUtil.deleteFromDatabase(Edition.class, "edition1", session);
         TestUtil.deleteFromDatabase(Type.class, BigDecimal.TEN, session);
         TestUtil.closeDatabaseSession(session);
     }



    @Test
     public void testGetSingleObject() {
         SolrMetadataSource mds = new SolrMetadataSource(session);
         mds.setSearchterms("id","test1234");
         mds.execute();
         assertEquals(Long.valueOf("1"),mds.getNumberOfHits());
         Object fetchedObject = mds.getAnother();
         assertEquals("test1234",fetchedObject.getId());
     }

     @Test
     public void testGetNonexistingObject() {
         SolrMetadataSource mds = new SolrMetadataSource(session);
         mds.setSearchterms("id","non_existing_id");
         mds.execute();
         assertEquals(Long.valueOf("0"),mds.getNumberOfHits());
         Object fetchedObject = mds.getAnother();
         assertNull(fetchedObject);
     }


    private void createAndSaveTestData() {
        Transaction trans = session.beginTransaction();
        Type type = new Type(BigDecimal.TEN,"TestType");
        session.saveOrUpdate(type);
        Edition edition = TestUtil.createTestEdition();
        session.saveOrUpdate(edition);
        Object object = TestUtil.createTestObject(type, edition);
        session.saveOrUpdate(object);
        trans.commit();
    }

}

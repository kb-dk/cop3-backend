package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.crud.database.hibernate.Type;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

import java.math.BigDecimal;
import java.util.Date;

import static java.math.BigInteger.*;

public class SolrMetadataSourceTest {

    private static Session session;


    private static String testId = "test1234";


    @Before
    public void initTest() {
         openDatabaseSession();
         createTestData();
     }

     @After
     public void deleteTestData() {
         Transaction trans = session.beginTransaction();
         session.delete(session.load(Object.class,"test1234"));
         session.delete(session.load(Edition.class,"edition1"));
         session.delete(session.load(Type.class,BigDecimal.TEN));
         trans.commit();
         session.close();
     }

     @Test
     public void testGetSingleObject() {
         SolrMetadataSource mds = new SolrMetadataSource(session);
         mds.setSearchterms("id","test1234");
         mds.execute();
         Assert.assertEquals(Long.valueOf("1"),mds.getNumberOfHits());
         Object fetchedObject = mds.getAnother();
         Assert.assertEquals("test1234",fetchedObject.getId());
     }

     @Test
     public void testGetNonexistingObject() {
         SolrMetadataSource mds = new SolrMetadataSource(session);
         mds.setSearchterms("id","non_existing_id");
         mds.execute();
         Assert.assertEquals(Long.valueOf("0"),mds.getNumberOfHits());
         Object fetchedObject = mds.getAnother();
         Assert.assertNull(fetchedObject);
     }


    private void createTestData() {
        Transaction trans = session.beginTransaction();
        Type type = new Type(BigDecimal.TEN,"TestType");
        session.saveOrUpdate(type);
        Edition edition = new Edition();
        edition.setId("edition1");
        edition.setName("Test edition");
        edition.setNameEn("Test edition en");
        edition.setUrlName("http://www.kb.dk");
        edition.setUrlMatrialType("material 1");
        edition.setUrlPubYear(new BigDecimal(2022));
        edition.setUrlPubMonth("Oct");
        edition.setUrlCollection("rugbrød");
        edition.setCumulusCatalog("images");
        edition.setCumulusTopCatagory("1");
        edition.setNormalisationrule("rule 1");

        edition.setVisiblePublic('j');
        session.saveOrUpdate(edition);
        Object object = new Object();
        GeometryFactory geoFactory = JTSFactoryFinder.getGeometryFactory();
        object.setPoint(geoFactory.createPoint(new Coordinate(10,20)));
        object.setId("test1234");
        object.setType(type);
        object.setEdition(edition);
        object.setMods("<mods></mods>");
        object.setDeleted('n');
        object.setLastModified("1231231231");
        object.setLastModifiedBy("me");
        object.setObjVersion(new BigDecimal(1));
        object.setInterestingess(new BigDecimal(0));
        object.setTitle("title");
        object.setCorrectness(new BigDecimal(0));
        object.setLikes(valueOf(0));
        object.setBuilding("building");
        object.setCreator("creator");
        object.setLocation("location");
        object.setNotAfter(new Date());
        object.setNotBefore(new Date());
        object.setPerson("persion");
        object.setBookmark(valueOf(0));
        object.setRandomNumber(BigDecimal.ZERO);
        session.saveOrUpdate(object);
        trans.commit();
    }

    private static void openDatabaseSession() {
        Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
        SessionFactory sessions = cfg.buildSessionFactory();
        session = sessions.openSession();
    }


}

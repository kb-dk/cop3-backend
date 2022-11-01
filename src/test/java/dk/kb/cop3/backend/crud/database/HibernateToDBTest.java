package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.constants.Types;
import dk.kb.cop3.backend.crud.database.hibernate.Category;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.crud.database.hibernate.Type;
import dk.kb.cop3.backend.crud.util.ObjectFromModsExtractor;
import dk.kb.cop3.backend.crud.util.TestUtil;
import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.*;
import org.locationtech.jts.geom.Geometry;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class HibernateToDBTest {

    private static Session session;
    private static final String LODFOTO = "Lodfoto";
    private static HibernateMetadataSource hibernateMetadataSource;
     private static HibernateMetadataWriter mdw;
    private static final String HIBERNATE_TEST_CATEGORY = "Hibernate-test-category";
    private static final String KATEGORI_TEKST = "slet denne test";
    private static final String MODS_FILE = "testdata/cumulus-ex/_OM/205/master_records/L0717_04.tif-mods.xml";
    private static final String TEST_ID = "/images/luftfo/2011/maj/luftfoto/objectTest";
    private static final double ORIGINAL_LON = 10.177794479921577;
    private static final double ORIGINAL_LAT = 55.275795018274586;

    private static MetadataSource mds;

    @BeforeClass
    public static void before() {
        Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
        SessionFactory sessions = cfg.buildSessionFactory();
        session = sessions.openSession();
        mds = new HibernateMetadataSource(session);

    }

    @AfterClass
    public static void after() {
        //session.getTransaction().rollback();
         session.close();
    }


    @Test
    public void findEnTypeMatcherKonstantenMedDataFraDB() {

        int konstantID = Types.getTypeByName(LODFOTO);

        mds.setType("" + konstantID);

        Type type = mds.getType();
        assertEquals(type.getTypeText(), LODFOTO);
    }


       @Test
    public void opretEnKategori() {
        Category nyCat = new Category(HIBERNATE_TEST_CATEGORY, KATEGORI_TEKST);
        session.save(nyCat);
        mds.setCategory(HIBERNATE_TEST_CATEGORY);
        assertEquals(nyCat.getCategoryText(), mds.getCategory().getCategoryText());
        //findCategory
        mds.setCategory(HIBERNATE_TEST_CATEGORY);
        Category cat = mds.getCategory();
        assertEquals(cat.getCategoryText(), KATEGORI_TEKST);
    }

    @Test
    public void findEdition() {
        mds.setEdition("/images/luftfo/2011/maj/luftfoto");
        Edition ed = mds.getEdition();
        assertEquals(ed.getName(), "Luftfoto");
    }

    private void changeAndTestGeoPoint(MetadataWriter metadataWriter, Object cobject) {
        double lon;
        Geometry pointForTest;
        double lat;
        final double NEW_LAT = 60.90d;
        final double NEW_LOM = 750.60d;
        metadataWriter.updateGeo(TEST_ID, NEW_LAT, NEW_LOM, "test user", cobject.getLastModified(), 0.0);
        TestUtil.getCobject(TEST_ID, session);
        pointForTest = cobject.getPoint();
        lat = pointForTest.getCoordinate().getX();
        lon = pointForTest.getCoordinate().getY();
        Assert.assertTrue(lat == NEW_LAT);
        Assert.assertTrue(lon == NEW_LOM);
    }


    private static String clob2string(Clob clob) throws Exception {
        StringBuffer stringBuffer = new StringBuffer();
        String s;
        BufferedReader br = new BufferedReader(clob.getCharacterStream());
        while ((s = br.readLine()) != null)
            stringBuffer.append(s);
        return stringBuffer.toString();
    }

    @Test // Den her giver vist ikke mening, men pyt
    public void testGet(){
        List<Edition> editions = session.createSQLQuery("select * from edition")
                .addEntity("edi", Edition.class)
                .list();
        Assert.assertTrue(editions.size()>0);
    }


}

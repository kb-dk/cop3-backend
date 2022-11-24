package dk.kb.cop3.backend.crud.test;

import dk.kb.cop3.backend.crud.database.HibernateMetadataWriter;
import dk.kb.cop3.backend.crud.database.MetadataWriter;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.crud.update.Reformulator;
import dk.kb.cop3.backend.crud.util.ObjectFromModsExtractor;
import dk.kb.cop3.backend.crud.util.TestUtil;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.junit.jupiter.api.Disabled;
import org.w3c.dom.Document;
import javax.xml.xpath.XPathExpressionException;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HibernateMetadataWriterTest {

    private static Logger logger = Logger.getLogger(HibernateMetadataWriterTest.class);
    private static Session session;
    final static String NEW_TITLE = "New Title";

    @BeforeEach
    public void initTest() throws FileNotFoundException {
        session = TestUtil.openDatabaseSession();
    }

    @AfterEach
    public void closeSession() {
        TestUtil.closeDatabaseSession(session);
    }

    @Test
    public void create() {
        HibernateMetadataWriter metadataWriter = new HibernateMetadataWriter(session);
        String testMods = TestUtil.getTestMods();
        TestUtil.createAndSaveDefaultTestCobject(TestUtil.TEST_ID, metadataWriter, session);
        final Object savedCopject = TestUtil.getCobject(TestUtil.TEST_ID, session);
        assertEquals("Danmark, Fyn, Langsted", savedCopject.getLocation());
        TestUtil.deleteFromDatabase(Object.class,TestUtil.TEST_ID, session);
    }

    @Test
    public void createFromMods() {
        String testMods = TestUtil.getTestMods();
        HibernateMetadataWriter metadataWriter = new HibernateMetadataWriter(session);
        String id = metadataWriter.createFromMods(testMods);
        assertNotEquals("",id);
//        TestUtil.deleteFromDatabase(Object.class, "/images/luftfo/2011/maj/luftfoto/object182167", session);
    }

    @Test
    public void updateCobject() throws XPathExpressionException {//Updatefunktionen, som denne test dækker kaldes tilsyneladende reelt aldrig. Så måske skal både metode og test dø!
        final String TEST_LOCATION = "Testlocation";
        HibernateMetadataWriter metadataWriter = new HibernateMetadataWriter(session);
        TestUtil.createAndSaveDefaultTestCobject(TestUtil.TEST_ID, metadataWriter, session);
        //Get created cobject
        Object savedCopject = TestUtil.getCobject(TestUtil.TEST_ID, session);
        assertFalse(savedCopject.getLocation().equalsIgnoreCase(TEST_LOCATION));
        String lastModified = savedCopject.getLastModified();
        //change and update copject
        savedCopject.setLocation(TEST_LOCATION);
        metadataWriter.updateCobject(savedCopject, lastModified);
        savedCopject = TestUtil.getCobject(TestUtil.TEST_ID, session);
        assertTrue(savedCopject.getLocation().equalsIgnoreCase(TEST_LOCATION));
        TestUtil.deleteFromDatabase(Object.class, TestUtil.TEST_ID, session);
    }

    @Test
    public void updateFromMods() throws XPathExpressionException {
        HibernateMetadataWriter metadataWriter = new HibernateMetadataWriter(session);
        String testMods = TestUtil.getTestMods();
        ObjectFromModsExtractor objectFromModsExtractor = new ObjectFromModsExtractor();
        //If cobject and mods-recordIdentifier dosn't correspond, update will fail.
        final String modsWithTestId = TestUtil.changeIdInMods(TestUtil.TEST_ID, testMods, objectFromModsExtractor);
        TestUtil.createAndSaveTestCobjectFromMods(TestUtil.TEST_ID, modsWithTestId, metadataWriter, session);
        testUpdatedCobject(TestUtil.TEST_ID, NEW_TITLE, metadataWriter);
        TestUtil.deleteFromDatabase(Object.class, TestUtil.TEST_ID, session);
        TestUtil.deleteAuditTrail(TestUtil.TEST_ID, session);
    }

    @Test
    public void updateGeoFromMods() throws XPathExpressionException {
        HibernateMetadataWriter metadataWriter = new HibernateMetadataWriter(session);
        String testMods = TestUtil.getTestMods();
        TestUtil.createAndSaveTestCobjectFromMods(TestUtil.TEST_ID,testMods,metadataWriter,session);
        Object savedCobject = TestUtil.getCobject(TestUtil.TEST_ID,session);
        ObjectFromModsExtractor objectFromModsExtractor = new ObjectFromModsExtractor();
        final String modsWithTestId = TestUtil.changeIdInMods(TestUtil.TEST_ID, testMods, objectFromModsExtractor);
        final String modsWithChangedLatLng= TestUtil.changeLatLngInMods(modsWithTestId,44.5,8.7);
        metadataWriter.updateFromMods(TestUtil.TEST_ID,modsWithChangedLatLng,savedCobject.getLastModified(),"Junit");
        savedCobject = TestUtil.getCobject(TestUtil.TEST_ID,session);
        double correctness = 0.000001d;
        assertEquals(44.5,savedCobject.getPoint().getCoordinate().getX(),correctness);
        assertEquals(8.7,savedCobject.getPoint().getCoordinate().getY(),correctness);
    }


    @Test
    public void updateGeo() {
        HibernateMetadataWriter metadataWriter = new HibernateMetadataWriter(session);
        TestUtil.createAndSaveDefaultTestCobject(TestUtil.TEST_ID, metadataWriter, session);
        final Object savedCopject = TestUtil.getCobject(TestUtil.TEST_ID, session);
        double x = savedCopject.getPoint().getCoordinate().getX();
        double y = savedCopject.getPoint().getCoordinate().getY();
        double correctness = 0.000001d;
        assertEquals(55.275795018274586 , x, correctness);
        assertEquals(10.177794479921577, y, correctness);
        final double LAT = 2.2;
        final double LON = 3.3;
        metadataWriter.updateGeo(TestUtil.TEST_ID, LAT, LON, TestUtil.TEST_ID, savedCopject.getLastModified(), correctness);
        assertUpdatedGeo(TestUtil.TEST_ID, correctness, LAT, LON);
        TestUtil.deleteFromDatabase(Object.class, TestUtil.TEST_ID, session);
        TestUtil.deleteAuditTrail(TestUtil.TEST_ID, session);
    }

    private void assertUpdatedGeo(String TEST_ID, double correctness, double LAT, double LON) {
        double x;
        double y;
        final Object updatedCopject = TestUtil.getCobject(TEST_ID, session);
        x = updatedCopject.getPoint().getCoordinate().getX();
        y = updatedCopject.getPoint().getCoordinate().getY();
        assertEquals(LAT, x, correctness);
        assertEquals(LON, y, correctness);
    }

    @Disabled
    @Test
    public void removeAuditTrail(){
        TestUtil.deleteAuditTrail("HibernateMetadataWriterTest", session);
    }

    private void testUpdatedCobject(String TEST_ID, String NEW_TITLE, MetadataWriter metadataWriter) throws XPathExpressionException {
        ObjectFromModsExtractor objectFromModsExtractor = new ObjectFromModsExtractor();
        updateModsInCobject(objectFromModsExtractor, metadataWriter, TEST_ID, NEW_TITLE);
        final Object updatedCobject = TestUtil.getCobject(TEST_ID, session);
        final String title = updatedCobject.getTitle();
        String mods = updatedCobject.getMods();
        Document modsDocument = objectFromModsExtractor.parseModsString(mods);
        String titleExtract = objectFromModsExtractor.extract(ObjectFromModsExtractor.TITLE_XPATH, modsDocument);
        assertTrue(NEW_TITLE.equalsIgnoreCase(titleExtract));
        assertTrue(NEW_TITLE.equalsIgnoreCase(title));
    }

    private static Object updateModsInCobject(ObjectFromModsExtractor objectFromModsExtractor, MetadataWriter metadataWriter, String TEST_ID, String NEW_TITLE) throws XPathExpressionException {
        Object cobject = TestUtil.getCobject(TEST_ID, session);
        String mods = cobject.getMods();
        Document modsDocument = objectFromModsExtractor.parseModsString(mods);
        String titleExtractedFromMods = objectFromModsExtractor.extract(ObjectFromModsExtractor.TITLE_XPATH, modsDocument);
        assertTrue("Overgård - 1988".equalsIgnoreCase(titleExtractedFromMods));
//      Tjek reformulators måde at opdatere et felt på i UpdateService klassen
        Reformulator reformulator = new Reformulator(mods);
        reformulator.changeField("title", NEW_TITLE);
        String newMods = reformulator.commitChanges();
        modsDocument = objectFromModsExtractor.parseModsString(newMods);
        titleExtractedFromMods = objectFromModsExtractor.extract(ObjectFromModsExtractor.TITLE_XPATH, modsDocument);
        assertTrue(NEW_TITLE.equalsIgnoreCase(titleExtractedFromMods));
        final String stringFromDocument = TestUtil.getStringFromDocument(modsDocument);
        cobject.setMods(stringFromDocument);
        metadataWriter.updateFromMods(TEST_ID, stringFromDocument, cobject.getLastModified(), TEST_ID);
        return cobject;
    }

}

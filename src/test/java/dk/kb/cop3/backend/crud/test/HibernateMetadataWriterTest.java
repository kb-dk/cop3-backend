package dk.kb.cop3.backend.crud.test;

import dk.kb.cop3.backend.crud.database.HibernateMetadataWriter;
import dk.kb.cop3.backend.crud.database.MetadataWriter;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.crud.update.Reformulator;
import dk.kb.cop3.backend.crud.util.ObjectFromModsExtractor;
import dk.kb.cop3.backend.crud.util.TestUtil;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.junit.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.persistence.PersistenceException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import java.io.StringWriter;


public class HibernateMetadataWriterTest {

    private static Logger logger = Logger.getLogger(HibernateMetadataWriterTest.class);
    private static Session session;
    final static String TEST_ID = "/images/luftfo/2011/maj/luftfoto/object/Test";
    final static String NEW_TITLE = "New Title";

    @Before
    public void initTest(){
        session = TestUtil.openDatabaseSession();
    }

    @After
    public void closeSession() {
        TestUtil.closeDatabaseSession(session);
    }

    @Test
    public void create() throws XPathExpressionException {
        HibernateMetadataWriter metadataWriter = new HibernateMetadataWriter(session);
        String testMods = TestUtil.getTestMods();
        createAndSaveTestCobject(TEST_ID, testMods, metadataWriter);
        final Object savedCopject = TestUtil.getCobject(TEST_ID, session);
        Assert.assertEquals("Danmark, Fyn, Langsted", savedCopject.getLocation());
        TestUtil.deleteFromDatabase(Object.class,TEST_ID, session);
    }

    @Test
    public void updateCobject() throws XPathExpressionException {//Updatefunktionen, som denne test dækker kaldes tilsyneladende reelt aldrig. Så måske skal både metode og test dø!
        final String TEST_LOCATION = "Testlocation";
        HibernateMetadataWriter metadataWriter = new HibernateMetadataWriter(session);
        String testMods = TestUtil.getTestMods();
        createAndSaveTestCobject(TEST_ID, testMods, metadataWriter);
        //Get created cobject
        Object savedCopject = TestUtil.getCobject(TEST_ID, session);
        Assert.assertFalse(savedCopject.getLocation().equalsIgnoreCase(TEST_LOCATION));
        String lastModified = savedCopject.getLastModified();
        //change and update copject
        savedCopject.setLocation(TEST_LOCATION);
        metadataWriter.updateCobject(savedCopject, lastModified);
        savedCopject = TestUtil.getCobject(TEST_ID, session);
        Assert.assertTrue(savedCopject.getLocation().equalsIgnoreCase(TEST_LOCATION));
        TestUtil.deleteFromDatabase(Object.class, TEST_ID, session);
    }

    @Test
    public void updateFromMods() throws XPathExpressionException {
        HibernateMetadataWriter metadataWriter = new HibernateMetadataWriter(session);
        String testMods = TestUtil.getTestMods();
        ObjectFromModsExtractor objectFromModsExtractor = new ObjectFromModsExtractor();
        //If cobject and mods-recordIdentifier dosn't correspond, update will fail.
        final String modsWithTestId = changeIdInMods(TEST_ID, testMods, objectFromModsExtractor);
        createAndSaveTestCobject(TEST_ID, modsWithTestId, metadataWriter);
        testUpdatedCobject(TEST_ID, NEW_TITLE, metadataWriter);
        TestUtil.deleteFromDatabase(Object.class, TEST_ID, session);
        TestUtil.deleteAuditTrail(TEST_ID, session);
    }

    @Test
    public void updateGeo() throws XPathExpressionException {
        HibernateMetadataWriter metadataWriter = new HibernateMetadataWriter(session);
        String testMods = TestUtil.getTestMods();
        createAndSaveTestCobject(TEST_ID, testMods, metadataWriter);
        final Object savedCopject = TestUtil.getCobject(TEST_ID, session);
        double x = savedCopject.getPoint().getCoordinate().getX();
        double y = savedCopject.getPoint().getCoordinate().getY();
        double correctness = 0.000001d;
        Assert.assertEquals(55.275795018274586 , x, correctness);
        Assert.assertEquals(10.177794479921577, y, correctness);
        final double LAT = 2.2;
        final double LON = 3.3;
        metadataWriter.updateGeo(TEST_ID, LAT, LON, TEST_ID, savedCopject.getLastModified(), correctness);
        assertUpdatedGeo(TEST_ID, correctness, LAT, LON);
        TestUtil.deleteFromDatabase(Object.class, TEST_ID, session);
        TestUtil.deleteAuditTrail(TEST_ID, session);

    }

    private void assertUpdatedGeo(String TEST_ID, double correctness, double LAT, double LON) {
        double x;
        double y;
        final Object updatedCopject = TestUtil.getCobject(TEST_ID, session);
        x = updatedCopject.getPoint().getCoordinate().getX();
        y = updatedCopject.getPoint().getCoordinate().getY();
        Assert.assertEquals(LAT, x, correctness);
        Assert.assertEquals(LON, y, correctness);
    }

    @Ignore
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
        Assert.assertTrue(NEW_TITLE.equalsIgnoreCase(titleExtract));
        Assert.assertTrue(NEW_TITLE.equalsIgnoreCase(title));
    }

    private Object updateModsInCobject(ObjectFromModsExtractor objectFromModsExtractor, MetadataWriter metadataWriter, String TEST_ID, String NEW_TITLE) throws XPathExpressionException {
        Object cobject = TestUtil.getCobject(TEST_ID, session);
        String mods = cobject.getMods();
        Document modsDocument = objectFromModsExtractor.parseModsString(mods);
        String titleExtractedFromMods = objectFromModsExtractor.extract(ObjectFromModsExtractor.TITLE_XPATH, modsDocument);
        Assert.assertTrue("Overgård - 1988".equalsIgnoreCase(titleExtractedFromMods));
//        modsDocument.getDocumentElement().getElementsByTagName("md:title").item(0).getFirstChild().setNodeValue(NEW_TITLE);

//        Tjek reformulators måde at opdatere et felt på i UpdateService klassen
        Reformulator reformulator = new Reformulator(mods);
        reformulator.changeField("title", NEW_TITLE);
        String newMods = reformulator.commitChanges();
        modsDocument = objectFromModsExtractor.parseModsString(newMods);
        titleExtractedFromMods = objectFromModsExtractor.extract(ObjectFromModsExtractor.TITLE_XPATH, modsDocument);
        Assert.assertTrue(NEW_TITLE.equalsIgnoreCase(titleExtractedFromMods));
        final String stringFromDocument = getStringFromDocument(modsDocument);
        cobject.setMods(stringFromDocument);
        metadataWriter.updateFromMods(TEST_ID, stringFromDocument, cobject.getLastModified(), TEST_ID);
        return cobject;
    }


    private void createAndSaveTestCobject(String TEST_ID, String mods, HibernateMetadataWriter metadataWriter) throws XPathExpressionException {
        Object cobject = TestUtil.extractCobjectFromMods(mods, session);
        Assert.assertEquals("Danmark, Fyn, Langsted", cobject.getLocation());
        cobject.setId(TEST_ID);
        try {
            metadataWriter.create(cobject);
        }catch (PersistenceException e) {
            //Chances are an old test failed and (c)object is not deleted
            TestUtil.deleteFromDatabase(Object.class, TEST_ID, session);
            metadataWriter.create(cobject);
        }
    }

    private String changeIdInMods(String TEST_ID, String testMods, ObjectFromModsExtractor objectFromModsExtractor) throws XPathExpressionException {
        Document modsDocument = objectFromModsExtractor.parseModsString(testMods);
        String idExtract = objectFromModsExtractor.extract(ObjectFromModsExtractor.ID_XPATH, modsDocument);

        final Node item = modsDocument.getDocumentElement().getElementsByTagName("md:recordIdentifier").item(0);
        item.getFirstChild().setNodeValue(TEST_ID);
        final String modsWithTestId = getStringFromDocument(modsDocument);
        return modsWithTestId;
    }

    public String getStringFromDocument(Document doc)
    {
        try
        {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        }
        catch(TransformerException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

}

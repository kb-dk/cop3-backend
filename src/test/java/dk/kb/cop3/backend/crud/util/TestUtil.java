package dk.kb.cop3.backend.crud.util;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.crud.database.HibernateMetadataWriter;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.SolrMetadataSource;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.crud.database.hibernate.Type;
import dk.kb.cop3.backend.crud.update.Reformulator;
import dk.kb.cop3.backend.solr.CopSolrClient;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.TimeZone;

import static java.math.BigInteger.valueOf;
import static org.junit.jupiter.api.Assertions.*;

public class TestUtil {
    private static final String LUFTFOTO_MODS_FILE = "src/test/resources/testdata/luftfoto_object182167.mods.xml";
    public final static String TEST_ID = "/images/luftfo/2011/maj/luftfoto/object000000000000";

    public static String getTestMods() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Copenhagen"));
        try {
            return Files.readString(Path.of(LUFTFOTO_MODS_FILE), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object extractCobjectFromMods(String mods, Session session){
        ObjectFromModsExtractor objectFromModsExtractor = ObjectFromModsExtractor.getInstance();
        Object copject = new Object();
        return objectFromModsExtractor.extractFromMods(copject, mods, session);
    }

    public static Session openDatabaseSession() throws FileNotFoundException {
        CopBackendProperties.initialize(new FileInputStream("src/test/resources/cop_config.xml"));
        SessionFactory sessions = HibernateUtil.getSessionFactory();
        return sessions.openSession();
    }

    public static void deleteFromDatabase(Class entityClass, Serializable id, Session session) {
        Transaction trans = session.beginTransaction();
        try {
            session.delete(session.load(entityClass, id));
        }catch (EntityNotFoundException entityNotFoundException){
            System.out.println(entityNotFoundException.getMessage());
            //do nothing - the entity is allready gone
        } finally {
            trans.commit();
        }
    }

    public static void closeDatabaseSession(Session session) {
        session.close();
    }

    public static Object createFullTestObject(){
        final Edition edition = createTestEdition();
        Type type = new Type(BigDecimal.TEN,"TestType");
        return createTestObject(type, edition);
    }

    public static Edition createTestEdition() {
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
        return edition;
    }

    public static void createAndSaveDefaultTestCobject(String TEST_ID, HibernateMetadataWriter metadataWriter, Session session){
        String testMods = TestUtil.getTestMods();
        createAndSaveTestCobjectFromMods(TEST_ID, testMods, metadataWriter, session);
    }

    public static void createAndSaveTestCobjectFromMods(String testId, String mods, HibernateMetadataWriter metadataWriter, Session session){
        Transaction transaction = session.beginTransaction();
        Object cobject = TestUtil.extractCobjectFromMods(mods, session);
        transaction.commit();
        cobject.setId(testId);
        try {
            metadataWriter.create(cobject);
        }catch (PersistenceException e) {
            //Chances are an old test failed and (c)object is not deleted
            TestUtil.deleteFromDatabase(Object.class, testId, session);
            metadataWriter.create(cobject);
        }
    }


    public static Object createTestObject(Type type, Edition edition) {
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
        return object;
    }

    public static Object getCobject(String id, Session session){
        SolrMetadataSource mds = new SolrMetadataSource(session);
        mds.setSearchterms("id",id);
        mds.execute();
        assertEquals(Long.valueOf("1"),mds.getNumberOfHits());
        return mds.getAnother();
    }

    public static void deleteAuditTrail(String oid, Session session) {
        Transaction transaction = session.beginTransaction();
        Query query = session.createQuery("delete dk.kb.cop3.backend.crud.database.hibernate.AuditTrail where oid = '" + oid + "'");
        query.executeUpdate();
        transaction.commit();
        final TransactionStatus status = transaction.getStatus();
    }

    public static String getStringFromDocument(Document doc)
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

    public static String changeIdInMods(String TEST_ID, String testMods, ObjectFromModsExtractor objectFromModsExtractor) throws XPathExpressionException {
        Document modsDocument = objectFromModsExtractor.parseModsString(testMods);
        final Node item = modsDocument.getDocumentElement().getElementsByTagName("md:recordIdentifier").item(0);
        item.getFirstChild().setNodeValue(TEST_ID);
        final String modsWithTestId = getStringFromDocument(modsDocument);
        return modsWithTestId;
    }

    public static String changeLatLngInMods(String testMods, double lat, double lng) {
        Reformulator reformulator = new Reformulator(testMods);
        reformulator.changeField("latlng", String.valueOf(lat) + ", " + String.valueOf(lng));
        return reformulator.commitChanges();
    }

    public static void deleteTestObjectFromSolr(String testId,Session session) {
        CopSolrClient solrClient = new CopSolrClient(session);
        solrClient.deleteCobjectFromSolr(testId);
    }
}

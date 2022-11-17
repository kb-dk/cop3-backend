package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.commonutils.DomUtils;
import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.constants.DatacontrollerConstants;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.crud.util.TestUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.*;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.junit.*;
import org.locationtech.jts.geom.Coordinate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.Iterator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class is the main reference for the supported URI's in the CRUD engine.
 */
public class ApiTest {
    private static final String COP_CONFIG = "src/test/resources/cop_config.xml";
    private static String HOST_NAME = "";
    static HttpClient client = new HttpClient();
    static GetMethod get = new GetMethod();
    static PutMethod put = new PutMethod();
    static PostMethod post = new PostMethod();
    private static final Logger logger = Logger.getLogger(ApiTest.class);
    private static final String SOLR_SUBJECT_NAME = "subject203";
    private static final String OBJECT_NAME = "object62132";
    private static final String OBJECT2_NAME = "object135334";
    private static final String OBJECT3_NAME = "object223212";
    private static final String OBJECT_PATH = "/images/luftfo/2011/maj/luftfoto/";
    private static final String OBJECT_URI = OBJECT_PATH + OBJECT_NAME;
    private static final String OBJECT2_URI = OBJECT_PATH + OBJECT2_NAME;
    private static final String OBJECT3_URI = OBJECT_PATH + OBJECT3_NAME;
    private static final String SUBJECT_URI = OBJECT_PATH + SOLR_SUBJECT_NAME;
    private static final String OBJECT_ID = "/images/luftfo/2011/maj/luftfoto";
    private final static String UPDATE_SERVICE_URI = "/update";
    private final static String CREATE_SERVICE_URI = "/create";
    private static final String SYNDICATION_SERVICE_URI = "/syndication" ;
    private static final String CONTENT_SERVICE_URI = "/content" ;
    private static final String NAVIGATION_SERVICE_URI = "/navigation" ;
    private static final String SYNDICATION_OBJECT_URI = SYNDICATION_SERVICE_URI + OBJECT_PATH + OBJECT3_NAME;
    private static final String SYNDICATION_SUBJECT_URI = SYNDICATION_SERVICE_URI + SUBJECT_URI;
    private static final String BOUNDING_BOX = "10.772781372070312,55.384376628312815,10.331611633300783,55.23587533144054";
    private static final String TEST_FILES_PATH = "src/test/resources/testdata/";
    private static final String LUFTFOTO_MODS_FILE_For_OBJECT3 = TEST_FILES_PATH + "luftfoto_"+ OBJECT3_NAME +".mods.xml";
    private static final String OPML_FILE = TEST_FILES_PATH + "david_simonsens_haandskrifter.opml.xml";

    /**
     * Classes for building a DOM Document from the result stream
     */
    private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder builder = null;

    /**
     * Start up the embedded Jetty at port 8080
     *
     * @throws Exception
     */
    @BeforeClass
    public static void initTest() throws Exception {
        CopBackendProperties.initialize(new FileInputStream(COP_CONFIG));
        client.getHttpConnectionManager().getParams().setConnectionTimeout(DatacontrollerConstants.CONN_TIMEOUT);
        client.getHttpConnectionManager().getParams().setSoTimeout(DatacontrollerConstants.CONN_TIMEOUT);
        HOST_NAME = CopBackendProperties.getCopBackendUrl();
        logger.debug("Hostname:"+ HOST_NAME);
        builder = factory.newDocumentBuilder();
    }

    /**
     * Stop the embedded server again
     *
     * @throws Exception
     */
    @AfterClass
    public static void postTest() throws Exception {
        close(get);
        close(put);
    }

    private void testConnectionToSolr(int statusCode, int expectedStatusCode){
        assertEquals(expectedStatusCode, statusCode);
    }

    private void testConnectionToDB(int statusCode, int expectedStatusCode){
        assertEquals(expectedStatusCode, statusCode);
    }

    private GetMethod getResponse(String request, String returnType){
        get.setPath(HOST_NAME + request);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching" + returnType + " at:  " + request);
            logger.error(io);
        }
        return get;
    }

    private void compareTheActualNumberOfRecordsWithExpectedNumberInMods(Document document, int expectedNumber) throws XPathExpressionException {
        int actualNumberOfRecords = extractXpathFromMods("/modsCollection/mods/recordInfo", document).getLength();
        assertEquals(expectedNumber, actualNumberOfRecords);
    }

    private void compareTheActualNumberOfRecordsWithExpectedNumberInRSS(Document document, int expectedNumber) throws XPathExpressionException {
        int actualNumberOfRecords = extractXpathFromRSS("rss/channel/item", document).getLength();
        assertEquals(expectedNumber, actualNumberOfRecords);
    }

    public Document parseModsString(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try
        {
            builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xmlString)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public NodeList extractXpathFromMods(String xp, Document document) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        NamespaceContext ctx = getNameSpace();
        xpath.setNamespaceContext(ctx);
        NodeList records = (NodeList) xpath.evaluate(xp, document, XPathConstants.NODESET);
        System.out.println("records -> " + records);
        return records;
    }

    public NodeList extractXpathFromRSS(String xp, Document document) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        return (NodeList) xpath.evaluate(xp, document, XPathConstants.NODESET);
    }

    private Coordinate[] getCoordinatesFromRSS(Document document) throws XPathExpressionException {
        NodeList records = extractXpathFromRSS("rss/channel/item", document);
        return getLatLonFromRecords(records);
    }

    private void checkIfAllCoordinatesAreInsideTheBoundingBox(Coordinate[] coordinates, String boundingBox) {
        for (Coordinate coordinate : coordinates) {
            assertTrue(coordinate.x + ' ' + coordinate.y + " should be located in the bounding box:", isCoordinateInsideBoundingBox(coordinate, boundingBox));
        }
    }

    private Boolean isCoordinateInsideBoundingBox(Coordinate coordinate, String boundingBox) {
        String[] bb = boundingBox.split(",");
        return Double.parseDouble(bb[3]) <= coordinate.x && coordinate.x <= Double.parseDouble(bb[1]) && Double.parseDouble(bb[2]) <= coordinate.y && coordinate.y <= Double.parseDouble(bb[0]);
    }

    private void showNodeList(NodeList nodeList){
        for (int i = 0; i < nodeList.getLength(); i++) {
            logger.info(nodeList.item(i).getTextContent());
        }
    }

    private Coordinate[] getLatLonFromRecords(NodeList nodeList){
        Coordinate[] coords = new Coordinate[nodeList.getLength()];
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element el = (Element) nodeList.item(i);
            double lat = Double.parseDouble(el.getElementsByTagName("geo:lat").item(0).getTextContent());
            double lon = Double.parseDouble(el.getElementsByTagName("geo:long").item(0).getTextContent());
            coords[i] = new Coordinate(lat, lon);
        }
        return coords;
    }
    private NamespaceContext getNameSpace(){
        return new NamespaceContext() {
            public String getNamespaceURI(String prefix) {
                return prefix.equals("md") ? "http://www.loc.gov/mods/v3" : null;
            }
            public Iterator<String> getPrefixes(String val) {
                return null;
            }
            public String getPrefix(String uri) {
                return null;
            }
        };
    }

    // GET LISTS OF OBJECTS
    // For testSyndicationAll* tests to pass there must be a connection to Solr and subject203 must exist in Solr

    @Test
    public void testSyndicationAllObjectsInSubjectMods() throws XPathExpressionException, IOException {
        GetMethod get = getResponse(SYNDICATION_SUBJECT_URI + "?format=mods&itemsPerPage=10", "list of objects");
        testConnectionToSolr(get.getStatusCode(), 200);
        Document document = parseModsString(get.getResponseBodyAsString());
        compareTheActualNumberOfRecordsWithExpectedNumberInMods(document, 10);
    }

    @Test
    public void testSyndicationAllObjectsInSubject() throws XPathExpressionException, IOException {
        GetMethod get = getResponse(SYNDICATION_SUBJECT_URI + "?itemsPerPage=10", "list of objects");
        testConnectionToSolr(get.getStatusCode(), 200);
        Document document = parseModsString(get.getResponseBodyAsString());
        compareTheActualNumberOfRecordsWithExpectedNumberInRSS(document, 10);
    }

    @Ignore("Ignored since language parameter has no effect on the response!")
    @Test
    public void testSyndicationAllObjectsInSubjectLanguage() throws IOException {
        GetMethod get1 = getResponse(SYNDICATION_SUBJECT_URI + "/da?format=mods&itemsPerPage=10", "list of objects");
        GetMethod get2 = getResponse(SYNDICATION_SUBJECT_URI + "/en?format=mods&itemsPerPage=10", "list of objects");
        assertEquals("Language should have no effect on the response.", get1.getResponseBodyAsString(), get2.getResponseBodyAsString());
    }

    @Ignore("Ignored since random parameter has no effect on the response!")
    @Test
    public void testSyndicationAllObjectsInFraction() throws IOException {
        GetMethod get = getResponse(SYNDICATION_SUBJECT_URI + "?format=mods&random=0.8&itemsPerPage=10", "list of objects");
        testConnectionToSolr(get.getStatusCode(), 200);
        logger.info(get.getResponseBodyAsString());
    }

    @Test
    public void testSyndicationAllObjectsInSubject5ItemPerRequest() throws XPathExpressionException, IOException {
        GetMethod get = getResponse(SYNDICATION_SUBJECT_URI + "?itemsPerPage=5", "list of objects");
        testConnectionToSolr(get.getStatusCode(), 200);
        Document document = parseModsString(get.getResponseBodyAsString());
        compareTheActualNumberOfRecordsWithExpectedNumberInRSS(document, 5);
    }

    @Test
    public void testSyndicationAllObjectsInSubjectInBBO() throws XPathExpressionException, IOException {
        GetMethod get = getResponse(SYNDICATION_SUBJECT_URI + "?bbo=" + BOUNDING_BOX + "&itemsPerPage=10", "list of objects");
        testConnectionToSolr(get.getStatusCode(), 200);
        Document document = parseModsString(get.getResponseBodyAsString());
        compareTheActualNumberOfRecordsWithExpectedNumberInRSS(document, 10);
        Coordinate[] coordinates = getCoordinatesFromRSS(document);
        checkIfAllCoordinatesAreInsideTheBoundingBox(coordinates, BOUNDING_BOX);
    }

    @Test //TODO check the totalResults
    public void testSyndicationAllObjectsInSubjectInBBOWithFreeText() {
        GetMethod get = getResponse(SYNDICATION_SUBJECT_URI + "?bbo=" + BOUNDING_BOX + "&query=jensen&itemsPerPage=10", "list of objects");
        testConnectionToSolr(get.getStatusCode(), 200);
    }

    @Test //TODO check the totalResults
    public void testSyndicationAllObjectsInSubjectInBBOWithFieldedSearch() {
        GetMethod get = getResponse(SYNDICATION_SUBJECT_URI + "?bbo=" + BOUNDING_BOX + "&query=person:jensen&itemsPerPage=10&itemsPerPage=10", "list of objects");
        testConnectionToSolr(get.getStatusCode(), 200);
    }

    @Test
    public void testSyndicationAllObjectsInSubjectInBBOWithYearRange() {
        GetMethod get = getResponse(SYNDICATION_SUBJECT_URI + "?bbo=" + BOUNDING_BOX + "&notBefore=1939-01-01&notAfter=1945-01-01&itemsPerPage=10", "list of objects");
        testConnectionToSolr(get.getStatusCode(), 200);
    }

    @Test
    public void testSyndicationAllThingsCombined() {
        GetMethod get = getResponse(SYNDICATION_SUBJECT_URI + "?bbo=" + BOUNDING_BOX + "&query=person:sylvest+jensen%26location:fyn&random=0.8&itemsPerPage=10", "list of objects");
        testConnectionToSolr(get.getStatusCode(), 200);
    }


    // GET SINGLE OBJECTS

    @Test
    public void testSyndicationObject() {
        GetMethod get = getResponse(SYNDICATION_OBJECT_URI, "object");
        testConnectionToDB(get.getStatusCode(), 200);
    }

    @Test
    public void testSyndicationObjectMods() {
        GetMethod get = getResponse(SYNDICATION_OBJECT_URI + "/da?format=mods", "object");
        testConnectionToDB(get.getStatusCode(), 200);
    }

    @Test
    public void testSyndicationObjectUnknown() {
        GetMethod get = getResponse(SYNDICATION_OBJECT_URI + "/da?format=unknown", "object");
        testConnectionToDB(get.getStatusCode(), 404);
    }

    // Navigation service
    @Ignore("Ignored because it seems like navigation service is not used!")
    @Test
    public void testNavigationOpmlFull() {
        GetMethod get = getResponse(NAVIGATION_SERVICE_URI + OBJECT_ID, "opml");
        testConnectionToDB(get.getStatusCode(), 200);
    }

    @Ignore("Ignored because it seems like navigation service is not used!")
    @Test
    public void testNavigationOpmlFullDa() {
        GetMethod get = getResponse(NAVIGATION_SERVICE_URI + OBJECT_PATH + "da", "opml");
        testConnectionToDB(get.getStatusCode(), 200);
    }

    @Ignore("Ignored because it seems like navigation service is not used!")
    @Test
    public void testNavigationOpmlSubject() {
        GetMethod get = getResponse(NAVIGATION_SERVICE_URI + SUBJECT_URI, "opml");
        testConnectionToDB(get.getStatusCode(), 200);
    }

    @Ignore("Ignored because it seems like navigation service is not used!")
    @Test
    public void testNavigationOpmlSubjectDa() {
        GetMethod get = getResponse(NAVIGATION_SERVICE_URI + SUBJECT_URI + "/da", "opml");
        testConnectionToDB(get.getStatusCode(), 200);
    }


    // Content services
    @Test
    public void testContentOpml() {
        GetMethod get = getResponse(CONTENT_SERVICE_URI + OBJECT_URI, "object");
        testConnectionToDB(get.getStatusCode(), 200);
    }

    @Test
    public void testContentOpmlLangDa() {
        GetMethod get = getResponse(CONTENT_SERVICE_URI + OBJECT_URI + "/da", "object");
        testConnectionToDB(get.getStatusCode(), 200);
    }


    /**
     * ********** These service are used mostly for backward compatibility and will be implemented in may*********
     *
     * @Test public void testDirectory() {
     * get.setPath(HOST_NAME + DIRECTORY_SERVICE_OPML);
     * try {
     * client.executeMethod(get);
     * } catch (java.io.IOException io) {
     * logger.error("IO Error fetching object at:  " + DIRECTORY_SERVICE_OPML);
     * }
     * assertEquals(200, get.getStatusCode());
     * }
     * @Test public void testConfigurationService() {
     * get.setPath(HOST_NAME + CONFIGURATION_SERVICE_PROPERTIES);
     * try {
     * client.executeMethod(get);
     * } catch (java.io.IOException io) {
     * logger.error("IO Error fetching object at:  " + CONFIGURATION_SERVICE_PROPERTIES);
     * }
     * assertEquals(200, get.getStatusCode());
     * }
     */


    //************************* CREATE AND UPDATE *******************//
    @Test
    public void testCreateObjectService() throws  FileNotFoundException{
        deleteObjectIfExists(OBJECT3_URI);
        int statusCode = createObject(OBJECT3_URI, LUFTFOTO_MODS_FILE_For_OBJECT3);
        assertEquals(201, statusCode);
        revertCreatingObject(OBJECT3_URI);
    }

    private void deleteObjectIfExists(String object) throws FileNotFoundException {
        final Session ses = TestUtil.openDatabaseSession();
        ses.beginTransaction();
        try {
            dk.kb.cop3.backend.crud.database.hibernate.Object cObject = ses.get(Object.class, object);
            if (cObject != null) {
                ses.delete(cObject);
                ses.getTransaction().commit();
            }
        } catch (Exception ex) {
            logger.debug("error "+ex.getMessage());
            ses.getTransaction().rollback();
            assertTrue("testCreateObjectService hibernate error "+ex.getMessage(),false);
        }
    }

    private int createObject(String object, String mods_file_name){
        PutMethod put = new PutMethod();
        put.setPath(HOST_NAME + CREATE_SERVICE_URI + object);
        logger.info(HOST_NAME + CREATE_SERVICE_URI + object);
        try {
            org.w3c.dom.Document dom = builder.parse(new File(mods_file_name));
            RequestEntity entity = new StringRequestEntity(DomUtils.doc2String(dom), "application/xml", "UTF-8");
            put.setRequestEntity(entity);
            logger.debug(put.getPath());
            client.executeMethod(put);
        } catch (java.io.IOException io) {
            logger.error("IO Error putting object to:  " + CREATE_SERVICE_URI);
            logger.debug(io.getMessage());
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
        return put.getStatusCode();
    }

    private void revertCreatingObject(String object) throws FileNotFoundException {
        deleteObjectIfExists(object);
    }

    @Test
    public void testUpdateObjectService() throws SAXException {
        String lastModified = getLastModifiedFromExistingObject();
        String lastModifiedBy = "TEST";

        post.setPath(HOST_NAME + CREATE_SERVICE_URI + OBJECT3_URI + "?lastmodified=" + lastModified + "&user=" + lastModifiedBy);
        logger.info(HOST_NAME + CREATE_SERVICE_URI + OBJECT3_URI + "?lastmodified=" + lastModified + "&user=" + lastModifiedBy);
        try {
            org.w3c.dom.Document dom = builder.parse(new File(LUFTFOTO_MODS_FILE_For_OBJECT3));
            RequestEntity entity = new StringRequestEntity(DomUtils.doc2String(dom), "application/xml", "UTF-8");
            post.setRequestEntity(entity);
            client.executeMethod(post);
        } catch (java.io.IOException io) {
            logger.error("IO Error putting object to:  " + UPDATE_SERVICE_URI);
            logger.error(io);
        }
        logger.debug("post.getStatusCode() = " + post.getStatusCode());
        assertEquals(200, post.getStatusCode());
    }

    private String getLastModifiedFromExistingObject() {
        get = new GetMethod();
        get.setPath(HOST_NAME + "/syndication" + OBJECT3_URI);
        String lastModified = "";
        try {
            logger.debug(get.getPath());
            client.executeMethod(get);
            lastModified = get.getResponseHeader("Last-Modified-Time-Stamp").getValue();
        } catch (Exception ex) {
            logger.error("Error fetching object at:  " + SYNDICATION_OBJECT_URI);
        }
        assertEquals(200, get.getStatusCode());
        return lastModified;
    }

    @Test
    public void testUpdateNavigationService() {
        put = new PutMethod();
        put.setPath(HOST_NAME + CREATE_SERVICE_URI + OBJECT_ID);
        try {
            System.out.println(put.getPath());
            Document opml = builder.parse(new File(OPML_FILE));
            RequestEntity entity = new StringRequestEntity(DomUtils.doc2String(opml), "application/xml", "UTF-8");
            put.setRequestEntity(entity);
            client.executeMethod(put);
        } catch (java.io.IOException io) {
            logger.error("IO Error putting opml to:  " + OBJECT_ID);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
        assertEquals(201, put.getStatusCode());
    }


    @Test
    public void testUpdateGeoService() throws  FileNotFoundException {
        PostMethod post = new PostMethod();
        final Session session = TestUtil.openDatabaseSession();
        createObject(OBJECT3_URI, LUFTFOTO_MODS_FILE_For_OBJECT3);

        Object cobject = TestUtil.getCobject(OBJECT3_URI, session);
        logger.info(OBJECT3_URI);
        final double lat = cobject.getPoint().getCoordinate().getX();
        final double lon = cobject.getPoint().getCoordinate().getY();
        post = updateGeoService(post, 10.423, 55.423);
        assertEquals("lat", 10.423, cobject.getPoint().getCoordinate().getX(), 0.1);
        assertEquals("lon", 55.423, cobject.getPoint().getCoordinate().getY(), 0.1);
        revertUpdateGeoService(post, lat, lon);
        assertEquals("lat", lat, cobject.getPoint().getCoordinate().getX(), 0.1);
        assertEquals("lon", lon, cobject.getPoint().getCoordinate().getY(), 0.1);

        deleteObjectIfExists(OBJECT3_URI);
        close(post);
    }

    private void revertUpdateGeoService(PostMethod post, double lat, double lon) {
        updateGeoService(post, lat, lon);
    }

    private PostMethod updateGeoService(PostMethod post, double lat, double lon) {
        post = prepareUpdatePost(post, lat, lon);
        try {
            client.executeMethod(post);
        } catch (java.io.IOException io) {
            logger.error("IO Error posting new geo coordinates to :  " + UPDATE_SERVICE_URI);
            logger.error(io);
        }
        logger.debug("code: " + post.getStatusCode() + " status text" + post.getStatusText());
        assertEquals(200, post.getStatusCode());
        return post;
    }

    private PostMethod prepareUpdatePost(PostMethod post, double lat, double lon) {
        post.setPath(HOST_NAME + UPDATE_SERVICE_URI + OBJECT3_URI);
        post.setParameter("lat", Double.toString(lat));
        post.setParameter("lng", Double.toString(lon));
        post.setParameter("user", "Mr. JUNIT ");
        logger.debug(post.getPath());
        return post;
    }


        @Test
    public void testUpdateGeoServiceFailsWithoutUser() {
        post.setPath(HOST_NAME + UPDATE_SERVICE_URI + OBJECT3_URI);
        post.setParameter("lat", "10.42");
        post.setParameter("lng", "55.42");
        logger.debug(post.getPath());
        try {
            client.executeMethod(post);
        } catch (java.io.IOException io) {
            logger.error("IO Error posting new geo coordinates to :  " + UPDATE_SERVICE_URI);
        }
        logger.debug("code: " + post.getStatusCode() + " status text" + post.getStatusText());
        assertEquals(400, post.getStatusCode());
    }

    private static void close(org.apache.commons.httpclient.HttpMethod method) {
	    method.releaseConnection();
    }

}

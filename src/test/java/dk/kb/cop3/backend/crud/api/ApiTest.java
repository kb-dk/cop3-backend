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
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    private static Logger logger = Logger.getLogger(ApiTest.class);
    private static final String OBJECT_NAME = "object62132";
    private static final String OBJECT2_NAME = "object135334";
    private static final String OBJECT3_NAME = "object182167";
    private static final String OBJECT_PATH = "/images/luftfo/2011/maj/luftfoto/";
    private static final String OBJECT_ID = "/images/luftfo/2011/maj/luftfoto";
    private static final String SYNDICATION_ROOT = "/syndication" + OBJECT_PATH;
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT =
            SYNDICATION_ROOT + OBJECT2_NAME + "?itemsPerPage=10";
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_MODS =
            SYNDICATION_ROOT + OBJECT2_NAME + "?format=mods&itemsPerPage=10";
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_LANG_DA =
            SYNDICATION_ROOT + OBJECT2_NAME + "/da?format=mods&itemsPerPage=10";
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_FRACTION =
            SYNDICATION_ROOT + OBJECT2_NAME + "?random=0.8&itemsPerPage=10";
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_MAX_5 =
            SYNDICATION_ROOT + OBJECT2_NAME + "?itemsPerPage=5";
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_MAX_5_PAGE_2 =
            SYNDICATION_ROOT + OBJECT2_NAME + "?itemsPerPage=5&page=2";
    private static final String BOUNDING_BOX = "10.772781372070312,55.384376628312815,10.331611633300783,55.23587533144054";
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO =
            SYNDICATION_ROOT + OBJECT2_NAME + "?bbo=" + BOUNDING_BOX + "&itemsPerPage=10";
    private final static String SYNDICATION_ALL_OBJECTS_IN_BBO =
            SYNDICATION_ROOT + "?bbo=" + BOUNDING_BOX + "&random=0.8&itemsPerPage=10";
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO_WITH_FREETEXT =
            SYNDICATION_ROOT + "?bbo=" + BOUNDING_BOX + "&query=jensen&itemsPerPage=10";
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO_WITH_FIELDED_SEARCH =
            SYNDICATION_ROOT + "?bbo=" + BOUNDING_BOX + "&query=person:jensen&itemsPerPage=10&itemsPerPage=10";
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO_WITH_YEAR_RANGE =
            SYNDICATION_ROOT + OBJECT2_NAME + "?bbo=" + BOUNDING_BOX + "&notBefore=1939-01-01&notAfter=1945-01-01&itemsPerPage=10";
    private final static String SYNDICATION_ALL_THINGS_COMBINED =
            SYNDICATION_ROOT + OBJECT2_NAME + "?bbo=" + BOUNDING_BOX + "&query=person:sylvest+jensen%26location:fyn&random=0.8&itemsPerPage=10";
    private final static String SYNDICATION_OBJECT = SYNDICATION_ROOT + OBJECT_NAME;
    private final static String SYNDICATION_OBJECT_AS_MODS = SYNDICATION_ROOT + OBJECT_NAME + "/da?format=mods";
    private final static String SYNDICATION_OBJECT_UNKNOWN = SYNDICATION_ROOT + OBJECT_NAME + "/da?format=unknown";
    private final static String CONTENT_OPML = "/content" + OBJECT_PATH + OBJECT_NAME;
    private final static String CONTENT_OPML_LANG_DA = "/content" + OBJECT_PATH + OBJECT_NAME + "/da";
    private final static String NAVIGATION_OPML_FULL = "/navigation" + OBJECT_ID;
    private final static String NAVIGATION_OPML_FULL_DA = "/navigation" + OBJECT_PATH + "da";
    private final static String NAVIGATION_OPML_SUBJECT = "/navigation" + OBJECT_PATH + OBJECT2_NAME;
    private final static String NAVIGATION_OPML_SUBJECT_DA = "/navigation" + OBJECT_PATH + OBJECT2_NAME + "/da";
    private final static String DIRECTORY_SERVICE_OPML = "/directory";
    private final static String CONFIGURATION_SERVICE_PROPERTIES = "/configuration" + OBJECT_ID;
    private final static String UPDATE_OBJECT_SERVICE = "/update";
    private final static String CREATE_OBJECT_SERVICE = "/create";
    private final static String CREATE_UPDATE_NAVIGATION_SERVICE = "/create";
    private final static String CREATE_UPDATE_OBJECT = OBJECT_PATH + OBJECT3_NAME;
    private static final String LUFTFOTO_MODS_FILE = "src/test/resources/testdata/luftfoto_object182167.mods.xml";
    private static final String LUFTFOTO_EDITION = OBJECT_ID;
    private static final String OPML_FILE = "testdata/cumulus-export/Luftfoto_OM/205/categories.xml";

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

    //***********READ TESTS********************//
    // Test names should explain what they are testing

    // GET LISTS OF OBJECTS

    private void testRequest(String request, String returnType, int expectedStatusCode){
        get.setPath(HOST_NAME + request);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching" + returnType + " at:  " + request);
            logger.error(io);
        }
        assertEquals(expectedStatusCode, get.getStatusCode());
    }

    @Test
    public void testSyndicationAllObjectsInSubjectMods() {
        testRequest(SYNDICATION_ALL_OBJECTS_IN_SUBJECT_MODS, "list of objects", 200);
    }

    @Test
    public void testSyndicationAllObjectsInSubject() {
        testRequest(SYNDICATION_ALL_OBJECTS_IN_SUBJECT, "list of objects", 200);
    }


    @Test
    public void testSyndicationAllObjectsInSubjectLangDa() {
        testRequest(SYNDICATION_ALL_OBJECTS_IN_SUBJECT_LANG_DA, "list of objects", 200);
    }

    @Test
    public void testSyndicationAllObjectsInFraction() {
        testRequest(SYNDICATION_ALL_OBJECTS_IN_SUBJECT_FRACTION, "list of objects", 200);
    }

    @Test
    public void testSyndicationAllObjectsInSubjectMax5() {
        testRequest(SYNDICATION_ALL_OBJECTS_IN_SUBJECT_MAX_5, "list of objects", 200);
    }

    @Test
    public void testSyndicationAllObjectsInSubjectInBBO() {
        testRequest(SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO, "list of objects", 200);
    }

    @Test
    public void testSyndicationAllObjectsInBBO() {
        testRequest(SYNDICATION_ALL_OBJECTS_IN_BBO, "list of objects", 200);
    }

    @Test
    public void testSyndicationAllObjectsInSubjectInBBOWithFreeText() {
        testRequest(SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO_WITH_FREETEXT, "list of objects", 200);
    }

    @Test
    public void testSyndicationAllObjectsInSubjectInBBOWithFieldedSearch() {
        testRequest(SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO_WITH_FIELDED_SEARCH, "list of objects", 200);
    }


    @Test
    public void testSyndicationAllObjectsInSubjectInBBOWithYearRange() {
        testRequest(SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO_WITH_YEAR_RANGE, "list of objects", 200);
    }

    @Test
    public void testSyndicationAllThingsCombined() {
        testRequest(SYNDICATION_ALL_THINGS_COMBINED, "list of objects", 200);
    }


    // GET SINGLE OBJECTS

    @Test
    public void testSyndicationObject() {
        testRequest(SYNDICATION_OBJECT, "object", 200);
    }

    @Test
    public void testSyndicationObjectMods() {
        testRequest(SYNDICATION_OBJECT_AS_MODS, "object", 200);
    }

    @Test
    public void testSyndicationObjectUnknown() {
        testRequest(SYNDICATION_OBJECT_UNKNOWN, "object", 404);
    }

    // Navigation service
    @Ignore("Ignored because it seems like navigation service is not used!")
    @Test
    public void testNavigationOpmlFull() {
        testRequest(NAVIGATION_OPML_FULL, "opml", 200);
    }

    @Ignore("Ignored because it seems like navigation service is not used!")
    @Test
    public void testNavigationOpmlFullDa() {
        testRequest(NAVIGATION_OPML_FULL_DA, "opml", 200);
    }

    @Ignore("Ignored because it seems like navigation service is not used!")
    @Test
    public void testNavigationOpmlSubject() {
        testRequest(NAVIGATION_OPML_SUBJECT, "opml", 200);
    }

    @Ignore("Ignored because it seems like navigation service is not used!")
    @Test
    public void testNavigationOpmlSubjectDa() {
        testRequest(NAVIGATION_OPML_SUBJECT_DA, "opml", 200);
    }


    // Content services
    @Test
    public void testContentOpml() {
        testRequest(CONTENT_OPML, "object", 200);
    }

    @Test
    public void testContentOpmlLangDa() {
        testRequest(CONTENT_OPML_LANG_DA, "object", 200);
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
        final Session ses = TestUtil.openDatabaseSession();
        ses.beginTransaction();
        try {
            dk.kb.cop3.backend.crud.database.hibernate.Object cObject = ses.get(Object.class,CREATE_UPDATE_OBJECT);
            if (cObject != null) {
                ses.delete(cObject);
                ses.getTransaction().commit();
            }
        } catch (Exception ex) {
            logger.debug("error "+ex.getMessage());
            ses.getTransaction().rollback();
            assertTrue("testCreateObjectService hibernate error "+ex.getMessage(),false);
        }

        put.setPath(HOST_NAME + CREATE_OBJECT_SERVICE + CREATE_UPDATE_OBJECT);
        try {
            org.w3c.dom.Document dom = builder.parse(new File(LUFTFOTO_MODS_FILE));
            RequestEntity entity = new StringRequestEntity(DomUtils.doc2String(dom), "application/xml", "UTF-8");
            put.setRequestEntity(entity);
            logger.debug(put.getPath());
            client.executeMethod(put);
        } catch (java.io.IOException io) {
            logger.error("IO Error putting object to:  " + CREATE_OBJECT_SERVICE);
            logger.debug(io.getMessage());
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
        assertEquals(201, put.getStatusCode());
    }

    @Test
    public void testUpdateObjectService() throws SAXException {
        client = new HttpClient();
        post = new PostMethod();

        String lastModified = getLastModifiedFromExistingObject();
        String lastModifiedBy = "TEST";

        post.setPath(HOST_NAME + CREATE_OBJECT_SERVICE  + CREATE_UPDATE_OBJECT+ "?lastmodified=" + lastModified + "&user=" + lastModifiedBy);
        logger.info(HOST_NAME + CREATE_OBJECT_SERVICE + CREATE_UPDATE_OBJECT+ "?lastmodified=" + lastModified + "&user=" + lastModifiedBy);
        try {
            org.w3c.dom.Document dom = builder.parse(new File(LUFTFOTO_MODS_FILE));
            RequestEntity entity = new StringRequestEntity(DomUtils.doc2String(dom), "application/xml", "UTF-8");
            post.setRequestEntity(entity);
            client.executeMethod(post);
        } catch (java.io.IOException io) {
            logger.error("IO Error putting object to:  " + UPDATE_OBJECT_SERVICE);
            logger.error(io);
        }
        logger.debug("post.getStatusCode() = " + post.getStatusCode());
        assertEquals(200, post.getStatusCode());
    }

    private String getLastModifiedFromExistingObject() {
        get = new GetMethod();
        get.setPath(HOST_NAME + "/syndication" + CREATE_UPDATE_OBJECT);
        String lastModified = "";
        try {
            logger.debug(get.getPath());
            client.executeMethod(get);
            lastModified = get.getResponseHeader("Last-Modified-Time-Stamp").getValue();
        } catch (Exception ex) {
            logger.error("Error fetching object at:  " + SYNDICATION_OBJECT);
        }
        assertEquals(200, get.getStatusCode());
        return lastModified;
    }

  /*  @Test
    public void testUpdateNavigationService() throws UnsupportedEncodingException, SAXException {
        put = new PutMethod();
        put.setPath(HOST_NAME + CREATE_UPDATE_NAVIGATION_SERVICE + LUFTFOTO_EDITION);
        try {
            System.out.println(put.getPath());
            Document opml = builder.parse(new File(OPML_FILE));
            RequestEntity entity = new StringRequestEntity(DomUtils.doc2String(opml), "application/xml", "UTF-8");
            put.setRequestEntity(entity);
            client.executeMethod(put);
        } catch (java.io.IOException io) {
            logger.error("IO Error putting opml to:  " + LUFTFOTO_EDITION);
        }
        //assertEquals(200, put.getStatusCode());
        assertEquals(201, put.getStatusCode());
    }*/


    @Test
    public void testUpdateGeoService() throws  FileNotFoundException {
        PostMethod post = new PostMethod();
        final Session session = TestUtil.openDatabaseSession();
        Object cobject = TestUtil.getCobject(CREATE_UPDATE_OBJECT, session);
        logger.info(CREATE_UPDATE_OBJECT);
        final double lat = cobject.getPoint().getCoordinate().getX();
        final double lon = cobject.getPoint().getCoordinate().getY();
        post = updateGeoService(post, 10.423, 55.423);
        assertEquals("lat", 10.423, cobject.getPoint().getCoordinate().getX(), 0.1);
        assertEquals("lon", 55.423, cobject.getPoint().getCoordinate().getY(), 0.1);
        revertUpdateGeoService(post, lat, lon);
        assertEquals("lat", lat, cobject.getPoint().getCoordinate().getX(), 0.1);
        assertEquals("lon", lon, cobject.getPoint().getCoordinate().getY(), 0.1);
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
            logger.error("IO Error posting new geo coordinates to :  " + UPDATE_OBJECT_SERVICE);
            logger.error(io);
        }
        logger.debug("code: " + post.getStatusCode() + " status text" + post.getStatusText());
        assertEquals(200, post.getStatusCode());
        return post;
    }

    private PostMethod prepareUpdatePost(PostMethod post, double lat, double lon) {
        post.setPath(HOST_NAME + UPDATE_OBJECT_SERVICE + CREATE_UPDATE_OBJECT);
        post.setParameter("lat", Double.toString(lat));
        post.setParameter("lng", Double.toString(lon));
        post.setParameter("user", "Mr. JUNIT ");
        logger.debug(post.getPath());
        return post;
    }


    /* deleted
        @Test
    public void testUpdateGeoServiceWithNoUser() throws UnsupportedEncodingException {
        post.setPath(HOST_NAME + UPDATE_OBJECT_SERVICE + CREATE_UPDATE_OBJECT);
        post.setParameter("lat", "10.42");
        post.setParameter("lng", "55.42");
        //post.setParameter("user", "Hr. JUNIT ");
        logger.debug(post.getPath());
        try {
            client.executeMethod(post);
        } catch (java.io.IOException io) {
            logger.error("IO Error posting new geo coordinates to :  " + UPDATE_OBJECT_SERVICE);
        }
        logger.debug("code: " + post.getStatusCode() + " status text" + post.getStatusText());
        assertEquals(304, post.getStatusCode());
    }
    */

    private static void close(org.apache.commons.httpclient.HttpMethod method) {
	    method.releaseConnection();
    }

}

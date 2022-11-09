package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.constants.DatacontrollerConstants;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.crud.util.TestUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.*;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.junit.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import static org.junit.Assert.assertEquals;

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
    private static final String OBJECT3_NAME = "object62138";
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
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO =
            SYNDICATION_ROOT + OBJECT2_NAME + "?bbo=-111.032,42.943,-119.856,43.039&itemsPerPage=10";
    private final static String SYNDICATION_ALL_OBJECTS_IN_BBO =
            SYNDICATION_ROOT + "?bbo=-111.032,42.943,-119.856,43.039&random=0.8&itemsPerPage=10";
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO_WITH_FREETEXT =
            SYNDICATION_ROOT + "?bbo=-111.032,42.943,-119.856,43.039&query=jensen&itemsPerPage=10";
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO_WITH_FIELDED_SEARCH =
            SYNDICATION_ROOT + "?bbo=-111.032,42.943,-119.856,43.039&query=person:jensen&itemsPerPage=10&itemsPerPage=10";
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO_WITH_YEAR_RANGE =
            SYNDICATION_ROOT + OBJECT2_NAME + "?bbo=-111.032,42.943,-119.856,43.039&notBefore=1939-01-01&notAfter=1945-01-01&itemsPerPage=10";
    private final static String SYNDICATION_ALL_THINGS_COMBINED =
            SYNDICATION_ROOT + OBJECT2_NAME + "?bbo=-111.032,42.943,-119.856,43.039&query=person:sylvest+jensen%26location:fyn&random=0.8&itemsPerPage=10";
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
    private static final String MODS_FILE = "testdata/cumulus-export/Luftfoto_OM/205/master_records/L0717_04.tif-mods.xml";
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

    @Test
    public void testSyndicationAllObjectsInSubjectMods() {
        get.setPath(HOST_NAME + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_MODS);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching object at:  " + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_MODS);
        }
        assertEquals(200, get.getStatusCode());
    }

    @Test
    public void testSyndicationAllObjectsInSubject() {
        get.setPath(HOST_NAME + SYNDICATION_ALL_OBJECTS_IN_SUBJECT);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching object at:  " + SYNDICATION_ALL_OBJECTS_IN_SUBJECT);
        }
        assertEquals(200, get.getStatusCode());
    }


    @Test
    public void testSyndicationAllObjectsInSubjectLangDa() {
        get.setPath(HOST_NAME + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_LANG_DA);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching object at:  " + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_LANG_DA);
        }
        assertEquals(200, get.getStatusCode());
    }

    @Test
    public void testSyndicationAllObjectsInFraction() {
        get.setPath(HOST_NAME + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_FRACTION);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching object at:  " + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_FRACTION);
        }
        assertEquals(200, get.getStatusCode());
    }

    @Test
    public void testSyndicationAllObjectsInSubjectMax5() {
        get.setPath(HOST_NAME + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_MAX_5);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching object at:  " + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_MAX_5);
        }
        assertEquals(200, get.getStatusCode());
    }

    @Test
    public void testSyndicationAllObjectsInSubjectInBBO() {
        get.setPath(HOST_NAME + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching object at:  " + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO);
        }
        assertEquals(200, get.getStatusCode());
    }

    @Test
    public void testSyndicationAllObjectsInBBO() {
        get.setPath(HOST_NAME + SYNDICATION_ALL_OBJECTS_IN_BBO);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching object at:  " + SYNDICATION_ALL_OBJECTS_IN_BBO);
        }
        assertEquals(200, get.getStatusCode());
    }

    @Test
    public void testSyndicationAllObjectsInSubjectInBBOWithFreeText() {
        get.setPath(HOST_NAME + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO_WITH_FREETEXT);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching object at:  " + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO_WITH_FREETEXT);
        }
        assertEquals(200, get.getStatusCode());
    }

    @Test
    public void testSyndicationAllObjectsInSubjectInBBOWithFieldedSearch() {
        get.setPath(HOST_NAME + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO_WITH_FIELDED_SEARCH);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching object at:  " + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO_WITH_FIELDED_SEARCH);
        }
        assertEquals(200, get.getStatusCode());
    }


    @Test
    public void testSyndicationAllObjectsInSubjectInBBOWithYearRange() {
        get.setPath(HOST_NAME + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO_WITH_YEAR_RANGE);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching object at:  " + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO_WITH_YEAR_RANGE);
        }
        assertEquals(200, get.getStatusCode());
    }

    @Test
    public void testSyndicationAllThingsCombined() {
        get.setPath(HOST_NAME + SYNDICATION_ALL_THINGS_COMBINED);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching object at:  " + SYNDICATION_ALL_THINGS_COMBINED);
        }
        assertEquals(200, get.getStatusCode());
    }


    // GET SINGLE OBJECTS

    @Test
    public void testSyndicationObject() {
        get.setPath(HOST_NAME + SYNDICATION_OBJECT);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching object at:  " + SYNDICATION_OBJECT);
        }
        assertEquals(200, get.getStatusCode());
    }

    @Test
    public void testSyndicationObjectMods() {
        get.setPath(HOST_NAME + SYNDICATION_OBJECT_AS_MODS);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching object at:  " + SYNDICATION_OBJECT_AS_MODS);
        }
        assertEquals(200, get.getStatusCode());
    }


    @Test
    public void testSyndicationObjectUnknown() {
        get.setPath(HOST_NAME + SYNDICATION_OBJECT_UNKNOWN);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching object at:  " + SYNDICATION_OBJECT_UNKNOWN);
        }
        assertEquals(404, get.getStatusCode());
    }

    // Navigation service
    @Ignore("Ignored because it seems like navigation service is not used!")
    @Test
    public void testNavigationOpmlFull() {
        get.setPath(HOST_NAME + NAVIGATION_OPML_FULL);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching opml at:  " + NAVIGATION_OPML_FULL);
        }
        assertEquals(200, get.getStatusCode());
    }

    @Ignore("Ignored because it seems like navigation service is not used!")
    @Test
    public void testNavigationOpmlFullDa() {
        get.setPath(HOST_NAME + NAVIGATION_OPML_FULL_DA);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching opml at:  " + NAVIGATION_OPML_FULL_DA);
        }
        assertEquals(200, get.getStatusCode());
    }

    @Ignore("Ignored because it seems like navigation service is not used!")
    @Test
    public void testNavigationOpmlSubject() {
        get.setPath(HOST_NAME + NAVIGATION_OPML_SUBJECT);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching opml at:  " + NAVIGATION_OPML_SUBJECT);
        }
        assertEquals(200, get.getStatusCode());
    }

    @Ignore("Ignored because it seems like navigation service is not used!")
    @Test
    public void testNavigationOpmlSubjectDa() {
        get.setPath(HOST_NAME + NAVIGATION_OPML_SUBJECT_DA);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching opml at:  " + NAVIGATION_OPML_SUBJECT_DA);
        }
        assertEquals(200, get.getStatusCode());
    }


    // Content services
    @Test
    public void testContentOpml() {
        get.setPath(HOST_NAME + CONTENT_OPML);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching object at:  " + CONTENT_OPML);
        }
        assertEquals(200, get.getStatusCode());
    }

    @Test
    public void testContentOpmlLangDa() {
        get.setPath(HOST_NAME + CONTENT_OPML_LANG_DA);
        logger.debug(get.getPath());
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching object at:  " + CONTENT_OPML_LANG_DA);
        }
        assertEquals(200, get.getStatusCode());
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
  /*  @Test
    public void testCreateObjectService() throws UnsupportedEncodingException, SAXException {

        SessionFactory fact = HibernateUtil.getSessionFactory();
        Session ses = fact.getCurrentSession();
        ses.beginTransaction();
        try {
            dk.kb.cop3.backend.crud.database.hibernate.Object cObject = (dk.kb.cop3.backend.crud.database.hibernate.Object) ses.get(Object.class,CREATE_UPDATE_OBJECT);
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
            org.w3c.dom.Document dom = builder.parse(new File(MODS_FILE));
            RequestEntity entity = new StringRequestEntity(DomUtils.doc2String(dom), "application/xml", "UTF-8");
            put.setRequestEntity(entity);
            logger.debug(put.getPath());
            client.executeMethod(put);
        } catch (java.io.IOException io) {
            logger.error("IO Error putting object to:  " + CREATE_OBJECT_SERVICE);
            logger.debug(io.getMessage());
        }
        assertEquals(201, put.getStatusCode());
    }
    */
    /*
    @Test
    public void testUpdateObjectService() throws  SAXException {
        client = new HttpClient();
        put = new PutMethod();
        get = new GetMethod();

        String lastModified = null;
        get.setPath(HOST_NAME + "/syndication"+CREATE_UPDATE_OBJECT);
        try {
            logger.debug(get.getPath());
            client.executeMethod(get);
            lastModified = get.getResponseHeader("Last-Modified-Time-Stamp").getValue();
        } catch (Exception ex) {
            logger.error("Error fetching object at:  " + SYNDICATION_OBJECT);
        }
        assertEquals(200, get.getStatusCode());

        put.setPath(HOST_NAME + CREATE_OBJECT_SERVICE  + CREATE_UPDATE_OBJECT+ "?lastModified=" + lastModified);
        logger.debug(HOST_NAME + CREATE_OBJECT_SERVICE + CREATE_UPDATE_OBJECT+ "?lastModified=" + lastModified);
        try {
            org.w3c.dom.Document dom = builder.parse(new File(MODS_FILE));
            RequestEntity entity = new StringRequestEntity(DomUtils.doc2String(dom), "application/xml", "UTF-8");
            put.setRequestEntity(entity);
            client.executeMethod(put);
        } catch (java.io.IOException io) {
            logger.error("IO Error putting object to:  " + UPDATE_OBJECT_SERVICE);
        }
        logger.debug("put.getStatusCode() = " + put.getStatusCode());
        assertEquals(200, put.getStatusCode());
    }    */

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

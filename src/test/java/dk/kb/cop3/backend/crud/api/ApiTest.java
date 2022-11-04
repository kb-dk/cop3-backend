package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.constants.DatacontrollerConstants;
import dk.kb.cop3.backend.commonutils.DomUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.*;
import org.apache.log4j.Logger;
// import org.eclipse.jetty.server.Server;
// import org.eclipse.jetty.util.Jetty;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class is the main reference for the supported URI's in the CRUD engine.
 */
public class ApiTest {

    // The webserver
    // private static Jetty jetty = null;

    // The port Jetty will bind to on localhost
    private static int HTTP_PORT = 8080;
    //    static Server jettyServer;

    //The host + port the client will use
    private final static String HOST_NAME = CopBackendProperties.getInstance().getConstants().getProperty("cop3_backend.baseurl");

    // Client methods
    static HttpClient client = new HttpClient();
    static GetMethod get = new GetMethod();
    static PutMethod put = new PutMethod();
    static PostMethod post = new PostMethod();

    private static Logger logger = Logger.getLogger(ApiTest.class);

    private static final String SYNDICATION_ROOT = "/syndication/images/luftfo/2011/maj/luftfoto/";

    // READ SERVICES

    // GET LISTS OF OBJECTS


    // All objects in a subject - as COP1 would use it
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT =
            SYNDICATION_ROOT + "subject203?itemsPerPage=10";

    // All objects in a subject - as COP1 would use it, in native MODS
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_MODS =
            SYNDICATION_ROOT + "subject203?format=mods&itemsPerPage=10";

    // All objects in a subject - as COP1 would use it, get the danish version
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_LANG_DA =
            SYNDICATION_ROOT + "subject203/da?format=mods&itemsPerPage=10";

    // All objects in a subject - as Luftfoto would use it (All objetcs with random number > 0.8)
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_FRACTION =
            SYNDICATION_ROOT + "subject203?random=0.8&itemsPerPage=10";

    // All objects in a subject - as COP1 would use it - max 5 records
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_MAX_5 =
            SYNDICATION_ROOT + "subject203?itemsPerPage=5";

    // All objects in a subject - as COP1 would use it - max 5 records - page 2
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_MAX_5_PAGE_2 =
            SYNDICATION_ROOT + "subject203?itemsPerPage=5&page=2";

    // All objects in a subject within a bounding box
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO =
            SYNDICATION_ROOT + "subject203?bbo=-111.032,42.943,-119.856,43.039&itemsPerPage=10";

    // All objects within a bounding box
    private final static String SYNDICATION_ALL_OBJECTS_IN_BBO =
            SYNDICATION_ROOT + "?bbo=-111.032,42.943,-119.856,43.039&random=0.8&itemsPerPage=10";


    // All objects in a subject within a bounding box with a freetext search on the term 'jensen'
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO_WITH_FREETEXT =
            SYNDICATION_ROOT + "?bbo=-111.032,42.943,-119.856,43.039&query=jensen&itemsPerPage=10";


    // All objects in a subject within a bounding box with a search on persons 'jensen'
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO_WITH_FIELDED_SEARCH =
            SYNDICATION_ROOT + "?bbo=-111.032,42.943,-119.856,43.039&query=person:jensen&itemsPerPage=10&itemsPerPage=10";

    // All objects in a subject within a bounding box with year in between 1939 - 1945
    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO_WITH_YEAR_RANGE =
            SYNDICATION_ROOT + "subject203?bbo=-111.032,42.943,-119.856,43.039&notBefore=1939-01-01&notAfter=1945-01-01&itemsPerPage=10";

    // All objects in a query that combines as much as possible
    private final static String SYNDICATION_ALL_THINGS_COMBINED =
            SYNDICATION_ROOT + "subject203?bbo=-111.032,42.943,-119.856,43.039&query=person:sylvest+jensen%26location:fyn&random=0.8&itemsPerPage=10";


    // GET SINGLE OBJECTS

    // Get the object with identifier /images/luftfo/2011/maj/luftfoto/object62132
    private final static String SYNDICATION_OBJECT = SYNDICATION_ROOT + "object62132";

    // Get the object with identifier /images/luftfo/2011/maj/luftfoto/object62132 as MODS
    private final static String SYNDICATION_OBJECT_MODS = SYNDICATION_ROOT + "object62132/da?format=mods";

    // Get the object with identifier /images/luftfo/2011/maj/luftfoto/object62132 in an unknown format
    private final static String SYNDICATION_OBJECT_UNKNOWN = SYNDICATION_ROOT + "object62132/da?format=unknown";


    // SPECIAL SERVICES

    // Content services
    // GET the TOC of the object with id images/luftfo/2011/maj/luftfoto/object62132
    private final static String CONTENT_OPML = "/content/images/luftfo/2011/maj/luftfoto/object62132";

    // GET the TOC of the object with id images/luftfo/2011/maj/luftfoto/object62132 in danish
    private final static String CONTENT_OPML_LANG_DA = "/content/images/luftfo/2011/maj/luftfoto/object62132/da";


    // Novigation services
    // GET the OPML of full edition
    private final static String NAVIGATION_OPML_FULL = "/navigation/images/luftfo/2011/maj/luftfoto";

    // GET the OPML of full edition in danish
    private final static String NAVIGATION_OPML_FULL_DA = "/navigation/images/luftfo/2011/maj/luftfoto/da";

    // GET the OPML of an edition, with subject 203 as top node
    private final static String NAVIGATION_OPML_SUBJECT = "/navigation/images/luftfo/2011/maj/luftfoto/subject203";

    // GET the OPML of an edition, with subject 203 as top node in danish
    private final static String NAVIGATION_OPML_SUBJECT_DA = "/navigation/images/luftfo/2011/maj/luftfoto/subject203/da";

    // Directory service
    // GET a list of all editions
    private final static String DIRECTORY_SERVICE_OPML = "/directory";

    // Configuration service
    // GET the default configuration for an edition as java.properties file
    private final static String CONFIGURATION_SERVICE_PROPERTIES = "/configuration/images/luftfo/2011/maj/luftfoto";


    // UPDATE / CREATE SERVICES

    // POST an updated object to the server.
    private final static String UPDATE_OBJECT_SERVICE = "/update";

    private final static String CREATE_OBJECT_SERVICE = "/create";

    // PUT a new or updated opml document to the server.
    private final static String CREATE_UPDATE_NAVIGATION_SERVICE = "/create";

    // The test object to create or update
    private final static String CREATE_UPDATE_OBJECT = "/images/luftfo/2011/maj/luftfoto/object62138";

    // The mods file on disk, with id /images/luftfo/2011/maj/luftfoto/object62132
    private static final String MODS_FILE = "testdata/cumulus-export/Luftfoto_OM/205/master_records/L0717_04.tif-mods.xml";

    // The edition to get an updated opml
    private static final String LUFTFOTO_EDITION = "/images/luftfo/2011/maj/luftfoto";

    // the opml file on disk
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
        client.getHttpConnectionManager().getParams().setConnectionTimeout(DatacontrollerConstants.CONN_TIMEOUT);
        client.getHttpConnectionManager().getParams().setSoTimeout(DatacontrollerConstants.CONN_TIMEOUT);

        //jettyServer = jetty.serverInitializedOK(HTTP_PORT);
        //jettyServer.start();
        //factory.setNamespaceAware(true);
        builder = factory.newDocumentBuilder();

    }

    /**
     * Stop the embedded server again
     *
     * @throws Exception
     */
    @AfterClass
    public static void postTest() throws Exception {
 //       jettyServer.stop();
 //       jettyServer.join();

	close(get);
	close(put);
	close(post);
    }

    //***********READ TESTS********************//
    // Test names should explain what they are testing

    // GET LISTS OF OBJECTS

    @Test
    public void testSyndicationAllObjectsInSubject() {
        get.setPath(HOST_NAME + SYNDICATION_ALL_OBJECTS_IN_SUBJECT);
        logger.debug(HOST_NAME + SYNDICATION_ALL_OBJECTS_IN_SUBJECT);


        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching object at:  " + SYNDICATION_ALL_OBJECTS_IN_SUBJECT);
        }
        assertEquals(200, get.getStatusCode());
    }


    @Test
    public void testSyndicationAllObjectsInSubjectMods() {
        get.setPath(HOST_NAME + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_MODS);
        try {
            logger.debug(get.getPath());
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching object at:  " + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_MODS);
        }
        assertEquals(200, get.getStatusCode());
    }


    @Test
    public void testSyndicationAllObjectsInSubjectLangDa() {
        get.setPath(HOST_NAME + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_LANG_DA);
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
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching object at:  " + SYNDICATION_OBJECT);
        }
        assertEquals(200, get.getStatusCode());
    }

    @Test
    public void testSyndicationObjectMods() {
        get.setPath(HOST_NAME + SYNDICATION_OBJECT_MODS);
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching object at:  " + SYNDICATION_OBJECT_MODS);
        }
        assertEquals(200, get.getStatusCode());
    }


    @Test
    public void testSyndicationObjectUnknown() {
        get.setPath(HOST_NAME + SYNDICATION_OBJECT_UNKNOWN);
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching object at:  " + SYNDICATION_OBJECT_UNKNOWN);
        }
        assertEquals(404, get.getStatusCode());
    }


    // SPECIAL SERVICES

    // Navigation service
    @Test
    public void testNavigationOpmlFull() {
        get.setPath(HOST_NAME + NAVIGATION_OPML_FULL);
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching opml at:  " + NAVIGATION_OPML_FULL);
        }
        assertEquals(200, get.getStatusCode());
    }

    @Test
    public void testNavigationOpmlFullDa() {
        get.setPath(HOST_NAME + NAVIGATION_OPML_FULL_DA);
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching opml at:  " + NAVIGATION_OPML_FULL_DA);
        }
        assertEquals(200, get.getStatusCode());
    }

    @Test
    public void testNavigationOpmlSubject() {
        get.setPath(HOST_NAME + NAVIGATION_OPML_SUBJECT);
        try {
            client.executeMethod(get);
        } catch (java.io.IOException io) {
            logger.error("IO Error fetching opml at:  " + NAVIGATION_OPML_SUBJECT);
        }
        assertEquals(200, get.getStatusCode());
    }

    @Test
    public void testNavigationOpmlSubjectDa() {
        get.setPath(HOST_NAME + NAVIGATION_OPML_SUBJECT_DA);
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
    /* */
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
    }  /*  */

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
    public void testUpdateGeoService() throws UnsupportedEncodingException {
        post.setPath(HOST_NAME + UPDATE_OBJECT_SERVICE + CREATE_UPDATE_OBJECT);
        post.setParameter("lat", "10.42");
        post.setParameter("lng", "55.42");
        post.setParameter("user", "Hr. JUNIT ");
        logger.debug(post.getPath());
        try {
            client.executeMethod(post);
        } catch (java.io.IOException io) {
            logger.error("IO Error posting new geo coordinates to :  " + UPDATE_OBJECT_SERVICE);
        }
        logger.debug("code: " + post.getStatusCode() + " status text" + post.getStatusText());
        assertEquals(200, post.getStatusCode());
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

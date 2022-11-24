package dk.kb.cop3.backend.crud.api;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Random;

/**
 *Den eneste brug for denne klasse er at finde den out-of-memory fejl vi roder med.
 *
 */
public class CrashTest {

    //The host + port the client will use
    private final static String HOST_NAME = "localhost:8080/cop";

    // Client methods
    static HttpClient client = new HttpClient();
    static GetMethod get = new GetMethod();
    static PutMethod put = new PutMethod();
    static PostMethod post = new PostMethod();
    Random random = new Random();


    private static Logger logger = Logger.getLogger(CrashTest.class);

    private static final String SYNDICATION_ROOT = "/syndication/images/luftfo/2011/maj/luftfoto/";

    private final static String SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO =
            SYNDICATION_ROOT + "subject203?bbo=10.5,55.1,10.68,55.2&format=rss&itemsPerPage=";

    @BeforeAll
    public static void initTest() throws Exception {
        // 0 = no timeout
        client.getHttpConnectionManager().getParams().setConnectionTimeout(0);
        client.getHttpConnectionManager().getParams().setSoTimeout(0);
    }

    //@Test
    public void testBBO() {
        int j = 1;
        for(int i = 0; i < 2000; i ++){
            int randomNumber = random.nextInt(500 - 5) + 5;

            get.setPath(HOST_NAME + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO + randomNumber);
            logger.debug(j + " ; " + HOST_NAME + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO + randomNumber);
            j++;
            try {
                client.executeMethod(get);
            } catch (java.io.IOException io) {
                logger.error("IO Error fetching object at:  " + SYNDICATION_ALL_OBJECTS_IN_SUBJECT_IN_BBO);
            }
            assertEquals(200, get.getStatusCode());

            get.releaseConnection();

        }

    }




}

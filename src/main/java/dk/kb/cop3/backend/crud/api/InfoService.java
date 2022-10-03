package dk.kb.cop3.backend.crud.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * @author: Andreas B. Westh
 * Date: 10/6/11
 * Time: 14:49 PM
 */
@Path("/")
public class InfoService {

    static final Logger LOGGER = LoggerFactory.getLogger(InfoService.class);
       /**
     * We can use this service to test that the web server is up and running
     * And that Jersey responds to GET requests
     *
     * @return A text string to the http client
     */
    @GET
    @Produces("text/plain")
    public Response getSyndicationHelloWorld() {
        LOGGER.info("Hello again from the COP-02 - Backend");
        return Response.ok("This is still the COP2 Backend").build(); // Unsupported type
    }
}

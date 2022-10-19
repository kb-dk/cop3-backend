package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.util.ValidatorPathParam;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

/**
 * Configuration service -- delivers everything a COP client needs to know
 * in order to access the editions in the directory . ex
 * <p/>
 * http://www.kb.dk/cop/configuration/letters/judsam/2011/mar/dsa
 * <p/>
 * <p/>
 * User: abwe
 * Date: 4/14/11
 * Time: 9:20 AM
 */
@Path("/configuration")
public class ConfigurationService {
    private static Logger myLogger = Logger.getLogger(ConfigurationService.class);
    private static final String dummyResponse = "<?xml version='1.0' encoding='UTF-8'?><rss version=\"2.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:md=\"http://www.loc.gov/mods/v3\" xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns:opensearch=\"http://a9.com/-/spec/opensearch/1.1/\" xmlns:tei=\"http://www.tei-c.org/ns/1.0\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xml=\"http://www.w3.org/XML/1998/namespace\"></channel></rss>";
    @GET
    @Path("/{medium}/{collection}/{year}/{month}/{edition}/")
    @Produces("application/xml")
    public Response getDescription(
            @PathParam("medium") String medium,
            @PathParam("collection") String collection,
            @PathParam("year") int year,
            @PathParam("month") String month,
            @PathParam("edition") String edition,
            @Context HttpServletRequest httpServletRequest,
            @Context UriInfo ui
    ) {
        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
        myLogger.debug("delivers everything a COP client needs to know in order to access the editions in the directory in an rss 2.0 feed \n" +
                "QueryParameters" + queryParams.toString());

        // validate input before connecting to Database
        String xmlString = null;
        if (ValidatorPathParam.validateMonth(month) &&
                ValidatorPathParam.validateYear(year) &&
                ValidatorPathParam.validateStrings(medium, collection, edition)
                ) {
            myLogger.debug("validation okay, all parameters okay. Do DB query");
            // connect to DB
            String editionId = "/" + medium + "/" + collection + "/" + year + "/" + month + "/" + edition;
            SessionFactory fact = HibernateUtil.getSessionFactory();
            Session ses = fact.getCurrentSession();
            ses.beginTransaction();
            Properties configuration = new Properties();

            try {

		myLogger.debug("configurationService no edition: " + editionId);
                Edition editionObject = (Edition) ses.get(Edition.class,editionId);

                if (editionObject != null) {
                    Properties constants = CopBackendProperties.getInstance().getConstants();
                    String baseUrl = constants.getProperty("cop2_backend.baseurl");
                    configuration.setProperty("template",constants.getProperty("template.default"));
                    configuration.setProperty("default_mode","thumbnail");
                    configuration.setProperty("itemsPerPage","40");
                    configuration.setProperty("root_category",editionObject.getCumulusTopCatagory());
                    configuration.setProperty("open_search_target",baseUrl+"/syndication"+editionId);
                    configuration.setProperty("opml_service",baseUrl+"/navigation"+editionId);
                    configuration.setProperty("open_search_description",baseUrl+"/description"+editionId);
                    configuration.setProperty("toc_service",baseUrl+"/content"+editionId);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    configuration.storeToXML(byteArrayOutputStream, editionObject.getDescription());
                    xmlString = byteArrayOutputStream.toString();
                } else
                    myLogger.error("configurationService no edition:"+editionId);

            } catch (Exception ex) {
                myLogger.error("Configuration some error:"+ex.getMessage(),ex);
            }
            ses.flush();
            ses.getTransaction().commit();
            if (ses.isConnected()) {
                myLogger.debug("Closing Hibernate session as we're still connected");
                ses.close();
            }
        }
        if (xmlString != null)
            return Response.ok(xmlString).build();
        else
            return Response.noContent().build();
    }
}


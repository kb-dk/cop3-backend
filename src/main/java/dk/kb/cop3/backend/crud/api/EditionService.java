package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.constants.Formats;
import dk.kb.cop3.backend.crud.database.HibernateEditionSource;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.format.EditionMetadataFormulator;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.w3c.dom.Document;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * # Directory service -- delivers a list of existing editions in OPML ex
 * http://www.kb.dk/cop/directory/
 * User: abwe
 * Date: 4/14/11
 * Time: 9:19 AM
 */
@Path("/editions/")
public class EditionService {
    private static Logger myLogger = Logger.getLogger(EditionService.class);
    private static final String dummyResponse = "<?xml version='1.0' encoding='UTF-8'?><rss version=\"2.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:md=\"http://www.loc.gov/mods/v3\" xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns:opensearch=\"http://a9.com/-/spec/opensearch/1.1/\" xmlns:tei=\"http://www.tei-c.org/ns/1.0\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xml=\"http://www.w3.org/XML/1998/namespace\"></channel></rss>";

    @GET
    @Path("{nn:([^/]+?/)*}{lang:(da|en)?}")
    @Produces("application/xml")
    public Response getEditions(
            @PathParam("lang") String language,
            @DefaultValue("rss") @QueryParam("format") Formats format,
            @QueryParam("query") String query,
            @QueryParam("page") int page,
            @QueryParam("notBefore") String notBefore,
            @QueryParam("notAfter") String notAfter,
            @Context HttpServletRequest httpServletRequest,
            @Context ServletContext servletContext

    ) {

        // Search Query provided. Do Syndication Service
        if (query != null && !query.equals("")) {
            myLogger.debug("Doing wide search from an edition  using Syndication service ");
            SyndicationService syndicationService = new SyndicationService();    // TODO This might cause some problem if we start using the static cache in the SyndicationService.
            Response response = syndicationService.getObjects("syndication", "any", 2009, "jul", "edtions", "", language, format, 0.0d, 40, page, null, query, notBefore, notAfter, "SEARCHWIDE", "all",null, "randomNumber","asc", httpServletRequest, servletContext);
            syndicationService = null;
            return response;

        } else {    // default
            myLogger.debug("Get Editions. ");
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            language = (language.equals("")) ? "da" : language;


            HibernateEditionSource source = new HibernateEditionSource(session);

            EditionMetadataFormulator formulator = new EditionMetadataFormulator();

            formulator.setLanguage(language);
            formulator.setSession(session);
            formulator.setFormat(format.toString());
            formulator.setDataSource(source);
            Document responseDoc = formulator.formulate();
            myLogger.debug("Formulator has returned");


            // editions/any/2009/jul/editions/da/

            Response.ResponseBuilder res = Response.ok(responseDoc);
            myLogger.debug("The response has been built");
            session.beginTransaction().commit();
            return res.build();
        }
    }
}

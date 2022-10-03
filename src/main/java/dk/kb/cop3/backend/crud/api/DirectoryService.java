package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.crud.database.HibernateEditionSource;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.format.DirectoryFormulator;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.w3c.dom.Document;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * # Directory service -- delivers a list of existing editions in OPML ex
 * http://www.kb.dk/cop/directory/
 * User: abwe
 * Date: 4/14/11
 * Time: 9:19 AM
 */
@Path("/directory")
public class DirectoryService {
    private static Logger myLogger = Logger.getLogger(DescriptionService.class);
    private static final String dummyResponse = "<?xml version='1.0' encoding='UTF-8'?><rss version=\"2.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:md=\"http://www.loc.gov/mods/v3\" xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns:opensearch=\"http://a9.com/-/spec/opensearch/1.1/\" xmlns:tei=\"http://www.tei-c.org/ns/1.0\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xml=\"http://www.w3.org/XML/1998/namespace\"></channel></rss>";

    @GET
    @Path("/editions{nn:(/([^/]+?/)*)}{lang:(da|en)?}")
    @Produces("application/xml")
    public Response getDirectory(
            @PathParam("lang") String language,
            @DefaultValue("rss") @QueryParam("format") String format
    ) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        // workaround to fix oracle bug
        SQLQuery sqlQuery = session.createSQLQuery("alter session set optimizer_mode=first_rows");
        sqlQuery.executeUpdate();

        language = (language.equals("")) ? "da" : language;

        HibernateEditionSource source = new HibernateEditionSource(session);

        DirectoryFormulator formulator = new DirectoryFormulator();

        formulator.setLanguage(language);
        formulator.setSession(session);
        formulator.setFormat(format);
        formulator.setDataSource(source);
        Document responseDoc = formulator.formulate();
        myLogger.debug("Formulator has returned");
        Response.ResponseBuilder res = Response.ok(responseDoc);
        myLogger.debug("The response has been built");
        session.flush();
        session.getTransaction().commit();
        if (session.isConnected()){
            myLogger.debug("Closing Hibernate session as we're still connected");
            session.close();
        }

        return res.build();
    }
}


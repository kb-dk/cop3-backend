package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.crud.database.HibernateEditionTool;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.format.DirectoryFormulator;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.w3c.dom.Document;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.xml.transform.TransformerException;

/**
 * # Directory service -- delivers a list of existing editions in OPML ex
 * http://www.kb.dk/cop/directory/
 * User: abwe
 * Date: 4/14/11
 * Time: 9:19 AM
 */
@Path("/directory")
public class DirectoryService {
    private static final Logger myLogger = Logger.getLogger(DescriptionService.class);

    @GET
    @Path("/editions{nn:(/([^/]+?/)*)}{lang:(da|en)?}")
    @Produces("application/xml")
    public Response getDirectory(
            @PathParam("lang") String language,
            @DefaultValue("rss") @QueryParam("format") String format
    ) throws TransformerException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        language = (language.equals("")) ? "da" : language;

        HibernateEditionTool source = new HibernateEditionTool(session);
        DirectoryFormulator formulator = new DirectoryFormulator();

        formulator.setLanguage(language);
        formulator.setFormat(format);
        formulator.setDataSource(source);
        Document responseDoc = formulator.formulate();
        Response.ResponseBuilder res = Response.ok(EditionService.getStringFromDoc(responseDoc));
        if (session.isConnected()){
            session.close();
        }
        return res.build();
    }
}


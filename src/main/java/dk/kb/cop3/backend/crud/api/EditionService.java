package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.constants.Formats;
import dk.kb.cop3.backend.crud.database.HibernateEditionTool;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.format.EditionMetadataFormulator;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.w3c.dom.Document;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

@Path("/editions/")
public class EditionService {
    private static final Logger myLogger = Logger.getLogger(EditionService.class);

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

    ) throws TransformerException {

        // Search Query provided. Do Syndication Service
        // TODO: do we need this
        if (query != null && !query.equals("")) {
            myLogger.debug("Doing wide search from an edition  using Syndication service ");
            SyndicationService syndicationService = new SyndicationService();
            return syndicationService.getObjects("syndication", "any", 2009, "jul", "edtions", "", language, format, 0.0d, 40, page, null, query, notBefore, notAfter, "SEARCHWIDE", "all",null, "randomNumber","asc", httpServletRequest, servletContext);
        } else {    // default
            Session session = HibernateUtil.getSessionFactory().openSession();
            language = (language.equals("")) ? "da" : language;

            HibernateEditionTool source = new HibernateEditionTool(session);
            EditionMetadataFormulator formulator = new EditionMetadataFormulator();

            formulator.setLanguage(language);
            formulator.setSession(session);
            formulator.setFormat(format.toString());
            formulator.setDataSource(source);
            Document responseDoc = formulator.formulate();
            session.close();
            return Response.ok()
                    .type(formulator.mediaType())
                    .entity(formulator.serialize(responseDoc))
                    .build();
        }
    }
}

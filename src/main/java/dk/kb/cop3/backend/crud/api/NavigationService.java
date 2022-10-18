package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.constants.ConfigurableConstants;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import org.apache.log4j.Logger;
import org.apache.xalan.processor.TransformerFactoryImpl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import java.io.StringReader;

/**
 *  This class bind to all navigation services (/navigation)
 *  That is the REST version of the Cumulus category hierarchy
 */
@Path("/navigation")
public class NavigationService {

    // Logger object
    private static Logger logger = Logger.getLogger(NavigationService.class);

    // opml massage xslt
    // TODO: move to configuration
    private static String opml_xslt = "/opml_massage.xsl";
    private static String opml_to_solr_xslt = "/opml2solr.xsl";

    private DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
    private TransformerFactory trans_fact = new TransformerFactoryImpl();

    /**
     * Binds to 4 different way of getting an opml file
     * - /navigation/images/luftfo/2011/maj/luftfoto
     * - /navigation/images/luftfo/2011/maj/luftfoto/da
     * - /navigation/images/luftfo/2011/maj/luftfoto/subject203
     * - /navigation/images/luftfo/2011/maj/luftfoto/subject203/da
     *
     * @param medium
     * @param collection
     * @param year
     * @param month
     * @param edition
     * @param id
     * @param language
     * @param httpServletRequest
     * @param ui
     * @return
     */
    @GET
    @Path("/{medium}/{collection}/{year}/{month}/{edition}{nn:(/)?}{id:(subject[0-9]+?)?}{nn2:(/)?}{lang:(da|en)?}")
    @Produces("application/xml")
    public Response getNavigation(
            @PathParam("medium") String medium,
            @PathParam("collection") String collection,
            @PathParam("year") int year,
            @PathParam("month") String month,
            @PathParam("edition") String edition,
            @PathParam("id") String id,
            @PathParam("lang") String language,
            @QueryParam("mode") String mode,
            @QueryParam("format") String format,
            @Context HttpServletRequest httpServletRequest,
            @Context UriInfo ui
    ) {

        // This is the only way of giving lang a default value
        language = (language.equals("")) ? "da" : language;

        //mode
        mode = "shallow".equals(mode) ? "shallow" : "deep";
        format =  "solr".equals(format) ? "solr"  : "opml";

        // This is what the edition looks like
        String editionId = "/" + medium + "/" + collection + "/" + year + "/" + month + "/" + edition;

        String subjectId = (id.indexOf("subject") > -1) ? editionId + "/" + id : null ;

        // The caching key is the concatenation of everything
        String cacheKey =   "edition:" + editionId + ";" +
                "id:" + id + ";" +
                "lang:" + language +
                "mode:"+mode+";";
        logger.debug(cacheKey);

        Document opml = null;
            Session ses = null;
            try {
                SessionFactory fact = HibernateUtil.getSessionFactory();

                ses = fact.getCurrentSession();
                ses.beginTransaction();
                Edition editionObject = (Edition) ses.get(Edition.class,editionId);
                ses.flush();
                ses.getTransaction().commit();

                if (editionObject != null && editionObject.getOpml() != null) {
                    dfactory.setNamespaceAware(true);
                    javax.xml.parsers.DocumentBuilder dBuilder = dfactory.newDocumentBuilder();
                    // build source opml from hibernate
                    Document sourceOpml =  dBuilder.parse(new InputSource(new StringReader(editionObject.getOpml())));


		    String xsl_file = opml_xslt;
		    if(format.equals("solr")) {
			xsl_file = opml_to_solr_xslt;
		    }

                    Transformer transformer =
                            trans_fact.newTransformer(new javax.xml.transform.stream.StreamSource(this.getClass().getResourceAsStream(xsl_file)));

                    if (id != null) {
                        logger.debug("setting id:"+id.replaceFirst("subject",""));
                        transformer.setParameter("start_node_id",id.replaceFirst("subject",""));
                    }
                    if (mode != null) {
                        transformer.setParameter("mode",mode);
                    }

                    java.util.Properties constants =
			        ConfigurableConstants.getInstance().getConstants();
                    String baseUrl = constants.getProperty("cop2_backend.baseurl");
                    String guiUri = constants.getProperty("gui.uri");
                    String base_url = baseUrl;
                    transformer.setParameter("base_uri",base_url+"/navigation"+editionId);
                    transformer.setParameter("edition_id",editionId);
                    transformer.setParameter("html_base_uri",guiUri + editionId);
                    transformer.setParameter("rss_base_uri",base_url+"/syndication"+editionId);


                    Document opmlResult = dBuilder.newDocument();
                    transformer.transform(new DOMSource(sourceOpml),new DOMResult(opmlResult));
                    opml = opmlResult;
                }

                return Response.ok(opml).build();
            } catch (Exception someEx){ // if getting from DB somehow fails, try to get an older entry from cache
                logger.warn("Error getting opml for "+editionId,someEx);
                return Response.noContent().build(); // This URI has no content.
            } finally {
                if (ses != null && ses.isConnected()){
                    logger.debug("Closing Hibernate session as we're still connected");
                    ses.close();
                }
            }
    }
}

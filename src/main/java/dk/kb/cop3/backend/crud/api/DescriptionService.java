package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import org.apache.xalan.processor.TransformerFactoryImpl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXB;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;

/**
 * Description service -- delivers a description of an edition as an opensearchdescription.
 * ex http://www.kb.dk/cop/description/letters/judsam/2011/mar/dsa
 * Usable in http://opensearch.a9.com/
 * The value of these have been limited since it has been impossible to limit the search to the edition described.
 * User: abwe
 * Date: 4/14/11
 * Time: 10:38 AM
 */
@Path("/description")
public class DescriptionService {
    private static final Logger logger = LoggerFactory.getLogger(DescriptionService.class);

    TransformerFactory trans_fact;
    private DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

    private String xslt;
    private String template;

    public DescriptionService() {
        trans_fact = new TransformerFactoryImpl();
        xslt = "/build_opensearch_description.xsl";
        template = "/template_opensearch_description.xml";
    }

    @GET
    @Path("/{medium}/{collection}/{year}/{month}/{edition}/{lang}/")
    @Produces("application/xml")
    public Response getDescription(
            @PathParam("medium") String medium,
            @PathParam("collection") String coll,
            @PathParam("year") int year,
            @PathParam("month") String month,
            @PathParam("edition") String edition,
            @PathParam("lang") String lang,
            @Context HttpServletRequest httpServletRequest,
            @Context UriInfo ui
    ) {
        String editionId = "/" + medium + "/" + coll + "/" + year + "/" + month + "/" + edition;
        String cacheKey = "edition:" + editionId + ";" + "lang:" + lang + ";";
        logger.debug(cacheKey);

        Transformer transformer = this.makeTransformer();

        Document description = null;
        Document descriptionTemplate = null;


        Session session = null;
        try {

            try {
                dfactory.setNamespaceAware(true);
                DocumentBuilder dBuilder = dfactory.newDocumentBuilder();
                InputStream in = this.getClass().getResourceAsStream(template);
                descriptionTemplate = dBuilder.parse(in);
            } catch (ParserConfigurationException e) {
                logger.error("Error parsing document",e);
            } catch (SAXException e) {
                logger.error("Error parsing document",e);
            } catch (IOException e) {
                logger.error("Error parsing document",e);
            }

            DOMSource source = new DOMSource(descriptionTemplate);
            DOMResult result = new DOMResult();

            SessionFactory fact = HibernateUtil.getSessionFactory();
            session = fact.openSession();
            session.beginTransaction();
            Edition editionObj = (Edition) session.get(Edition.class, editionId);
            session.getTransaction().commit();
            logger.debug("EditionId is: " + editionId);
            //String language = "";

            // If no language is given for the edtion. Take the language parameter from the path
            //if(editionObj.getUiLanguage()== null && (lang.equalsIgnoreCase("da") || lang.equalsIgnoreCase("en") )){
            //   language = lang;  // use language from path
            //}else{
            //    language = editionObj.getUiLanguage();
            //}
            String collection = "";
            String name = "";
            String contactEmail = "";
            String descriptionTxt = "";
            if (!editionId.equalsIgnoreCase("/editions/any/2009/jul/editions")) {
                if (lang.equals("en")) {
                    if (editionObj.getCollectionEn() != null) {
                        collection = editionObj.getCollectionEn();
                    }
                } else {
                    if (editionObj.getCollectionDa() != null) {
                        collection = editionObj.getCollectionDa();
                    }
                }
                if (lang.equals("da")) {
                    if (editionObj.getName() != null) {
                        name = editionObj.getName();
                    }
                } else {
                    if (editionObj.getDescription() != null) {
                        name = editionObj.getDescription();
                    }
                }
                logger.debug("Name is: " + name);
                logger.debug("Collection is: " + collection);
                logger.debug("Short name is: " + editionObj.getUrlName());
                logger.debug(".. description is " + descriptionTxt);
                logger.debug("Language is: " + lang);

                if (editionObj.getContactEmail() != null) {
                    contactEmail = editionObj.getContactEmail();
                }
            } else {    // Frontpage Description
                if (lang.equalsIgnoreCase("en")) {
                    name = "KB's Digital Editions";
                    contactEmail = "helpdesk@kb.dk";
                    descriptionTxt = "The Royal Library's front page for its digital editions.";
                    collection = "The Royal Library's front page for its digital editions.";
                } else {
                    name = "KB's Digitale Udgivelser";
                    contactEmail = "helpdesk@kb.dk";
                    descriptionTxt = "Det Kongelige Biblioteks forside for digitale udgivelser";
                    collection = "Det Kongelige Biblioteks forside for digitale udgivelser";
                }
            }
            transformer.setParameter("long_name", name);
            transformer.setParameter("short_name", edition);
            transformer.setParameter("contact", contactEmail);
            transformer.setParameter("description", descriptionTxt);
            transformer.setParameter("developer", collection);

            transformer.setParameter("language", lang);
            logger.debug("XSL parameters have been set");
            transformer.transform(source, result);
            logger.debug("The transformation is done");
            description = (Document) result.getNode();

            StringWriter sw = new StringWriter();
            JAXB.marshal(description, sw);
            return Response.ok(sw.toString()).build();

        } catch (Exception someEx) { // if getting from DB somehow fails, try to get an older entry from cache
            logger.warn("Error getting description for: " + editionId, someEx);

            logger.warn("We could neither deliver new content or reuse old document");
            return Response.noContent().build(); // This URI has no content.

        } finally {
            if (session != null && session.isConnected()) {
                logger.debug("Closing Hibernate session as we're still connected");
                session.close();
            }
        }

    }

    private Transformer makeTransformer() {

        Transformer transformer = null;
        try {
            logger.debug("XSL path is:" + this.xslt);

            StreamSource streamSource = new StreamSource(this.getClass().getResourceAsStream(xslt));
            transformer = trans_fact.newTransformer(streamSource);
            logger.debug("Transforming using XSL " + this.xslt + " transformer.toString(): " + transformer.toString());
            return transformer;
        } catch (TransformerConfigurationException e) {
            logger.warn("Transformer configuration error", e);
            return null;
        }
    }
}

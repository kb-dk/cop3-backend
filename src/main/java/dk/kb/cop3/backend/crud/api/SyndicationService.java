package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.constants.Formats;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.MetadataSource;
import dk.kb.cop3.backend.crud.database.SolrMetadataSource;
import dk.kb.cop3.backend.crud.format.*;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.w3c.dom.Document;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.math.BigDecimal;


/**
 * Class for binding to all URI's on /syndication
 * - Get single object
 * - Get object by subject, search terms, bounding boxes etc etc
 * - Put new object
 * - Put an update to existing object
 * - Post a new Geo point to an existing object
 */

// The class binds to /syndication
@Path("/syndication")
public class SyndicationService {

    // TODO: do we need this
    @Context
    UriInfo uriInfo;

    private static final Logger logger = Logger.getLogger(SyndicationService.class);

    /**
     * This method binds to all syndication GET services
     * the class ApiTest is the best reference for it
     *
     * @param medium
     * @param collection
     * @param year
     * @param month
     * @param edition
     * @param id
     * @param language
     * @param format
     * @param random
     * @param itemsPerPage
     * @param page
     * @param bbo                bounding box as specified by opensearch
     * @param query
     * @param notBefore
     * @param notAfter
     * @param httpServletRequest
     * @param servletContext
     */

    //@Produces("application/xml")
    @GET
    @Path("/{medium}/{collection}/{year}/{month}/{edition}/{id:(subject[0-9]+?|object[0-9]+?)?}{nn:(/)?}{lang:(da|en)?}")
    public Response getObjects(
            @PathParam("medium") String medium,
            @PathParam("collection") String collection,
            @PathParam("year") int year,
            @PathParam("month") String month,
            @PathParam("edition") String edition,
            @PathParam("id") String id,
            @PathParam("lang") String language,
            @DefaultValue("rss") @QueryParam("format") Formats format,
            @DefaultValue("0.0") @QueryParam("random") double random,
            @DefaultValue("40") @QueryParam("itemsPerPage") int itemsPerPage,
            @DefaultValue("-1") @QueryParam("page") int page,
            @QueryParam("bbo") String bbo,
            @QueryParam("query") String query,
            @QueryParam("notBefore") String notBefore,
            @QueryParam("notAfter") String notAfter,
            @QueryParam("searchWide") String searchwide,   // ABWE OKT 2011: THIS IS THE WORST cgi-param hacks in the history of modern computing
            @QueryParam("type") String type,               // hvilken type ønsker man at søge efter, skrå-,lod eller protokol type 1,2,3.
            @QueryParam("correctness") String correctness, // DGJ: Afgrænse til foto der står korreket eller ikke
            @QueryParam("orderBy") String orderBy,
            @QueryParam("sortOrder") String sortOrder,
            @Context HttpServletRequest httpServletRequest,
            @Context ServletContext servletContext
    ) {
        // This is the only way of giving lang a default value
        language = (language.equals("")) ? "da" : language;

        // This is what the edition looks like
        String editionId = "/" + medium + "/" + collection + "/" + year + "/" + month + "/" + edition;

        String subjectId = (id.contains("subject")) ? editionId + "/" + id : null;
        String objectId = (id.contains("object")) ? editionId + "/" + id : null;

        Document responseDoc;
        Session session = null;
        try {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();
            MetadataSource mds = new SolrMetadataSource(session);
            // give the parameters to the metadatasource
            performSearch(id, language, random, itemsPerPage, page, bbo, query, notBefore, notAfter, searchwide, type,
                    correctness, orderBy, sortOrder, editionId, subjectId, objectId,
                    session, mds);


            // Formulate the response
            MetadataFormulator mdf = getMetadataFormulator(format);
            mdf.setServletContext(servletContext);
            mdf.setRequest(httpServletRequest);
            mdf.setDataSource(mds);
            responseDoc = mdf.formulate();
            String rDoc = getStringFromDoc(responseDoc);
            if (format == Formats.osd) {
                rDoc = rDoc.replaceAll("</?[^>]*>", "");
            }

            Response.ResponseBuilder res = Response.ok(rDoc);
            res.type(mdf.mediaType());
            res.header("Last-Modified-Time-Stamp", mdf.getLastModifiedTimeStamp());
            return res.build();
        } catch (Exception e) {
            logger.error("Error getting objects", e);
            return Response.status(500).build();
        } finally {
            if (session != null && session.isConnected()) {
                session.close();
            }
        }
    }

    private void performSearch(String id, String language,
                               double random, int itemsPerPage, int page, String bbo, String query,
                               String notBefore, String notAfter, String searchwide, String type,
                               String correctness, String orderBy, String sortOrder, String editionId,
                               String subjectId, String objectId,
                               Session session, MetadataSource mds) {
        mds.setBoundingBox(bbo);
        if (subjectId != null) {
            // quick fix for problem with category identifiers containing lang
            // always append '/da/'
            logger.debug("setting subject id: " + subjectId + "/" + language + "/");
            mds.setCategory(subjectId + "/" + language + "/");
        }
        if (objectId != null) {
            logger.debug("setting Searchterms id: " + editionId + "/" + id + "/");
            mds.setSearchterms("id", editionId + "/" + id);
        }

        if (searchwide == null || searchwide.equals("")) {   // ABWE OKT 2011: THIS IS THE WORST cgi-param hacks in the history of modern computing
            logger.debug("searching for edition.");
            mds.setEdition(editionId);
        }
        if (notAfter != null) {
            mds.setNotAfter(notAfter);
        }
        if (notBefore != null) {
            mds.setNotBefore(notBefore);
        }

        if (type != null && !type.equals("all")) { // hvis typen er angivet. Skråfoto, lodfoto eller protokol side. Så kan den angives og søgningen begrænses hermed til f.eks. protokolsider.
            mds.setType(type);
        }

        if (correctness != null) {
            mds.setCorrectness(new BigDecimal(correctness));
        }

        mds.setNumberPerPage(itemsPerPage);

        if (page > 0) {
            mds.setOffset(calculateOffSet(page, itemsPerPage));
        } else {
            mds.setOffset(0);
        }

        mds.setRandom(random);
        if (query != null && !query.equals("")) {
            setQueryParams(query, mds);
        }

        if (orderBy != null && !orderBy.equals("")) {
            mds.setSortcolumn(orderBy);
        }

        if (sortOrder != null && sortOrder.equals("-1")) {
            mds.setSortorder(-1);
        }

        if (sortOrder != null && sortOrder.equals("1")) {
            mds.setSortorder(1);
        }
        mds.execute();
        session.flush();
        session.getTransaction().commit();
    }


    private String getStringFromDoc(Document doc) throws TransformerException {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            writer.flush();
            return writer.toString();
    }

    /**
     * Return the right MetaDataFormulator
     *
     * @param format
     * @return a formulator or null if format is unknown
     */
    private MetadataFormulator getMetadataFormulator(Formats format) {
        switch (format) {
            case osd:
                return new OsdMetadataFormulator();
            case rss:
                return new RssMetadataFormulator();
            case atom:
                return new AtomMetadataFormulator();
            case mods:
                return new ModsMetadataFormulator();
            case kml:
                return new KmlMetadataFormulator();
            case solr:
                return new SolrMetadataFormulator();
            default:
                return new ModsMetadataFormulator();
        }
    }

    /**
     * Little helper method to calculate the offset of a search result
     *
     * @param pageNumber
     * @param itemsPerPage
     * @return the offset
     */
    private int calculateOffSet(int pageNumber, int itemsPerPage) {
        return (pageNumber * itemsPerPage + 1) - itemsPerPage;
    }

    /**
     * Query parser function
     * sets field:term and type
     * example query: person:sylvest+jensen%26location:fyn%26type:Skråfoto
     *
     * @param query query from request
     * @param mds the current metadatasource
     */
    private void setQueryParams(String query, MetadataSource mds) {
        if (query.contains("&")) { // case more than one term
            String[] terms = query.split("&");
            for (String term : terms) {
                addTerm(term, mds);
            }
        } else { // only on term!
            addTerm(query, mds);
        }
    }

    /**
     * UNTESTET add terms to metadatasource
     *
     * @param term
     * @param mds
     */
    private void addTerm(String term, MetadataSource mds) {

        if (!term.contains(":")) { // fritekst søgning
            mds.setSearchterms(term);
        } else {
            mds.setSearchterms(
                    term.substring(0, term.indexOf(":")),
                    term.substring(term.indexOf(":") + 1));
        }
    }
}



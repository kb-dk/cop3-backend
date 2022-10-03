package dk.kb.cop3.backend.crud.api;

import com.opensymphony.oscache.base.CacheEntry;
import dk.kb.cop3.backend.commonutils.CachebleResponse;
import dk.kb.cop3.backend.constants.Formats;
import dk.kb.cop3.backend.crud.cache.CacheManager;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.MetadataSource;
import dk.kb.cop3.backend.crud.database.SolrMetadataSource;
import dk.kb.cop2.backend.crud.format.*;
import dk.kb.cop3.backend.crud.format.*;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.w3c.dom.Document;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
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

    // The cache manager
    private CacheManager manager = CacheManager.getInstance();

    private static Logger logger = Logger.getLogger(SyndicationService.class);

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
     * @return
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

        // The caching key is the concatenation of everything
        String cacheKey = String.format(
                "syndication:edition:%s;id:%s;lang:%s;format:%s;random:%s;itemsPerPage:%d;page:%d;bbo:%s;query:%s;notBefore:%s;notAfter:%s;",
                editionId, id, language, format, random, itemsPerPage, page, bbo, query, notBefore, notAfter);
        logger.debug("Cache key: " + cacheKey);

        // Get the entry from cache, if it is not more than 15 min old (CacheManager.SHORT_LIVING_OBJECT)
        CachebleResponse cacheRes = manager.get(cacheKey, CacheManager.SHORT_LIVING_OBJECT);
        Response.ResponseBuilder responseBuilder = null;
        Document responseDoc = null;
        MetadataFormulator mdf = getMetadataFormulator(format);
        Session session = null;

        if (cacheRes != null) { // We have the object in the cache, return it
            responseBuilder = Response.ok(cacheRes.getDoc());
	    responseBuilder.type(mdf.mediaType());
            responseBuilder.header("Last-Modified-Time-Stamp", cacheRes.getLastModifiedDate());
            return responseBuilder.build();

        } else {
            try {
                // Get the object(s) from database

                // However, if the medium is 'edition' we have a special
                // case. Eeeh. That was no good idea.

                SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
                session = sessionFactory.openSession();

                session.beginTransaction();
                // workaround to fix oracle bug
                SQLQuery sqlQuery = session.createSQLQuery("alter session set optimizer_mode=first_rows");
                sqlQuery.executeUpdate();

                MetadataSource mds = new SolrMetadataSource(session);
                // give the parameters to the metadatasource
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

                if(searchwide == null || searchwide.equals("")){   // ABWE OKT 2011: THIS IS THE WORST cgi-param hacks in the history of modern computing
                    logger.debug("searching for edition.");
                    mds.setEdition(editionId);
                }
                if (notAfter != null ) {
                    mds.setNotAfter(notAfter);
                }
                if (notBefore != null ) {
                    mds.setNotBefore(notBefore);
                }

                if(type != null && !type.equals("all")){ // hvis typen er angivet. Skråfoto, lodfoto eller protokol side. Så kan den angives og søgningen begrænses hermed til f.eks. protokolsider.
                    mds.setType(type);
                }

                if(correctness != null) {
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

                if(orderBy != null && !orderBy.equals("")){
                    mds.setSortcolumn(orderBy);
                }

                if (sortOrder != null && sortOrder.equals("-1")) {
                   mds.setSortorder(-1);
                }

                if (sortOrder != null && sortOrder.equals("1")) {
                    mds.setSortorder(1);
                }

                try {
                    logger.debug("staring search");
                    Long start_time = System.currentTimeMillis();
                    mds.execute(); // Her kastes exceptions
                    logger.debug("search ended "+(System.currentTimeMillis() - start_time)/1000L);
                    session.flush();
                    session.getTransaction().commit();
                } catch (Exception e) {
                    logger.error("An exception occurred while executing the MetaDataSource" +  e.getLocalizedMessage());
                    // error in search return status 500
                    return Response.status(500).build();
                }

                // Formulate the response

                mdf.setServletContext(servletContext);
                mdf.setRequest(httpServletRequest);
                mdf.setDataSource(mds);

                // Formulate the cacheble DOM doc.
		        responseDoc = mdf.formulate();
                String rDoc = getStringFromDoc(responseDoc);

		if(format == Formats.osd) {
		    rDoc = rDoc.replaceAll("</?[^>]*>","");
		} 

                CachebleResponse newRes = new CachebleResponse(responseDoc, mdf.getLastModifiedTimeStamp());

                // Put it in the cache
                manager.put(cacheKey, newRes); // put it in the cache
                // return the doc

                Response.ResponseBuilder res = Response.ok(rDoc);
		        res.type(mdf.mediaType());
                res.header("Last-Modified-Time-Stamp", newRes.getLastModifiedDateAsNumber());
                return res.build();
            } catch (Exception e) { // if getting from DB somehow fails, try to get an older entry from cache
                logger.error("Exception while getting objects from the database", e);
                cacheRes = manager.get(cacheKey, CacheEntry.INDEFINITE_EXPIRY);
                if (cacheRes !=null && cacheRes.getDoc() != null) {
                    responseBuilder = Response.ok(cacheRes.getDoc());
		            responseBuilder.type(mdf.mediaType());
                    responseBuilder.header("Last-Modified-Time-Stamp", cacheRes.getLastModifiedDate());
                    return responseBuilder.build();
                } else {
                    return Response.noContent().build(); // This URI has no content.
                }
            } finally {
                logger.debug("In finally block, attempting to close Hibernate resources...");
                logger.debug("session != null: " + session != null);
                if (session != null && session.isConnected()){
                    logger.debug("Closing Hibernate session as we're still connected");
                    session.cancelQuery();
                    session.close();
                }
                logger.debug("Exiting finally block...");
            }
        }
    }

    private String getStringFromDoc(org.w3c.dom.Document doc) {
	try {
	    javax.xml.transform.dom.DOMSource domSource = new javax.xml.transform.dom. DOMSource(doc);
	    java.io.StringWriter writer = new  java.io.StringWriter();
	    javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(writer);
	    javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
	    javax.xml.transform.Transformer transformer = tf.newTransformer();
	    transformer.transform(domSource, result);
	    writer.flush();
	    return writer.toString();
        }
        catch(javax.xml.transform.TransformerException ex)
	    {
		ex.printStackTrace();
		return null;
	    }
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
                return null;
        }
    }

    /**
     * Little helper method to calculate the offset of a search result
     *
     * @param pageNumber
     * @param itemsPerPage
     * @return
     */
    private int calculateOffSet(int pageNumber, int itemsPerPage) {
        return (pageNumber * itemsPerPage + 1) - itemsPerPage;
    }

    /**
     * Query parser function
     * sets field:term and type
     * example query: person:sylvest+jensen%26location:fyn%26type:Skråfoto
     *
     * @param query
     * @param mds
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
        } else if (term.contains(":")) {  // open search syntax i query feltet.
            mds.setSearchterms(term.substring(0, term.indexOf(":")),
                    term.substring(term.indexOf(":") + 1, term.length()));
        }
    }
}



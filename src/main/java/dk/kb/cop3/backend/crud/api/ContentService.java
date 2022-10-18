package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.crud.cache.CacheManager;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.MetadataSource;
import dk.kb.cop3.backend.crud.database.SolrMetadataSource;
import dk.kb.cop3.backend.crud.format.ContentMetadataFormulator;
import dk.kb.cop3.backend.crud.format.MetadataFormulator;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.w3c.dom.Document;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


/**
 * Class that delivers Table of Contents to the world (/content)
 */
@Path("/content")
public class ContentService {

    private static Logger logger = Logger.getLogger(ContentService.class);

    // The Cache manager
    private CacheManager manager = CacheManager.getInstance();

    /**
     *
     * @param medium
     * @param collection
     * @param year
     * @param month
     * @param edition
     * @param id
     * @param language
     * @param httpServletRequest
     * @param servletContext
     * @param ui
     * @return
     */

    @GET
    @Path("/{medium}/{collection}/{year}/{month}/{edition}/{id}{nn:(/)?}{lang:(da|en)?}")
    @Produces("application/xml")
    public Response getContent(
            @PathParam("medium") String medium,
            @PathParam("collection") String collection,
            @PathParam("year") int year,
            @PathParam("month") String month,
            @PathParam("edition") String edition,
            @PathParam("id") String id,
            @PathParam("lang") String language,
            @Context HttpServletRequest httpServletRequest,
            @Context ServletContext servletContext,
            @Context UriInfo ui
    ) {
       // This is the only way of giving lang a default value
        language = (language.equals("")) ? "da" : language;

        // This is what the edition looks like
        String editionId = "/" + medium + "/" + collection + "/" + year + "/" + month + "/" + edition;

        String objectId = (id.indexOf("object") > -1) ? editionId + "/" + id : null;

        Document responseDoc =  null;

        if(responseDoc != null) { // We have the object in the cache, return it
            return Response.ok(responseDoc).build();
        } else {
            Session session = null;
            try {
                // Get the object(s) from database
                session = HibernateUtil.getSessionFactory().getCurrentSession();
                session.beginTransaction();
                // workaround to fix oracle bug
                SQLQuery sqlQuery = session.createSQLQuery("alter session set optimizer_mode=first_rows");
                sqlQuery.executeUpdate();
                MetadataSource mds = new SolrMetadataSource(session);

                if (objectId != null){
                        logger.debug("objid:  " + objectId);
                        logger.debug("id calculated:" + editionId + "/" + id );
                        mds.setSearchterms("id",editionId + "/" + id  );
                }
                logger.debug("edjid:  " + editionId);
                mds.setEdition(editionId);

                mds.setRandom(0.0);

                try{
                    mds.execute(); // Her kastes exceptions
                } catch (Exception e){
                    logger.fatal("Error trying to build Content.");
                    throw e;
                }

                // Formulate the response

                MetadataFormulator mdf = new ContentMetadataFormulator();
                mdf.setServletContext(servletContext);
                mdf.setRequest(httpServletRequest);
                mdf.setDataSource(mds);
                responseDoc = mdf.formulate();

                session.flush();
                session.getTransaction().commit();

                return Response.ok(responseDoc).build();
            } catch (Exception someEx){ // if getting from DB somehow fails, try to get an older entry from cache
                logger.error("" + someEx + "  " + someEx.getMessage());
                responseDoc = null;// manager.get(cacheKey, CacheEntry.INDEFINITE_EXPIRY);

                if(responseDoc != null){
                    return Response.ok(responseDoc).build(); // An older version existed. Return that
                } else {
                    return Response.noContent().build(); // This URI has no content.
                }
            } finally {
                if (session != null && session.isConnected()){
                    logger.debug("Closing Hibernate session as we're still connected");
                    session.close();
                }
            }
        }
    }
}

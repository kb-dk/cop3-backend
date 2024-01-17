package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.crud.database.HibernateMetadataWriter;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.MetadataWriter;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.solr.CopSolrClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;



// The class binds to /create

@Path("/create")
public class CreateService {
    private static final Logger logger = LoggerFactory.getLogger(CreateService.class);

    @GET
    @Path("/{medium}/{collection}/{year}/{month}/{edition}/{id}")
    public Response createCobjectFromMods(@PathParam("medium") String medium,
                                          @PathParam("collection") String collection,
                                          @PathParam("year") int year,
                                          @PathParam("month") String month,
                                          @PathParam("edition") String edition,
                                          @PathParam("id") String id) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        String idFromRequest = "/" + medium + "/" + collection + "/" + year + "/" + month + "/" + edition + "/" + id;
        try {
            Transaction tx = session.beginTransaction();
            Object object = session.get(Object.class, idFromRequest);
            tx.commit();
            if (object != null) {
                return Response.ok(object.getMods())
                        .header("Last-Modified-Time-Stamp", object.getLastModified())
                        .type(MediaType.APPLICATION_XML_TYPE)
                        .build();
            }
            return Response.status(HttpStatus.SC_NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error getting object", e);
            return Response.serverError().build();
        } finally {
            session.close();
        }
    }


    /**
     * Put-service. Receive som data, and try saving it to db.
     * PUT <a href="http://localhost:8080/cop/syndication/SOMEOBJECT">...</a> (the path is not final)
     *
     * @return
     */
    @PUT
    @Path("/{medium}/{collection}/{year}/{month}/{edition}/{id}")
    public Response createCobjectFromMods(@PathParam("medium") String medium,
                                          @PathParam("collection") String collection,
                                          @PathParam("year") int year,
                                          @PathParam("month") String month,
                                          @PathParam("edition") String edition,
                                          @PathParam("id") String id,
                                          @Context HttpServletRequest httpServletRequest,
                                          String mods) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        String objectId;
        try {
            MetadataWriter mdw = new HibernateMetadataWriter(session);
            objectId = mdw.createFromMods(mods);
        } catch (Exception e) {
            logger.error("Error creating object in database ", e);
            session.close();
            return Response.serverError().entity("Error creating object in database").build();
        }

        Response response = checkMetadatawriterResponse(objectId);
        if (response != null) {
            session.close();
            return response;
        }

        CopSolrClient solrHelper = new CopSolrClient(session);
        if (solrHelper.updateCobjectInSolr(objectId,true)) {
            session.close();
            return Response.ok().build();
        } else {
            session.delete(objectId,Object.class);
            session.close();
            return Response.serverError().entity("error updating solr "+solrHelper.getErrorMsg()).build();
        }

    }

    @POST
    @Path("/{medium}/{collection}/{year}/{month}/{edition}/{id}")
    public Response updateExistingObjectFromMods(@PathParam("medium") String medium,
                                                 @PathParam("collection") String collection,
                                                 @PathParam("year") int year,
                                                 @PathParam("month") String month,
                                                 @PathParam("edition") String edition,
                                                 @PathParam("id") String id,
                                                 @QueryParam("lastmodified") String lastModified,
                                                 @QueryParam("user") String user,
                                                 @Context HttpServletRequest httpServletRequest,
                                                 String mods) {
        String objectIdFromRequest = "/" + medium + "/" + collection + "/" + year + "/" + month + "/" + edition + "/" + id;
        Session session = HibernateUtil.getSessionFactory().openSession();
        String lastmodifiedReturned;
        try {
            MetadataWriter mdw = new HibernateMetadataWriter(session);
            lastmodifiedReturned = mdw.updateFromMods(objectIdFromRequest, mods, lastModified, user);
        } catch (HibernateException ex) {
            logger.error("Cannot create object from mods", ex);
            session.close();
            return Response.serverError().entity("error").build();
        }

        Response response = checkMetadatawriterResponse(lastmodifiedReturned);
        if (response != null) {
            // something went wrong, return
            session.close();
            return response;
        }

        CopSolrClient solrHelper = new CopSolrClient(session);
        if (solrHelper.updateCobjectInSolr(objectIdFromRequest,true)) {
            session.close();
            return Response.ok().build();
        } else {
            session.close();
            return Response.serverError().entity("error updating solr "+solrHelper.getErrorMsg()).build();
        }
    }

    private Response checkMetadatawriterResponse(String objectId) {
        if (StringUtils.isEmpty(objectId) || "error".equals(objectId) ||
                "ids-do-not-match".equals(objectId)) {
            logger.error("unable to update from mods");
            return Response.serverError().entity(objectId).build();
        }
        if (objectId.equals("out-of-date")) {
            return Response.notModified("out-of-date").build();
        }
        if ("id-not-found".equals(objectId)) {
            return Response.status(HttpStatus.SC_NOT_FOUND)
                    .entity("object not found")
                    .build();
        }
        if ("conflict".equals(objectId)) {
            return Response.status(409).entity("conflict").build();
        }
        return null;

    }

    @PUT
    @Path("/{medium}/{collection}/{year}/{month}/{edition}")
    public Response putNavigation(
            @PathParam("medium") String medium,
            @PathParam("collection") String collection,
            @PathParam("year") int year,
            @PathParam("month") String month,
            @PathParam("edition") String edition,
            String opml) {

        String editionId = "/" + medium + "/" + collection + "/" + year + "/" + month + "/" + edition;
        logger.info("PUT Opml Edition ID" + editionId + "opml " + opml);

        Session ses = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = ses.beginTransaction();
        try {
            Edition editionObject = ses.get(Edition.class, editionId);
            if (editionObject == null) {
                logger.warn("Edition " + editionId + " not found");
                ses.close();
                return Response.status(404).build();
            }
            editionObject.setOpml(opml);
            logger.info("updating opml");
            ses.update(editionObject);
            transaction.commit();
        } catch (HibernateException ex) {
            logger.warn("Error updating OPML " + ex.getMessage());
            transaction.rollback();
            ses.close();
            return Response.status(500).build();
        }
        CopSolrClient solrClient = new CopSolrClient(ses);
        solrClient.updateCategoriesSolrForEdition(editionId);
        if(ses.isOpen()) {
            ses.close();
        }
        return Response.status(201).build();
    }
}

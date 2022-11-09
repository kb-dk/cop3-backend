package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.crud.database.HibernateMetadataWriter;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.MetadataWriter;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.solr.SolrHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URI;


// The class binds to /create

@Path("/create")
public class CreateService {
    private static Logger logger = Logger.getLogger(CreateService.class);

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
     * PUT http://localhost:8080/cop/syndication/SOMEOBJECT (the path is not final)
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
        try {
            MetadataWriter mdw = new HibernateMetadataWriter(session);
            String objectId = mdw.createFromMods(mods);
            if ("conflict".equals(objectId)) {
                return Response.status(HttpStatus.SC_CONFLICT)
                        .entity("object allready exists")
                        .build();
            }
            if ("error".equals(objectId)) {
                return Response.serverError().entity("error creating object").build();
            }
            return updateSolrAndReturnReponse(objectId);
        } finally {
            session.close();
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
        String idFromRequest = "/" + medium + "/" + collection + "/" + year + "/" + month + "/" + edition + "/" + id;
        Session session = HibernateUtil.getSessionFactory().openSession();
        String objectId;
        try {
            MetadataWriter mdw = new HibernateMetadataWriter(session);
            objectId = mdw.updateFromMods(idFromRequest, mods, lastModified, user);
        } catch (HibernateException ex) {
            logger.error("Cannot create object from mods", ex);
            return Response.serverError().entity("error").build();
        } finally {
            session.close();
        }
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
        return updateSolrAndReturnReponse(objectId);

    }

    private static Response updateSolrAndReturnReponse(String objectId) {
        try {
            SolrHelper.indexCopObject(objectId);
        } catch (XPathExpressionException | SolrServerException | IOException e) {
            logger.error("Error updating solr", e);
            return Response.serverError().entity("Error updating solr").build();
        }
        return Response.ok(objectId).build();
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

        SessionFactory fact = HibernateUtil.getSessionFactory();
        Session ses = fact.getCurrentSession();
        ses.beginTransaction();
        try {
            Edition editionObject = (Edition) ses.get(Edition.class, editionId);
            if (editionObject == null) {
                logger.warn("Edition " + editionId + " not found");
                ses.close();
                return Response.status(404).build();
            }
            editionObject.setOpml(opml);
            ses.update(editionObject);
        } catch (HibernateException ex) {
            logger.warn("Error updating OPML " + ex.getMessage());
            ses.getTransaction().rollback();
            ex.printStackTrace();
            ses.close();
            return Response.status(500).build();
        }
        ses.getTransaction().commit();
        return Response.status(201).build();
    }
}

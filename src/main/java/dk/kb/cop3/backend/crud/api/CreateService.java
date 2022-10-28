package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.crud.database.HibernateMetadataWriter;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.MetadataWriter;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.util.ObjectFromModsExtractor;
import dk.kb.cop3.backend.crud.util.JMSProducer;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.jms.JMSException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.URI;


// The class binds to /create
@Path("/create")
public class CreateService {
    private static Logger logger = Logger.getLogger(CreateService.class);
    private CopBackendProperties consts = CopBackendProperties.getInstance();

    /**
     * Put-service. Receive som data, and try saving it to db.
     * PUT http://localhost:8080/cop/syndication/SOMEOBJECT (the path is not final)
     *
     * @return
     */
    @PUT
    @Path("/{medium}/{collection}/{year}/{month}/{edition}/{id}")
    public Response post(@PathParam("medium") String medium,
                         @PathParam("collection") String collection,
                         @PathParam("year") int year,
                         @PathParam("month") String month,
                         @PathParam("edition") String edition,
                         @PathParam("id") String id,
                         @QueryParam("lastModified") String lastModified,
                         @Context HttpServletRequest httpServletRequest,
                         String doc) {

        String uri = "/" + medium + "/" + collection + "/" + year + "/" + month + "/" + edition + "/" + id;
        String editionId = "/" + medium + "/" + collection + "/" + year + "/" + month + "/" + edition;
        Object cobject = new Object();

        ObjectFromModsExtractor objectFromModsExtractor = ObjectFromModsExtractor.getInstance();
        SessionFactory fact = HibernateUtil.getSessionFactory();
        Session session = fact.getCurrentSession();
//        session.beginTransaction();

        try {
            MetadataWriter mdw = new HibernateMetadataWriter(session);
            if (lastModified != null) {
                Object nytCobjectFraMods = objectFromModsExtractor.extractFromMods(cobject, doc, session);
                session.evict(cobject);
                String newLastModified = "";
                newLastModified = mdw.updateCobject(nytCobjectFraMods, lastModified);

                if (newLastModified != null && !newLastModified.equals("")) {
                    session.getTransaction().commit();
                    this.sendToSolr(uri);
                    return Response.ok("Updated").build();
                } else {
                    session.getTransaction().rollback();
                    return Response.notModified("wrong lastModifiedDate provided").build();
                }
            } else {
                Object nytFraMods = objectFromModsExtractor.extractFromMods(cobject, doc, session);
                String newLastModified = "";
                newLastModified = mdw.create(nytFraMods);
                if (newLastModified != null || !newLastModified.equals("")) {
                    try {
                        session.getTransaction().commit();
                        sendToSolr(uri);
                        return Response.created(URI.create(nytFraMods.getId())).build();
                    } catch (Exception e) {
                        e.printStackTrace();
                        session.getTransaction().rollback();
                        logger.error("COULD NOT CREATE NEW COBJECT from mods: " + "\n***********\n " + nytFraMods.getMods() + "\n***********\n ");
                        logger.error(e);
                        Response.ResponseBuilder res = Response.status(409);
                        return res.build();
                    }
                } else {
                    session.getTransaction().rollback();
                    logger.error("COULD NOT CREATE NEW COBJECT from mods: " + "\n***********\n " + nytFraMods.getMods() + "\n***********\n ");
                    return Response.status(400).build();
                }
            }
        } catch (Throwable e) {
            logger.error("Unable to create object "+editionId+id,e);
            Response.ResponseBuilder res = Response.status(500);
            return res.build();
        } finally {
            if (session != null && session.isConnected()){
                session.cancelQuery();
                session.close();
            }
        }
    }

    /**
     * This methos is related to the navigation service.
     *
     * @param medium
     * @param collection
     * @param year
     * @param month
     * @param edition
     * @param opml
     * @return
     *
     * Example:
     * PUT http://localhost:8080/cop/create/letters/judsam/2011/mar/dsa
     * Content-Type: application/xml
     *
     * <?xml version="1.0" encoding="UTF-8"?>
     * <opml version="2.0">
     * ...
     * </opml>
     *
     */

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

    private void sendToSolr(String id) {
        //TODO
        // call solrizerService
    }
}

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
     * our first Jersey webservice which support GET and PUT
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

        logger.debug("CREATE SERVICE");

        String uri = "/" + medium + "/" + collection + "/" + year + "/" + month + "/" + edition + "/" + id;
        // This is what the edition looks like
        String editionId = "/" + medium + "/" + collection + "/" + year + "/" + month + "/" + edition;
        String cacheKey = "syndication:edition:" + editionId + ";" +
                "id:" + id + ";";
        // logger.debug("cachekey: " + cacheKey);
        logger.debug("*****       uri " + uri);
        logger.debug("*****       mods: " + doc);
        logger.debug("*****       Content type: " + httpServletRequest.getContentType());
        logger.debug("*****       Char Encoding: " + httpServletRequest.getCharacterEncoding());


        Object cobject =
                new Object();

        ObjectFromModsExtractor bu = ObjectFromModsExtractor.getInstance();
        SessionFactory fact = HibernateUtil.getSessionFactory();
        Session ses = fact.getCurrentSession();
        ses.beginTransaction();

        try {
            MetadataWriter mdw = new HibernateMetadataWriter(ses);

            if (lastModified != null) {
                logger.debug("Trying to UPDATE object " + uri + ", at time: " + lastModified);

                Object nytCobjectFraMods =
                        bu.extractFromMods(cobject, doc, ses);

                ses.evict(cobject);
                cobject = null;
                String newLastModified = "";

                newLastModified = mdw.updateCobject(nytCobjectFraMods, lastModified);
                logger.debug("newLastModified: " + newLastModified);

                if (newLastModified != null && !newLastModified.equals("")) {
                    ses.getTransaction().commit();
                    logger.debug("Object created ID: " + nytCobjectFraMods.getId());
                    this.sendToSolrizr(uri);
                    return Response.ok("Updated").build();

                } else {
                    ses.getTransaction().rollback();
                    return Response.notModified("wrong lastModifiedDate provided").build();
                }

            } else {
                logger.debug("Trying to CREATE new object " + uri);

                Object nytFraMods = bu.extractFromMods(cobject, doc, ses);


                String newLastModified = "";
                newLastModified = mdw.create(nytFraMods);

                if (newLastModified != null || !newLastModified.equals("")) {
                    try {
                        ses.getTransaction().commit();
                        logger.debug("Object created ID: " + nytFraMods.getId());
                        sendToSolrizr(uri);
                        return Response.created(URI.create(nytFraMods.getId())).build();
                    } catch (Exception e) {
                        e.printStackTrace();
                        ses.getTransaction().rollback();
                        logger.error("COULD NOT CREATE NEW COBJECT from mods: " +
                                "\n***********\n "
                                + nytFraMods.getMods() +
                                "\n***********\n ");
                        logger.error(e);
                        Response.ResponseBuilder res = Response.status(409);
                        return res.build();
                    }
                } else {
                    ses.getTransaction().rollback();
                    logger.error("COULD NOT CREATE NEW COBJECT from mods: " +
                            "\n***********\n "
                            + nytFraMods.getMods() +
                            "\n***********\n ");
                    return Response.status(400).build();
                }
            }
        } catch (Throwable e) {
            logger.error("Unable to create object "+editionId+id,e);
            Response.ResponseBuilder res = Response.status(500);
            return res.build();
        } finally {
            logger.debug("In finally block, attempting to close Hibernate resources...");
            logger.debug("session != null: " + ses != null);
            logger.debug("session.isConnected(): " + ses.isConnected());
            if (ses != null && ses.isConnected()){
                logger.debug("Closing Hibernate session as we're still connected");
                ses.cancelQuery();
                ses.close();
            }
            logger.debug("Exiting finally block...");
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

    private void sendToSolrizr(String id) {
        try {
            JMSProducer producer = new JMSProducer(
                    this.consts.getConstants().getProperty("cop2.solrizr.queue.host"),
                    this.consts.getConstants().getProperty("cop2.solrizr.queue.update"));
            producer.sendMessage(id);
            producer.shutDownPRoducer();
            if ("true".equals(this.consts.getConstants().getProperty("cop2.solrizr.queue.copy_messages"))) {
                producer = new JMSProducer(
                        this.consts.getConstants().getProperty("cop2.solrizr.queue.host"),
                        this.consts.getConstants().getProperty("cop2.solrizr.queue.update")+".copy");
                producer.sendMessage(id);
                producer.shutDownPRoducer();
            }
        } catch (JMSException ex) {
            logger.error("Unable to connect to solrizr queue "+ex.getMessage());
        }
    }
}

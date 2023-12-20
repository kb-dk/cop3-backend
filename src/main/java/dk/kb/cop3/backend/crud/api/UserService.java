package dk.kb.cop3.backend.crud.api;


import dk.kb.cop3.backend.constants.Areas;
import dk.kb.cop3.backend.constants.DSFLAreas;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.services.GeoProvisioningService;
import dk.kb.cop3.backend.crud.services.UserProvisioningService;
import org.apache.commons.httpclient.HttpStatus;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * kb.dk
 *
 * @author jatr
 *         Date: 24/11/11
 *         Time: 13:45
 */
@Path("/user-services")
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * Get a user from the COPII back-end
     *
     * @param pid - library user id
     * @return User domain object for the given user
     */
    @POST
    @Path("/get-user")
    @Produces("application/json")
    public String post(@FormParam("pid") String pid,
                       @FormParam("id") String id,
                       @FormParam("givenName") String givenName,
                       @FormParam("surName") String surName,
                       @FormParam("commonName") String commonName) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        UserProvisioningService userService = new UserProvisioningService(session);
        Transaction transaction = session.beginTransaction();
        try {
            return userService.fetchOrCreateUserReturnUserJson(pid, id, givenName, surName, commonName);
        } catch (HibernateException e) {
            logger.error("error getting user", e);
            return e.getMessage();
        } finally {
            transaction.commit();
            session.close();
        }
    }

    /**
     * Get a user name from the COPII backend
     *
     * @param pid - library user id
     * @return String representing the users name
     */
    @POST
    @Path("/get-username")
    @Produces("text/plain")
    public String post(@FormParam("pid") String pid) {

        logger.debug("Got a request to find a user with pid: " + pid);
        Session session = HibernateUtil.getSessionFactory().openSession();
        UserProvisioningService userService = new UserProvisioningService(session);
        Transaction transaction = session.beginTransaction();
        try {
            return userService.getUserNameByPid(pid);
        } catch (HibernateException e) {
            logger.error("error getting username from pid", e);
            return e.getMessage();
        } finally {
            transaction.commit();
            session.close();
        }
    }


    /**
     * New give point to user webservice  with respect to the area
     *
     * @param pid    the usersPID
     * @param points the number of points the user deserves
     * @param lat  latitude
     * @param lng  longitude
     * @return
     */
    @POST
    @Path("/add-to-score-in-area")
    @Produces("text/plain")
    public Response postWithGeoCoords(@FormParam("pid") String pid, @FormParam("points") String points,  @FormParam("lat") double lat, @FormParam("lng") double lng) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        UserProvisioningService userProvisioningService = new UserProvisioningService(session);
        GeoProvisioningService geoProvisioningService = new GeoProvisioningService(session);
        Transaction transaction = session.beginTransaction();
        try {
            DSFLAreas area = geoProvisioningService.getAreaNotDanmark(lat,lng);
            if (area == null) {
                return Response.status(HttpStatus.SC_BAD_REQUEST).entity("point not in any area").build();
            }
            userProvisioningService.updateScoreInArea(pid, points, area);
            return  Response.ok().entity("score updated").build();
        } catch (HibernateException e) {
            logger.error("error getting username from pid", e);
            return Response.serverError().build();
        } finally {
            transaction.commit();
            session.close();
        }
    }


    /**
     * this service returns a number of users ordered by their points/user score.
     * http://localhost:8080/cop/user-services/get-users/?numberOfUsers=18
     *
     * @param numberOfUsers
     * @return a List og users in JSON with no sensitive information like e-mail or CPR number.
     */

    @GET
    @Path("/get-users")
    @Produces("application/json")
    public Response getTopUsers(@DefaultValue("10") @QueryParam("numberOfUsers") int numberOfUsers) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        UserProvisioningService userProvisioningService = new UserProvisioningService(session);
        Transaction transaction = session.beginTransaction();
        try {
            String topUsersJson = userProvisioningService.getUsersTopListForAreaAsJson(numberOfUsers,DSFLAreas.Danmark);
            final Response myResponse = Response.status(Response.Status.OK)
                    .entity(topUsersJson)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8") //"; charset=ISO-8859-1"
                    .build();
            return myResponse;
        } catch (Exception ex) {
            logger.error("error getting topuser",ex);
            return Response.serverError().entity("error getting topusers").build();
        } finally {
            transaction.commit();
            session.close();
        }

    }

    /**
     * this service returns a number of users ordered by their points/user score for a given area.
     * http://localhost:8080/cop/user-services/get-users/Hovedstaden/?numberOfUsers=18
     *
     * @param numberOfUsers
     * @return a List og users in JSON with no sensitive information like e-mail or CPR number.
     */
    @GET
    @Path("/get-users/{nameOfArea}")
    @Produces("application/json")
    public Response getTopUsers(@DefaultValue("10") @QueryParam("numberOfUsers") int numberOfUsers, @PathParam("nameOfArea") String nameOfArea) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        UserProvisioningService userService = new UserProvisioningService(session);
        Transaction transaction = session.beginTransaction();
        try {
            DSFLAreas areaToPullTopUsersFrom = Areas.getAreaEnumByName(nameOfArea);
            String usersInAreaJson = userService.getUsersTopListForAreaAsJson(numberOfUsers, areaToPullTopUsersFrom);
            final Response myResponse = Response.status(Response.Status.OK)
                    .entity(usersInAreaJson)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8")
                    .build();
            return myResponse;
        }catch (Exception ex) {
            logger.error("error getting topuser",ex);
            return Response.serverError().entity("error getting topusers").build();
        } finally {
            transaction.commit();
            session.close();
        }
    }
}

package dk.kb.cop3.backend.crud.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.kb.cop3.backend.constants.Areas;
import dk.kb.cop3.backend.constants.DSFLAreas;
import dk.kb.cop3.backend.crud.database.hibernate.User;
import dk.kb.cop3.backend.crud.database.hibernate.UserRole;
import dk.kb.cop3.backend.crud.exception.AreaNotFoundException;
import dk.kb.cop3.backend.crud.exception.UserProvisioningServiceException;
import dk.kb.cop3.backend.crud.model.UserForThePublic;
import dk.kb.cop3.backend.crud.services.GeoProvisioningService;
import dk.kb.cop3.backend.crud.services.UserProvisioningService;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * kb.dk
 *
 * @author jatr
 *         Date: 24/11/11
 *         Time: 13:45
 */
@Path("/user-services")
public class UserService {

    static final Logger LOGGER = Logger.getLogger(UserService.class);
    static final int KB_USER_ROLE_ID = 1;

    /**
     * @param pid        - library user id
     * @param id         - CPR nummer
     * @param givenName
     * @param surName
     * @param commonName
     * @param role
     * @param email
     * @return
     */
    @POST
    @Path("/add-user")
    public Response post(@FormParam("pid") String pid,
                         @FormParam("id") String id,
                         @FormParam("givenName") String givenName,
                         @FormParam("surName") String surName,
                         @FormParam("commonName") String commonName,
                         @FormParam("role") String role,
                         @FormParam("email") String email) {


        User user = new User();
        user.setPid(pid);
        user.setId(id);
        user.setGivenName(givenName);
        user.setSurName(surName);
        user.setCommonName(commonName);

        UserRole guestUserRole = new UserRole();
        guestUserRole.setRoleId(3);
        guestUserRole.setRoleName("GUEST_USER");

        user.setRole(guestUserRole);
        user.setEmail(email);

        user.setUserScore(new BigInteger("0"));
        user.setUserScore1(new BigInteger("0"));
        user.setUserScore2(new BigInteger("0"));
        user.setUserScore3(new BigInteger("0"));
        user.setUserScore4(new BigInteger("0"));
        user.setUserScore5(new BigInteger("0"));
        user.setUserScore6(new BigInteger("0"));
        user.setUserScore7(new BigInteger("0"));
        user.setUserScore8(new BigInteger("0"));
        user.setUserScore9(new BigInteger("0"));

        UserProvisioningService userProvisioningService = new UserProvisioningService();
        try {
            userProvisioningService.createUser(user);
            return Response.ok().build();
        } catch (UserProvisioningServiceException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

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

        LOGGER.debug("Got a request to find a user with pid: " + pid);
        UserProvisioningService userProvisioningService = new UserProvisioningService();
        try {
            User user = userProvisioningService.getUserByPid(pid);
            if (user == null) {//user doesn't exist in the DB so create a new one using the details from the Brugerbase DB
                LOGGER.debug("Creating new user...");
                User newUser = new User();
                newUser.setPid(pid);
                newUser.setId(id);
                newUser.setGivenName(givenName);
                newUser.setCommonName(commonName);
                newUser.setSurName(surName);

                UserRole userRole = userProvisioningService.getUserRole(KB_USER_ROLE_ID);

                newUser.setRole(userRole);
                newUser.setRoleId(KB_USER_ROLE_ID);
                newUser.setLastActive(new Timestamp(System.currentTimeMillis()));
                newUser.setUserScore(new BigInteger("0"));
                newUser.setUserScore1(new BigInteger("0"));
                newUser.setUserScore2(new BigInteger("0"));
                newUser.setUserScore3(new BigInteger("0"));
                newUser.setUserScore4(new BigInteger("0"));
                newUser.setUserScore5(new BigInteger("0"));
                newUser.setUserScore6(new BigInteger("0"));
                newUser.setUserScore7(new BigInteger("0"));
                newUser.setUserScore8(new BigInteger("0"));
                newUser.setUserScore9(new BigInteger("0"));
                userProvisioningService.createUser(newUser);
                user = newUser;
            }
            //Encode the response as JSON
            Gson gson = new GsonBuilder().create();
            String userJson = gson.toJson(user);
            LOGGER.debug("Returning JSON: " + userJson);

            return userJson;
        } catch (UserProvisioningServiceException e) {
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
            return e.getMessage();
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

        LOGGER.debug("Got a request to find a user with pid: " + pid);
        UserProvisioningService userProvisioningService = new UserProvisioningService();
        try {
            String userName = userProvisioningService.getUserNameByPid(pid);
            //Encode the response as JSON
            LOGGER.debug("Returning username: "+ userName);
            return userName;
        } catch (UserProvisioningServiceException e) {
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @Deprecated
    @POST
    @Path("/add-to-score")
    @Produces("text/plain")
    public String post(@FormParam("pid") String pid, @FormParam("points") String points) {
        UserProvisioningService userProvisioningService = new UserProvisioningService();
        userProvisioningService.updateScore(pid, points);
        return "ok";
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
    public String postWithGeoCoords(@FormParam("pid") String pid, @FormParam("points") String points,  @FormParam("lat") double lat, @FormParam("lng") double lng) {
        LOGGER.debug("Got a request to add userscores related to a area for pid: " + pid + " points: " + points + " lat: " +lat + " lng: " + lng);
        UserProvisioningService userProvisioningService = new UserProvisioningService();
        DSFLAreas danmark = DSFLAreas.Danmark;
        // Figure out what area the geoCoords are in and update the userScore for the user for that specific area.
        GeoProvisioningService geoProvisioningService = new GeoProvisioningService();
        DSFLAreas relevantArea;
        try {
            relevantArea = geoProvisioningService.getArea(lat, lng);
        } catch (AreaNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            relevantArea = danmark; // fall back to denmark!
        }
        userProvisioningService.updateScore(pid, points, relevantArea);

        return "added points for area:" +relevantArea +" pid: " + pid + " points: " + points + " lat: " +lat + " lng: " + lng;
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

        LOGGER.info("get users: " + numberOfUsers);

        UserProvisioningService userProvisioningService = new UserProvisioningService();
        List<User> allUsersInternal = userProvisioningService.getUsersForTopList(numberOfUsers);
        List<UserForThePublic> allUsersPublic = new ArrayList<UserForThePublic>();
        for (int i = 0; i < allUsersInternal.size(); i++) {
            UserForThePublic user = new UserForThePublic(allUsersInternal.get(i).getCommonName(), allUsersInternal.get(i).getPid(), allUsersInternal.get(i).getEmail(), allUsersInternal.get(i).getUserScore());
            allUsersPublic.add(user);
            //LOGGER.debug("user name: " + user.getCommonName() + " score:" + user.getUserScore().toPlainString());
        }

        //Encode the response as JSON
        Gson gson = new GsonBuilder().create();
        //Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
        String userJson = gson.toJson(allUsersPublic);
        final Response myResponse = Response.status(Response.Status.OK)
                .entity(userJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8") //"; charset=ISO-8859-1"
                .build();
        return myResponse;

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

        LOGGER.info("get users: " + numberOfUsers + " "+nameOfArea);

        UserProvisioningService userProvisioningService = new UserProvisioningService();
        DSFLAreas areaToPullTopUsersFrom = Areas.getAreaEnumByName(nameOfArea);

        List<User> allUsersInternal = userProvisioningService.getUsersFromAreaForTopList(numberOfUsers, areaToPullTopUsersFrom);// getUsersForTopList(numberOfUsers);
        List<UserForThePublic> allUsersPublic = new ArrayList<UserForThePublic>();
        for (int i = 0; i < allUsersInternal.size(); i++) {
            //UserForThePublic user = new UserForThePublic(allUsersInternal.get(i).getCommonName(), allUsersInternal.get(i).getPid(), allUsersInternal.get(i).getEmail(), allUsersInternal.get(i).getUserScore());
            UserForThePublic user = new UserForThePublic(allUsersInternal.get(i).getCommonName(), allUsersInternal.get(i).getPid(), allUsersInternal.get(i).getEmail(),
                    allUsersInternal.get(i).getUserScore(),
                    allUsersInternal.get(i).getUserScore1(),
                    allUsersInternal.get(i).getUserScore2(),
                    allUsersInternal.get(i).getUserScore3(),
                    allUsersInternal.get(i).getUserScore4(),
                    allUsersInternal.get(i).getUserScore5(),
                    allUsersInternal.get(i).getUserScore6(),
                    allUsersInternal.get(i).getUserScore7(),
                    allUsersInternal.get(i).getUserScore8(),
                    allUsersInternal.get(i).getUserScore9());
            allUsersPublic.add(user);
            //LOGGER.debug("user name: " + user.getCommonName() + " score:" + user.getUserScore().toPlainString());
        }

        //Encode the response as JSON
        Gson gson = new GsonBuilder().create();
        //Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
        String userJson = gson.toJson(allUsersPublic);
        final Response myResponse = Response.status(Response.Status.OK)
                .entity(userJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8") //"; charset=ISO-8859-1"
                .build();
        return myResponse;
    }


}

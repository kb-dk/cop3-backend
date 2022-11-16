package dk.kb.cop3.backend.crud.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.kb.cop3.backend.constants.DSFLAreas;
import dk.kb.cop3.backend.crud.database.UserDao;
import dk.kb.cop3.backend.crud.database.UserDaoImpl;
import dk.kb.cop3.backend.crud.database.UserRoleDao;
import dk.kb.cop3.backend.crud.database.UserRoleDaoImpl;
import dk.kb.cop3.backend.crud.database.hibernate.User;
import dk.kb.cop3.backend.crud.database.hibernate.UserRole;
import dk.kb.cop3.backend.crud.exception.AreaNotFoundException;
import dk.kb.cop3.backend.crud.exception.UserProvisioningServiceException;
import dk.kb.cop3.backend.crud.model.UserForThePublic;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Service facade class for User related business methods
 * <p/>
 * kb.dk
 *
 * @author jatr
 *         Date: 24/11/11
 *         Time: 14:13
 */
@Transactional
public class UserProvisioningService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProvisioningService.class);
    private static final int KB_USER_ROLE_ID = 1;

    private Session session;

    public UserProvisioningService(Session session) {
        this.session = session;
    }

    public String fetchOrCreateUserReturnUserJson(String pid, String id, String givenName, String surName, String commonName, Session session) {
        User user = session.get(User.class, pid);
        if (user == null) {//user doesn't exist in the DB so create a new one using the details from the Brugerbase DB
            User newUser = new User();
            newUser.setPid(pid);
            newUser.setId(id);
            newUser.setGivenName(givenName);
            newUser.setCommonName(commonName);
            newUser.setSurName(surName);
            UserRole userRole = session.get(UserRole.class,KB_USER_ROLE_ID);
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
            session.save(newUser);
            user = newUser;
        }
        //Encode the response as JSON
        Gson gson = new GsonBuilder().create();
        String userJson = gson.toJson(user);
        return userJson;
    }

    /**
     * Get the user name for the given pid
     *
     * @param pid
     * @return
     * @throws UserProvisioningServiceException
     *
     */
    public String getUserNameByPid(String pid)  {

        String userName = "unknown user";
        User user = session.get(User.class,pid);
        if (user != null) {
            userName = user.getCommonName();
        }
        return userName;
    }

    /**
     * get a given number of users ordered by the user score.
     *
     * @param numberOfUsers number Of Users.
     * @return the list of Hibernate Users.
     */
    public String getUsersForTopList(int numberOfUsers) {
        LOGGER.info("Getting users from the DB ordered by score. Number of Users to retrieve " + numberOfUsers);
        Query queryResult = session.createQuery("from User user order by user.userScore desc ");
        queryResult.setMaxResults(numberOfUsers);
        List<User> allUsersInternal  = queryResult.list();

        List<UserForThePublic> allUsersPublic = new ArrayList<UserForThePublic>();
        for (int i = 0; i < allUsersInternal.size(); i++) {
            UserForThePublic user = new UserForThePublic(allUsersInternal.get(i).getCommonName(), allUsersInternal.get(i).getPid(), allUsersInternal.get(i).getEmail(), allUsersInternal.get(i).getUserScore());
            allUsersPublic.add(user);
        }
        Gson gson = new GsonBuilder().create();
        String userJson = gson.toJson(allUsersPublic);
        return userJson;
    }


    /**
     * get a given number of users ordered by the user score.
     *
     * @param numberOfUsers number Of Users.
     * @return the list of Hibernate Users.
     */
    public String getUsersFromAreaForTopList(int numberOfUsers, DSFLAreas aSpecificArea) {
        UserDao userDao = new UserDaoImpl();
        List<User> allUsersInternal = userDao.getUsers(numberOfUsers, aSpecificArea);
        List<UserForThePublic> allUsersPublic = new ArrayList<UserForThePublic>();
        for (int i = 0; i < allUsersInternal.size(); i++) {
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
        }
        Gson gson = new GsonBuilder().create();
        String userJson = gson.toJson(allUsersPublic);
        return userJson;
    }

    /**
     * Get a user role object from the DAO layer
     *
     * @param userRoleId
     * @return UserRole the user role from the DAO layer or null if no role matching the id could be found
     * @throws UserProvisioningServiceException
     *
     */
    public UserRole getUserRole(int userRoleId) throws UserProvisioningServiceException {

        UserRoleDao userRoleDao = new UserRoleDaoImpl();
        UserRole userRole;
        try {
            LOGGER.debug("Calling DAO layer to get user role {}", userRoleId);
            userRole = userRoleDao.findUserRoleById(userRoleId);
            LOGGER.debug("Retrieved user role: {}", userRole.toString());
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UserProvisioningServiceException("Det var ikke muligt at tilføje en ny bruger på grund af en serverfejl");
        }
        return userRole;
    }


    /**
     * Updating the national score and adding to a specific area userscore
     *
     * @param pid
     * @param points
     * @param lat lattitude of point
     * @param lng longitue of point
     */
    public void updateScoreInArea(String pid, String points, double lat, double lng) throws AreaNotFoundException {
        GeoProvisioningService geoProvisioningService = new GeoProvisioningService();
        DSFLAreas aSpecificArea = geoProvisioningService.getArea(lat, lng);
        UserDao userDao = new UserDaoImpl();
        User user = userDao.getUser(pid);
        if (aSpecificArea == DSFLAreas.Danmark) {
            user.setUserScore(user.getUserScore().add(new BigInteger(points)));
        } else if (aSpecificArea == DSFLAreas.Fyn) {
            user.setUserScore(user.getUserScore().add(new BigInteger(points)));  // Fyn
            user.setUserScore1(user.getUserScore1().add(new BigInteger(points)));
        } else if (aSpecificArea == DSFLAreas.Bornholm) {
            user.setUserScore(user.getUserScore().add(new BigInteger(points)));  // Bornholm
            user.setUserScore2(user.getUserScore2().add(new BigInteger(points)));
        } else if (aSpecificArea == DSFLAreas.Hovedstaden) {
            user.setUserScore(user.getUserScore().add(new BigInteger(points)));  // Hovedstaden
            user.setUserScore3(user.getUserScore3().add(new BigInteger(points)));
        } else if (aSpecificArea == DSFLAreas.Kattegat) {
            user.setUserScore(user.getUserScore().add(new BigInteger(points)));  // Kattegat
            user.setUserScore4(user.getUserScore4().add(new BigInteger(points)));
        } else if (aSpecificArea == DSFLAreas.LollandFalster) {
            user.setUserScore(user.getUserScore().add(new BigInteger(points)));  // LollandFalster
            user.setUserScore5(user.getUserScore5().add(new BigInteger(points)));
        } else if (aSpecificArea == DSFLAreas.Midtjylland) {
            user.setUserScore(user.getUserScore().add(new BigInteger(points)));  // Midtjylland
            user.setUserScore6(user.getUserScore6().add(new BigInteger(points)));
        } else if (aSpecificArea == DSFLAreas.Nordjylland) {
            user.setUserScore(user.getUserScore().add(new BigInteger(points)));  // Nordjylland
            user.setUserScore7(user.getUserScore7().add(new BigInteger(points)));
        } else if (aSpecificArea == DSFLAreas.Sjælland) {
            user.setUserScore(user.getUserScore().add(new BigInteger(points)));  // Sjælland
            user.setUserScore8(user.getUserScore8().add(new BigInteger(points)));
        } else if (aSpecificArea == DSFLAreas.Sydjylland) {
            user.setUserScore(user.getUserScore().add(new BigInteger(points)));  // Sønderjylland
            user.setUserScore9(user.getUserScore9().add(new BigInteger(points)));
        } else {
            LOGGER.error("No area to distribute points!!!");
        }
        session.update(user);
    }
}

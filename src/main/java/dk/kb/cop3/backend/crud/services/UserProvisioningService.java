package dk.kb.cop3.backend.crud.services;

import dk.kb.cop3.backend.constants.DSFLAreas;
import dk.kb.cop3.backend.crud.database.UserDao;
import dk.kb.cop3.backend.crud.database.UserRoleDao;
import dk.kb.cop3.backend.crud.database.hibernate.User;
import dk.kb.cop3.backend.crud.database.hibernate.UserRole;
import dk.kb.cop3.backend.crud.exception.UserProvisioningServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
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

    Logger LOGGER = LoggerFactory.getLogger(UserProvisioningService.class);

    private static final ApplicationContext COPIII_CONTEXT = new ClassPathXmlApplicationContext("copIII-dao-context.xml");

    public UserProvisioningService() {}

    /**
     * Calls the DAO layer to create a user for the given domain object
     *
     * @param user User domain object
     * @throws UserProvisioningServiceException
     *
     */
    public void createUser(User user) throws UserProvisioningServiceException {

        UserDao userDao = (UserDao) COPIII_CONTEXT.getBean("userDao");
        LOGGER.trace("createUser successfully retrieved UserDao from Spring application context");

        try {
            LOGGER.debug("Calling DAO layer to create new user...");
            userDao.addUser(user);
            LOGGER.debug("DAO layer created user successfully");
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UserProvisioningServiceException("Det var ikke muligt at tilføje en ny bruger på grund af en serverfejl");
        }
    }

    /**
     * Gets a User from the DAO layer
     *
     * @param pid - pid of the user to be retrieved
     * @return User domain object from the DAO layer
     * @throws UserProvisioningServiceException
     *
     */
    public User getUserByPid(String pid) throws UserProvisioningServiceException {

        UserDao userDao = (UserDao) COPIII_CONTEXT.getBean("userDao");
        LOGGER.trace("getUserByPid successfully retrieved UserDao from Spring application context");
        User user;
        try {
            LOGGER.debug("Calling DAO layer to get user {}", pid);
            user = userDao.getUser(pid);
            LOGGER.debug("DAO layer retrieved user successfully");
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UserProvisioningServiceException("Det var ikke muligt at tilføje en ny bruger på grund af en serverfejl");
        }

        return user;
    }

    /**
     * Get the user name for the given pid
     *
     * @param pid
     * @return
     * @throws UserProvisioningServiceException
     *
     */
    public String getUserNameByPid(String pid) throws UserProvisioningServiceException {

        UserDao userDao = (UserDao) COPIII_CONTEXT.getBean("userDao");
        LOGGER.trace("getUserByPid successfully retrieved UserDao from Spring application context");
        String userName = null;
        try {
            LOGGER.debug("Calling DAO layer to get user name {}", pid);
            User user = userDao.getUser(pid);
            if (user != null) {
                userName = user.getCommonName();
            }
            LOGGER.debug("Retrieved user name: {}", userName);

        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UserProvisioningServiceException("Det var ikke muligt at tilføje en ny bruger på grund af en serverfejl");
        }

        return userName;
    }

    /**
     * get a given number of users ordered by the user score.
     *
     * @param numberOfUsers number Of Users.
     * @return the list of Hibernate Users.
     */
    public List<User> getUsersForTopList(int numberOfUsers) {
        UserDao userDao = (UserDao) COPIII_CONTEXT.getBean("userDao");
        List<User> x = userDao.getUsers(numberOfUsers);

        return x;
    }


    /**
     * get a given number of users ordered by the user score.
     *
     * @param numberOfUsers number Of Users.
     * @return the list of Hibernate Users.
     */
    public List<User> getUsersFromAreaForTopList(int numberOfUsers, DSFLAreas aSpecificArea) {
        UserDao userDao = (UserDao) COPIII_CONTEXT.getBean("userDao");
        List<User> x = userDao.getUsers(numberOfUsers, aSpecificArea);

        return x;
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

        UserRoleDao userRoleDao = (UserRoleDao) COPIII_CONTEXT.getBean("userRoleDao");
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

    @Deprecated
    public void updateScore(String pid, String points) {
        LOGGER.debug("adding " + points + " points for user " + pid);
        UserDao userDao = (UserDao) COPIII_CONTEXT.getBean("userDao");

        User user = userDao.getUser(pid);
        LOGGER.debug("user: " + user);
        user.setUserScore(user.getUserScore().add(new BigInteger(points)));
        userDao.modifyUser(user);
    }

    /**
     * Updating the national score and adding to a specific area userscore
     *
     * @param pid
     * @param points
     * @param aSpecificArea
     */
    public void updateScore(String pid, String points, DSFLAreas aSpecificArea) {
        LOGGER.debug("adding " + points + " points for user " + pid);
        UserDao userDao = (UserDao) COPIII_CONTEXT.getBean("userDao");
        LOGGER.debug("aSpecificArea = {}", aSpecificArea.toString());

        User user = userDao.getUser(pid);
        LOGGER.debug("user: " + user);

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

        userDao.modifyUser(user);
    }
}

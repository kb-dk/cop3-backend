package dk.kb.cop3.backend.crud.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.kb.cop3.backend.constants.Areas;
import dk.kb.cop3.backend.constants.DSFLAreas;
import dk.kb.cop3.backend.crud.database.hibernate.User;
import dk.kb.cop3.backend.crud.database.hibernate.UserRole;
import dk.kb.cop3.backend.crud.exception.AreaNotFoundException;
import dk.kb.cop3.backend.crud.exception.UserProvisioningServiceException;
import dk.kb.cop3.backend.crud.model.UserForThePublic;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

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

    private final Session session;

    public UserProvisioningService(Session session) {
        this.session = session;
    }

    public String fetchOrCreateUserReturnUserJson(String pid, String id, String givenName, String surName, String commonName, Session session) {
        ensureSessionHasOpenTransaction();
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
        Gson gson = new GsonBuilder().create();
        return gson.toJson(user);
    }

    /**
     * Get the username for the given pid
     *
     * @param pid
     * @return
     *
     */
    public String getUserNameByPid(String pid)  {
        ensureSessionHasOpenTransaction();
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
     * @param area the area to get topusers from
     * @return the list of Hibernate Users.
     */
    public String getUsersTopListForAreaAsJson(int numberOfUsers, DSFLAreas area) {
        ensureSessionHasOpenTransaction();
        String sortField = Areas.fromEnumToRelevantDBUserFieldName(area);
        List<User> allUsersInternal = session.createQuery("from User user order by user."+sortField+" desc").
                setMaxResults(numberOfUsers).list();
        List<UserForThePublic> allUsersPublic = convertToPublicUserList(allUsersInternal);
        Gson gson = new GsonBuilder().create();
        return gson.toJson(allUsersPublic);
    }

    /**
     * Updating the national score and adding to a specific area userscore
     *
     * @param pid
     * @param points
     */
    public void updateScoreInArea(String pid, String points, DSFLAreas area) throws AreaNotFoundException {
        User user = session.get(User.class,pid);
        if (area == DSFLAreas.Danmark) {
            user.setUserScore(user.getUserScore().add(new BigInteger(points)));
        } else if (area == DSFLAreas.Fyn) {
            user.setUserScore(user.getUserScore().add(new BigInteger(points)));
            user.setUserScore1(user.getUserScore1().add(new BigInteger(points)));
        } else if (area == DSFLAreas.Bornholm) {
            user.setUserScore(user.getUserScore().add(new BigInteger(points)));
            user.setUserScore2(user.getUserScore2().add(new BigInteger(points)));
        } else if (area == DSFLAreas.Hovedstaden) {
            user.setUserScore(user.getUserScore().add(new BigInteger(points)));
            user.setUserScore3(user.getUserScore3().add(new BigInteger(points)));
        } else if (area == DSFLAreas.Kattegat) {
            user.setUserScore(user.getUserScore().add(new BigInteger(points)));
            user.setUserScore4(user.getUserScore4().add(new BigInteger(points)));
        } else if (area == DSFLAreas.LollandFalster) {
            user.setUserScore(user.getUserScore().add(new BigInteger(points)));
            user.setUserScore5(user.getUserScore5().add(new BigInteger(points)));
        } else if (area == DSFLAreas.Midtjylland) {
            user.setUserScore(user.getUserScore().add(new BigInteger(points)));
            user.setUserScore6(user.getUserScore6().add(new BigInteger(points)));
        } else if (area == DSFLAreas.Nordjylland) {
            user.setUserScore(user.getUserScore().add(new BigInteger(points)));
            user.setUserScore7(user.getUserScore7().add(new BigInteger(points)));
        } else if (area == DSFLAreas.Sjælland) {
            user.setUserScore(user.getUserScore().add(new BigInteger(points)));
            user.setUserScore8(user.getUserScore8().add(new BigInteger(points)));
        } else if (area == DSFLAreas.Sydjylland) {
            user.setUserScore(user.getUserScore().add(new BigInteger(points)));
            user.setUserScore9(user.getUserScore9().add(new BigInteger(points)));
        } else {
            LOGGER.error("No area to distribute points!!!");
        }
        session.update(user);
    }

    private static List<UserForThePublic> convertToPublicUserList(List<User> internalUserList) {
        return internalUserList.stream()
                .map(UserForThePublic::new)
                .collect(Collectors.toList());
    }

    private void ensureSessionHasOpenTransaction() {
        if (!session.getTransaction().isActive()) {
            throw new IllegalArgumentException("Userprovisioning service: session does not have open transaction");
        }
    }
}

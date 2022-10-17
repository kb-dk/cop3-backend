package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.constants.Areas;
import dk.kb.cop3.backend.constants.DSFLAreas;
import dk.kb.cop3.backend.crud.database.hibernate.User;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * kb.dk
 * @author jatr
 * Date: 23/11/11
 * Time: 15:38
 *
 * Hibernate UserDao implementation class for data manipulation of User domain object
 */
@Repository
public class UserDaoImpl implements UserDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);

    private SessionFactory sessionFactory;

    public UserDaoImpl(){
        this.sessionFactory = new Configuration().configure("hibernate.cfg.xml")
                .buildSessionFactory();

    }

    /**
     * Save a new user to the USERS table
     * @param user domain object
     * @return String representing the USER_PID
     * @throws DataAccessException
     */
    @Override
    public String addUser(User user) throws DataAccessException {

        Session session = null;
        Transaction tx  = null;
        try {
            LOGGER.debug("Saving new user to DB...");
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            String userPid = (String) session.save(user);
            LOGGER.debug("User saved successfully with pid: " + userPid);
            tx.commit();
            session.close();
            return userPid;
        }catch (Exception e) {
            LOGGER.error("Error while adding user  ", e);
            if(tx != null){
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return null;
    }

    /**
     * Retrieve User domain object from the database, including the full tree of related objects from all tables related
     * to the USERS table
     * @param userPid - pid of the User to be retrieved
     * @return
     * @throws DataAccessException
     */
    @Override
    public User getUser(String userPid) throws DataAccessException {

        Session session = null;
        Transaction transaction = null;
        try{
            LOGGER.debug("Getting user from the DB with Pid: " + userPid);
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Query query = session.createSQLQuery(" select user from users  where user_pid='"+userPid+"'");
//OLD:            Query query = session.createSQLQuery(" select user0_.USER_PID as USER1_8_0_, user0_.USER_ID as USER2_8_0_, user0_.USER_GIVEN_NAME as USER3_8_0_, user0_.USER_SURNAME as USER4_8_0_, user0_.USER_COMMON_NAME as USER5_8_0_, user0_.USER_ROLE_ID as USER6_8_0_, user0_.USER_EMAIL as USER7_8_0_, user0_.USER_SCORE as USER8_8_0_, user0_.USERSCORE1 as USERSCORE9_8_0_, user0_.LAST_ACTIVE_DATE as LAST10_8_0_  from COP2.USERS user0_  where user0_.USER_PID='"+userPid+"'");
            query.list();
            User user = (User) session.get(User.class, userPid);
            transaction.commit();
            LOGGER.debug("Successfully performed DB query");
            return user;
        } catch (ObjectNotFoundException e) {
            LOGGER.debug("No user found for userPid: " + userPid);
        } catch (Exception e) {
            LOGGER.error("Error while getting user for userPid: " + userPid, e);
            e.printStackTrace();
        }
        finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return null;
    }

    /**
     * Retrieve Users domain object from the database, including the full tree of related objects from all tables related
     * to the USERS table
     * @param numberOfUsersToRetrieve - numberOfUsersToRetrieve
     * @return
     * @throws DataAccessException
     */
    @Override
    public List<User> getUsers(int numberOfUsersToRetrieve) throws DataAccessException {

        Session session = null;
        Transaction transaction = null;
        try{
            LOGGER.info("Getting users from the DB ordered by score. Number of Users to retrieve " + numberOfUsersToRetrieve );
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Query queryResult = session.createQuery("from User user order by user.userScore desc ");
              queryResult.setMaxResults(numberOfUsersToRetrieve);
              java.util.List allUsers;
              allUsers = queryResult.list();
            LOGGER.info("Users found: " + allUsers.size());

            transaction.commit();
            LOGGER.debug("Successfully performed DB query");
            return allUsers;
        } catch (ObjectNotFoundException e) {
            LOGGER.debug("No users found");
        } catch (Exception e) {
            LOGGER.error("Error while getting users " , e);
            e.printStackTrace();
        }
        finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return null;
    }



    /**
      * Retrieve Users domain object from the database, including the full tree of related objects from all tables related
      * to the USERS table
      * @param numberOfUsersToRetrieve - numberOfUsersToRetrieve
      * @return a list of users
      * @throws DataAccessException
      */
     @Override
     public List<User> getUsers(int numberOfUsersToRetrieve, DSFLAreas anSpecificArea) throws DataAccessException {

         Session session = null;
         Transaction transaction = null;
         try{
             LOGGER.info("Getting users from the DB ordered by score. Number of Users to retrieve " + numberOfUsersToRetrieve );
             session = sessionFactory.openSession();
             transaction = session.beginTransaction();

             String specificUserScoreField = Areas.fromEnumToRelevantDBUserFieldName(anSpecificArea); //   userScore1-userScore9
             Query queryResult = session.createQuery("from User user order by user." + specificUserScoreField + " desc ");
               queryResult.setMaxResults(numberOfUsersToRetrieve);
               java.util.List allUsers;
               allUsers = queryResult.list();
             LOGGER.info("Users found: " + allUsers.size());

             transaction.commit();
             LOGGER.debug("Successfully performed DB query");
             return allUsers;
         } catch (ObjectNotFoundException e) {
             LOGGER.debug("No users found");
         } catch (Exception e) {
             LOGGER.error("Error while getting users " , e);
             e.printStackTrace();
         }
         finally {
             if (session != null && session.isOpen()) {
                 session.close();
             }
         }
         return null;
     }

    /**
     * Save updates to a User domain object
     * @param user the updated User domain object
     * @throws DataAccessException
     */
    @Override
    public void modifyUser(User user) throws DataAccessException {

        Session session = null;
        Transaction tx = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            session.saveOrUpdate(user);
            tx.commit();
        }catch (Exception e) {
            LOGGER.error("Error while editing user  ", e);
            if(tx != null){
                tx.rollback();
            }
            e.printStackTrace();
        }
        finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Delete a user from the DB
     * @param user the User domain object that will be deleted from the database
     * @throws DataAccessException
     */
    @Override
    public void deleteUser(User user) throws DataAccessException {

        Session session = null;
        Transaction tx = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            session.delete(user);
            tx.commit();
            session.close();
        } catch (Exception e) {
            LOGGER.error("Error while deleting user  ", e);
            if(tx != null){
                tx.rollback();
            }
            e.printStackTrace();
        }
        finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

}

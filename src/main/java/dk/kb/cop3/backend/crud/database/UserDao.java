package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.constants.DSFLAreas;
import dk.kb.cop3.backend.crud.database.hibernate.User;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * kb.dk
 *
 * Interface for all DAO operations relating to User domain objects
 * @author jatr
 * Date: 23/11/11
 * Time: 15:38
 */
public interface UserDao {

    /**
     * Save a new User domain object to the database
     * @param user User domain object to be saved
     * @return pid of the new User
     * @throws DataAccessException Spring DAO exception class
     */
    public String addUser(User user) throws DataAccessException;

    /**
     * Get a User domain object from the database
     *
     * @param userPid - the ID of the User being searched for
     * @return User domain object found in the DB or null if not found
     * @throws DataAccessException Spring DAO exception class
     */
    public User getUser(String userPid) throws DataAccessException;


    /**
     * Get the a list of the most active users
     * @param numberOfUsersToRetrieve amount of users to retrieve
     * @return list of user objects.
     * @throws DataAccessException
     */
    public List getUsers(int numberOfUsersToRetrieve) throws DataAccessException;


    /**
     * Get the a list of the most active users in an area. I.e. Bornholm
     * @param numberOfUsersToRetrieve amount of users to retrieve
     * @param aSpecificArea a specific Area.
     * @return list of user objects.
     * @throws DataAccessException
     */
    public List getUsers(int numberOfUsersToRetrieve, DSFLAreas aSpecificArea) throws DataAccessException;


    /**
     * Save changes for a given User domain object
     *
     * @param user the updated User domain object
     * @throws DataAccessException Spring DAO exception class
     */
    public void modifyUser(User user) throws DataAccessException;

    /**
     * Delete the data for a domain user from the database
     *
     * @param user the User domain object that will be deleted from the database
     * @throws DataAccessException Spring DAO exception class
     */
    public void deleteUser(User user) throws DataAccessException;
}

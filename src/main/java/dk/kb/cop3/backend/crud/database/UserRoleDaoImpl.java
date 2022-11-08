package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.database.hibernate.UserRole;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

/**
 * Implementation of UserRoleDao interface, uses Hibernate to query the database
 *
 * kb.dk
 *
 * @author jatr
 *         Date: 22/12/11
 *         Time: 15:01
 */
public class UserRoleDaoImpl implements UserRoleDao{

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleDaoImpl.class);

    /**
     * Implementation of the findUserRoleById method
     * @param roleId the role id of the role we want to find
     * @return UserRole domain object representing a user role or null if not found
     * @throws DataAccessException
     */
    @Override
    public UserRole findUserRoleById(int roleId) throws DataAccessException {
        Session session = null;
        Transaction tx = null;
        try{
            LOGGER.debug("Getting user from the DB with roleId: " + roleId);
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            UserRole userRole = (UserRole) session.get(UserRole.class, roleId);
            tx.commit();
            LOGGER.debug("Successfully performed DB query");
            return userRole;
        } catch (ObjectNotFoundException e) {
            LOGGER.debug("No user role found for role id: " + roleId);
        } catch (Exception e) {
            LOGGER.error("Error while getting user for role id: " + roleId, e);
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return null;
    }
}

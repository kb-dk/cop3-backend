package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.database.hibernate.UserRole;
import org.springframework.dao.DataAccessException;

/**
 * Interface for UserRoleDao
 *
 * kb.dk
 *
 * @author jatr
 *         Date: 22/12/11
 *         Time: 15:00
 */
public interface UserRoleDao {

    /**
     * find a UserRole object by the role id specified
     * @param roleId
     * @return UserRole domain object representing a user role
     * @throws DataAccessException
     */
    public UserRole findUserRoleById(int roleId) throws DataAccessException;
}

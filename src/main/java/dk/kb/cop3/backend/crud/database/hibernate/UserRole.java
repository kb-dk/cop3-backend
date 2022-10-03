package dk.kb.cop3.backend.crud.database.hibernate;

import java.io.Serializable;
import java.lang.*;
import java.util.Set;

/**
 * kb.dk
 *
 * @author jatr
 *         Date: 24/11/11
 *         Time: 17:58
 */
public class UserRole implements Serializable {

    private Integer roleId;
    private String roleName;
    private Set<UserPermissions> permissions;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<UserPermissions> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<UserPermissions> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserRole userRole = (UserRole) o;

        if (roleId != null ? !roleId.equals(userRole.roleId) : userRole.roleId != null) return false;
        if (roleName != null ? !roleName.equals(userRole.roleName) : userRole.roleName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = roleId != null ? roleId.hashCode() : 0;
        result = 31 * result + (roleName != null ? roleName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}

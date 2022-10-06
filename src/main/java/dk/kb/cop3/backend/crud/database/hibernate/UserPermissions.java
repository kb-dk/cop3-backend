package dk.kb.cop3.backend.crud.database.hibernate;

import java.lang.*;
import java.util.Set;

/**
 * kb.dk
 *
 * @author jatr
 *         Date: 24/11/11
 *         Time: 18:00
 */
public class UserPermissions {

    private Integer permissionId;
    private String permissionName;
    private Set<UserRole> roles;

    public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserPermissions that = (UserPermissions) o;

        if (permissionId != null ? !permissionId.equals(that.permissionId) : that.permissionId != null) return false;
        if (permissionName != null ? !permissionName.equals(that.permissionName) : that.permissionName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = permissionId != null ? permissionId.hashCode() : 0;
        result = 31 * result + (permissionName != null ? permissionName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserPermissions{" +
                "permissionId=" + permissionId +
                ", permissionName='" + permissionName + '\'' +
                '}';
    }
}

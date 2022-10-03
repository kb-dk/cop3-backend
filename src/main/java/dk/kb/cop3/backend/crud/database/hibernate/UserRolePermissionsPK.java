package dk.kb.cop3.backend.crud.database.hibernate;

import java.io.Serializable;

/**
 * kb.dk
 *
 * @author jatr
 *         Date: 29/11/11
 *         Time: 09:49
 */
public class UserRolePermissionsPK implements Serializable {
    private Integer permissionId;

    public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }

    private Integer roleId;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserRolePermissionsPK that = (UserRolePermissionsPK) o;

        if (permissionId != null ? !permissionId.equals(that.permissionId) : that.permissionId != null) return false;
        if (roleId != null ? !roleId.equals(that.roleId) : that.roleId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = permissionId != null ? permissionId.hashCode() : 0;
        result = 31 * result + (roleId != null ? roleId.hashCode() : 0);
        return result;
    }
}

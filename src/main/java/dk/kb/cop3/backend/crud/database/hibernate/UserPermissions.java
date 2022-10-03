package dk.kb.cop3.backend.crud.database.hibernate;

import java.lang.*;

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

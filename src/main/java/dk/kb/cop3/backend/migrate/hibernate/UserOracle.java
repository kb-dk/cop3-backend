package dk.kb.cop3.backend.migrate.hibernate;


import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * A domain object to represent a library user in the system
 *
 * @author jatr
 * @since 21/11/11
 */
public class UserOracle implements java.io.Serializable {

    private String pid;//library user id - corresponds to primary key in the USERS table, generated from an external source
    private String id;//CPR id used for logging into the system
    private String givenName;
    private String surName;
    private String commonName;
    private Integer roleId;
    private UserRoleOracle role;
    private String email;
    private BigInteger userScore;
    private BigInteger userScore1;
    private BigInteger userScore2;
    private BigInteger userScore3;
    private BigInteger userScore4;
    private BigInteger userScore5;
    private BigInteger userScore6;
    private BigInteger userScore7;
    private BigInteger userScore8;
    private BigInteger userScore9;
    private Timestamp lastActive;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public UserRoleOracle getRole() {
        return role;
    }

    public void setRole(UserRoleOracle role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getLastActive() {
        return lastActive;
    }

    public void setLastActive(Timestamp lastActive) {
        this.lastActive = lastActive;
    }

    public BigInteger getUserScore() {
        return userScore;
    }

    public void setUserScore(BigInteger userScore) {
        this.userScore = userScore;
    }


    public BigInteger getUserScore1() {
        return userScore1;
    }

    public void setUserScore1(BigInteger userScore1) {
        this.userScore1 = userScore1;
    }

    public BigInteger getUserScore2() {
        return userScore2;
    }

    public void setUserScore2(BigInteger userScore2) {
        this.userScore2 = userScore2;
    }

    public BigInteger getUserScore3() {
        return userScore3;
    }

    public void setUserScore3(BigInteger userScore3) {
        this.userScore3 = userScore3;
    }

    public BigInteger getUserScore4() {
        return userScore4;
    }

    public void setUserScore4(BigInteger userScore4) {
        this.userScore4 = userScore4;
    }

    public BigInteger getUserScore5() {
        return userScore5;
    }

    public void setUserScore5(BigInteger userScore5) {
        this.userScore5 = userScore5;
    }

    public BigInteger getUserScore6() {
        return userScore6;
    }

    public void setUserScore6(BigInteger userScore6) {
        this.userScore6 = userScore6;
    }

    public BigInteger getUserScore7() {
        return userScore7;
    }

    public void setUserScore7(BigInteger userScore7) {
        this.userScore7 = userScore7;
    }

    public BigInteger getUserScore8() {
        return userScore8;
    }

    public void setUserScore8(BigInteger userScore8) {
        this.userScore8 = userScore8;
    }


    public BigInteger getUserScore9() {
        return userScore9;
    }

    public void setUserScore9(BigInteger userScore9) {
        this.userScore9 = userScore9;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserOracle users = (UserOracle) o;

        if (lastActive != null ? !lastActive.equals(users.lastActive) : users.lastActive != null)
            return false;
        if (commonName != null ? !commonName.equals(users.commonName) : users.commonName != null)
            return false;
        if (email != null ? !email.equals(users.email) : users.email != null) return false;
        if (givenName != null ? !givenName.equals(users.givenName) : users.givenName != null)
            return false;
        if (id != null ? !id.equals(users.id) : users.id != null) return false;
        if (pid != null ? !pid.equals(users.pid) : users.pid != null) return false;
        if (surName != null ? !surName.equals(users.surName) : users.surName != null) return false;
        if (role != null ? !role.equals(users.role) : users.role != null) return  false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = pid != null ? pid.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (givenName != null ? givenName.hashCode() : 0);
        result = 31 * result + (surName != null ? surName.hashCode() : 0);
        result = 31 * result + (commonName != null ? commonName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (lastActive != null ? lastActive.hashCode() : 0);
        return result;
    }




    @Override
    public String toString() {
        return "User{" +
                "pid='" + pid + '\'' +
                ", id='" + id + '\'' +
                ", givenName='" + givenName + '\'' +
                ", surName='" + surName + '\'' +
                ", commonName='" + commonName + '\'' +
                ", roleId=" + roleId +
                ", role=" + role +
                ", email='" + email + '\'' +
                ", userScore=" + userScore +
                ", userScore1=" + userScore1 +
                ", userScore2=" + userScore2 +
                ", userScore3=" + userScore3 +
                ", userScore4=" + userScore4 +
                ", userScore5=" + userScore5 +
                ", userScore6=" + userScore6 +
                ", userScore7=" + userScore7 +
                ", userScore8=" + userScore8 +
                ", lastActive=" + lastActive +
                '}';
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}

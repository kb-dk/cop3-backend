package dk.kb.cop3.backend.crud.model;

import java.math.BigInteger;

/**
 * @author: Andreas B. Westh
 * Date: 8/23/12
 * Time: 15:02 PM
 */
public class UserForThePublic {

    /**
     * Construct a public object of a user without social security number and e-mail.
     * @param commonName
     * @param pid
     * @param email
     * @param userScore
     */
    @Deprecated
    public UserForThePublic(String commonName, String pid, String email, BigInteger userScore) {
        this.commonName = commonName;
        this.pid = pid;
        this.email = email;
        this.userScore = userScore;
    }

    /**
     * Construct a public object of a user without social security number, userscores and e-mail.
     * @param commonName
     * @param pid
     * @param email
     * @param userScore   Danmark
     * @param userScore1  Fyn
     * @param userScore2  Bornholm
     * @param userScore3  Hovedstaden
     * @param userScore4  Kattegat
     * @param userScore5  LollandFalster
     * @param userScore6  Midtjylland
     * @param userScore7  Nordjylland
     * @param userScore8  Sjælland
     * @param userScore9  Sønderjylland
     */
    public UserForThePublic(String commonName, String pid, String email, BigInteger userScore, BigInteger userScore1, BigInteger userScore2, BigInteger userScore3, BigInteger userScore4, BigInteger userScore5, BigInteger userScore6, BigInteger userScore7, BigInteger userScore8, BigInteger userScore9) {
        this.commonName = commonName;
        this.pid = pid;
        this.email = email;
        this.userScore = userScore;
        this.userScore1 = userScore1;
        this.userScore2 = userScore2;
        this.userScore3 = userScore3;
        this.userScore4 = userScore4;
        this.userScore5 = userScore5;
        this.userScore6 = userScore6;
        this.userScore7 = userScore7;
        this.userScore8 = userScore8;
        this.userScore9 = userScore9;
    }

    private String commonName;
    private String pid;//library user id - corresponds to primary key in the USERS table, generated from an external source
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


    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
    public String toString() {
        return "UserForThePublic{" +
                "commonName='" + commonName + '\'' +
                ", pid='" + pid + '\'' +
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
                ", userScore9=" + userScore9 +
                '}';
    }
}

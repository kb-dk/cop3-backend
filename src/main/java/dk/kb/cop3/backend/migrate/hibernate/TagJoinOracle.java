package dk.kb.cop3.backend.migrate.hibernate;

import dk.kb.cop3.backend.crud.database.hibernate.Object;

import java.io.Serializable;
import java.sql.Timestamp;

public class TagJoinOracle implements Serializable {

    private String tid;
    private String oid;
    private String creator;
    private java.sql.Timestamp timestamp;

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TagJoinOracle{" +
                "tid='" + tid + '\'' +
                ", oid='" + oid + '\'' +
                ", creator='" + creator + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

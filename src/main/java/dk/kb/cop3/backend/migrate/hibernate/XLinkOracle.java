package dk.kb.cop3.backend.migrate.hibernate;

import java.io.Serializable;

public class XLinkOracle implements Serializable{

    private String id;
    private String xlink_from;
    private String xlink_to;
    private String xlink_type;
    private String xlink_role;
    private String creator;
    private java.sql.Timestamp timestamp;
    private String xlink_title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getXlink_from() {
        return xlink_from;
    }

    public void setXlink_from(String xlink_from) {
        this.xlink_from = xlink_from;
    }

    public String getXlink_to() {
        return xlink_to;
    }

    public void setXlink_to(String xlink_to) {
        this.xlink_to = xlink_to;
    }

    public String getXlink_type() { return xlink_type; }

    public void setXlink_type(String xlink_type) { this.xlink_type = xlink_type; }

    public String getXlink_role() { return xlink_role; }

    public void setXlink_role(String xlink_role) { this.xlink_role = xlink_role; }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public  java.sql.Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(java.sql.Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getXlink_title() { return xlink_title; }

    public void setXlink_title(String xlink_title) { this.xlink_title = xlink_title; }
}

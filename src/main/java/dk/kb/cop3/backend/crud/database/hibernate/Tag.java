package dk.kb.cop3.backend.crud.database.hibernate;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Tag implements Serializable{

    private String id;
    private String tag_value;
    private String creator;
    //private String timestamp;
    private String xlink_to;
    private Set tagjoins = new HashSet(0);
    private java.sql.Timestamp timestamp;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTag_value() {
        return tag_value;
    }

    public void setTag_value(String tag_value) {
        this.tag_value = tag_value;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getXlink_to() {
        return xlink_to;
    }

    public void setXlink_to(String xlink_to) {
        this.xlink_to = xlink_to;
    }

    public Set getTagjoins() {
        return tagjoins;
    }

    public void setTagjoins(Set tagjoins) {
        this.tagjoins = tagjoins;
    }

    public  java.sql.Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(java.sql.Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

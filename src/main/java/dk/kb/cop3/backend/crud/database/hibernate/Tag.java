package dk.kb.cop3.backend.crud.database.hibernate;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: Andreas B. Westh
 * Date: 11/29/11
 * Time: 17:38 PM
 */
public class Tag implements Serializable{

    private String id;
    private String tag_value;
    private String creator;
    //private String timestamp;
    private String xlink_to;
    private Set objects = new HashSet(0);
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

    public Set getObjects() {
        return objects;
    }

    public void setObjects(Set objects) {
        this.objects = objects;
    }

    public  java.sql.Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(java.sql.Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

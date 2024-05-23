package dk.kb.cop3.backend.crud.database.hibernate;

import java.io.Serializable;
import java.sql.Timestamp;

public class TagJoin implements Serializable {

    private Object object;
    private Tag tag;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    private String creator;
    private java.sql.Timestamp timestamp;

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
    public boolean equals(java.lang.Object obj) {
        return obj.getClass() == this.getClass() &&
                this.object.getId() == ((TagJoin) obj).object.getId() &&
                this.tag.getId() == ((TagJoin) obj).tag.getId();
    }
}

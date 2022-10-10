package dk.kb.cop3.backend.migrate.hibernate;

import java.io.Serializable;

/**
 * Created by dgj on 04-07-2018.
 */
public class CommentOracle implements Serializable {

    private String text;
    private String id;
    private String creator;
    private String xlink_to;
    private java.lang.Object object;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public java.lang.Object getObject() {
        return object;
    }

    public void setObject(java.lang.Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "text='" + text + '\'' +
                ", id='" + id + '\'' +
                ", creator='" + creator + '\'' +
                ", xlink_to='" + xlink_to + '\'' +
                '}';
    }
}
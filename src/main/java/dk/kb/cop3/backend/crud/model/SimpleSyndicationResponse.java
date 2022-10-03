package dk.kb.cop3.backend.crud.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author: Andreas B. Westh
 * Date: 8/29/12
 * Time: 18:03 PM
 */
public class SimpleSyndicationResponse {

    public String id;
    public String title;
    public String author;
    public String lastmodified;
    public String latitude;
    public String longitude;
    public Date lastmodifiedDate;
    public HashMap categories = new HashMap();


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLastmodified() {
        return lastmodified;
    }

    public void setLastmodified(String lastmodified) {
        this.lastmodified = lastmodified;
    }

    public Date getLastmodifiedDate() {
        return lastmodifiedDate;
    }

    public void setLastmodifiedDate(Date lastmodifiedDate) {
        this.lastmodifiedDate = lastmodifiedDate;
    }

    public HashMap getCategories() {
        return categories;
    }

    public void setCategories(HashMap categories) {
        this.categories = categories;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}

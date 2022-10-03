package dk.kb.cop3.backend.commonutils;

import org.w3c.dom.Document;

/**
 * Created by IntelliJ IDEA.
 * User: jac
 * Date: 8/8/11
 * Time: 9:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class CachebleResponse {

    private Document doc = null;
    private String lastModifiedDate = "";

    public CachebleResponse(Document doc, String lastModifiedDate) {
        this.doc = doc;
        this.lastModifiedDate = lastModifiedDate;
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate.trim();
    }

    public long getLastModifiedDateAsNumber() {
        if (lastModifiedDate.trim().equals("")){
            return new Long(0);
        } else  {
            return Long.parseLong(lastModifiedDate.trim());
        }
    }


    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }


}

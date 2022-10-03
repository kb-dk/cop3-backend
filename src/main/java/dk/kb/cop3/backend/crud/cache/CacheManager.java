package dk.kb.cop3.backend.crud.cache;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import dk.kb.cop3.backend.commonutils.CachebleResponse;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: jac
 * Date: 18-04-11
 * Time: 14:30
 *
 * Denne klasse isolerer metoder til at gemme, hente og invalidere DOM Dokumenter fra
 * oscache.
 *
 */
public class CacheManager {

    private static Logger myLogger = Logger.getLogger(CacheManager.class);
    private GeneralCacheAdministrator admin = new GeneralCacheAdministrator();

    public static final int LONG_LIVING_OBJECT =  86400; // 24 hours
    public static final int MEDIUM_LIVING_OBJECT =  7200; // 2 hours
    public static final int SHORT_LIVING_OBJECT =  900; // 15 Minutes

    private static CacheManager ourInstance = new CacheManager();

    public static CacheManager getInstance() {
        return ourInstance;
    }

    private CacheManager() {}


    /**
     *
     *
     * @param id
     * @return
     */
    public CachebleResponse get(String id, int cacheExpiry){
        return null;
        /*try {
            CachebleResponse cr = (CachebleResponse) admin.getFromCache(id, cacheExpiry);
            myLogger.debug("getting object with id : " + id + ", from cache");
            return cr;
        } catch (NeedsRefreshException nre) { // either there was no entry entry with the given key, or it has expired
            myLogger.trace("no valid cacheentry was found for object with id: " + id);
            return null;
        } */
    }



    /**
     *
     * @param id
     * @param cr
     */
    public void put(String id, CachebleResponse cr){
        /*
        boolean updated = false;
        try{
            admin.putInCache(id,cr);
            updated = true;
        } finally {
            if (!updated) {
                admin.cancelUpdate(id);
            }
        } */
    }

    /**
     *
     * @param id
     */
    public void flush(String id){
        //admin.flushEntry(id);
    }

    public void remove(String id){
        //admin.removeEntry(id);
    }

    /****** Protected methods ****************/

    /**
     * Don't use this one. Always use one with exp. time.
     *
     * @param id
     * @return
     */
    protected CachebleResponse get(String id){
        try {
            CachebleResponse cr = (CachebleResponse) admin.getFromCache(id);
            myLogger.debug("getting object with id : " + id + ", from cache");
            return cr;
        } catch (NeedsRefreshException nre) { // either there was no entry entry with the given key, or it has expired
            myLogger.info("no valid cacheentry was found for object with id: " + id);
            return null;
        }
    }

    protected String getAlgorithm(){
        return admin.getProperty("cache.algorithm");
    }

    protected void setCapacity(int capacity){
        admin.setCacheCapacity(capacity);
    }

    protected int getCapacity(){
        return admin.getCache().getCapacity();
    }

    protected int cacheSize(){
        return admin.getCache().getSize();
    }

    protected void clearAll(){
        admin.flushAll();
    }

}

package dk.kb.cop3.backend.crud.cache;

import dk.kb.cop3.backend.commonutils.CachebleResponse;
//import dk.kb.cop3.backend.datacontroller.CopTransformerTest;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: jac
 * Date: 18-04-11
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */
public class CacheManagerTest {

    private static Logger myLogger = Logger.getLogger(CacheManagerTest.class);
    private static CacheManager manager = CacheManager.getInstance();

    private static final String MODS_FILE_1 = "testdata/cumulus-export/Luftfoto_OM/205/master_records/L0717_01.tif-mods.xml";
    private static final String MODS_FILE_2 = "testdata/cumulus-export/Luftfoto_OM/205/master_records/L0717_02.tif-mods.xml";
    private static final String MODS_FILE_3 = "testdata/cumulus-export/Luftfoto_OM/205/master_records/L0717_03.tif-mods.xml";

    private static Document mods1 = null;
    private static Document mods2 = null;
    private static Document mods3 = null;

    private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder builder = null;

    @BeforeClass
    public static void initTests(){
        // Set capacity to 2, to test algorithm
        manager.setCapacity(2);
        myLogger.debug("algo: " + manager.getAlgorithm());

        //Build DOM objects
         try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                myLogger.debug(e.getMessage());
            }
            try {
                mods1 = builder.parse(new File(MODS_FILE_1));
                mods2 = builder.parse(new File(MODS_FILE_2));
                mods3 = builder.parse(new File(MODS_FILE_3));
            } catch (SAXException e) {
                myLogger.error("Some weird SAX exception in the test: " + e.getMessage());
            } catch (IOException e) {
                myLogger.error("Some weird IO exception in the test: " + e.getMessage());
            }

    }

    @AfterClass
    public static void cleanUp(){
        manager.clearAll();
    }


    @Test
    public void testCacheEmpty(){
        assertEquals(0, manager.cacheSize());
    }

    @Test
    public void testGetFromEmptyCache(){
        assertNull(manager.get("this-is-an-invalid-key", CacheManager.SHORT_LIVING_OBJECT));
    }

    @Test
    public void testPutIntoCache(){
        manager.put("images:luftfo:2011:may:luftfoto:object62132",new CachebleResponse(mods1, null));
        assertTrue(true);  // Test of size in next test
    }

    //@Test
    public void testCacheNotEmpty(){
        assertEquals(1, manager.cacheSize());
    }

    //@Test
    public void testGetFromPopulatedCache(){
        CachebleResponse mods = manager.get("images:luftfo:2011:may:luftfoto:object62132", CacheManager.SHORT_LIVING_OBJECT);
        Assert.assertNotNull(mods);
        // Empty cache after test
        manager.remove("images:luftfo:2011:may:luftfoto:object62132");
    }


    /**
     *
     *  Use this test as a reference for the difference
     *  between remove and flush
     */
    //@Test
    public void testFlushAndRemove(){
        // Check the size = clean cache
        Assert.assertEquals(0, manager.cacheSize());

        // Populate with two objects
        manager.put("images:luftfo:2011:may:luftfoto:object62132",new CachebleResponse(mods1, null));
        manager.put("images:luftfo:2011:may:luftfoto:object62134",new CachebleResponse(mods2, null));

        // Check the size
        Assert.assertEquals(2, manager.cacheSize());

        // Get the objects from cache
        CachebleResponse myMods1 = manager.get("images:luftfo:2011:may:luftfoto:object62132", CacheManager.SHORT_LIVING_OBJECT);
        CachebleResponse myMods2 = manager.get("images:luftfo:2011:may:luftfoto:object62134", CacheManager.SHORT_LIVING_OBJECT);

        // Check that they are not null
        assertNotNull(myMods1);
        assertNotNull(myMods2);

        // For manuel inspection - write the files to filesystem
        //CopTransformerTest.writeXmlFile(myMods1, "mods1BeforeFlush.xml");
        //CopTransformerTest.writeXmlFile(myMods2, "mods2BeforeFlush.xml");

        // Flush one entry
        manager.flush("images:luftfo:2011:may:luftfoto:object62134");
        // Get that same entry from cache
        CachebleResponse myMods3 = manager.get("images:luftfo:2011:may:luftfoto:object62134");
        // Check that the result is null
        assertNull(myMods3);

        // For manuel inspection - write the file to filesystem
        // CopTransformerTest.writeXmlFile(myMods3, "mods3AfterFlush.xml");

        // Check that the size is still 2
        assertEquals(2, manager.cacheSize());

        // remove an object
        manager.remove("images:luftfo:2011:may:luftfoto:object62132");

        // Try to get it
        CachebleResponse myMods4 = manager.get("images:luftfo:2011:may:luftfoto:object62132");

        // Check that the entry is null
        assertNull(myMods4);

        // For manuel inspection - write the file to filesystem
        //CopTransformerTest.writeXmlFile(myMods4, "mods4AfterRemove.xml");

        // Check that the cache size is now 1
        assertEquals(1, manager.cacheSize());

        // Revove the last entry
        manager.remove("images:luftfo:2011:may:luftfoto:object62134");

        // Check that the cache size is now 0 = empty
        assertEquals(0, manager.cacheSize());

    }

    /**
     * Test that the expire parameter works as expected
     */
    //@Test
    public void testExpire(){
        // Check the size = clean cache
        Assert.assertEquals(0, manager.cacheSize());

        // Populate with two objects
        manager.put("images:luftfo:2011:may:luftfoto:object62132",new CachebleResponse(mods1, null));
        waiting(3);

        // Get this object if it is less that 15 minutes old
        CachebleResponse myMods1 = manager.get("images:luftfo:2011:may:luftfoto:object62132", CacheManager.SHORT_LIVING_OBJECT);

        // Get this object if it is less that 2 seconds old
        CachebleResponse myMods2 = manager.get("images:luftfo:2011:may:luftfoto:object62132", 2);

        // Get the same object again, to check that it is not flushed
        CachebleResponse myMods3 = manager.get("images:luftfo:2011:may:luftfoto:object62132", CacheManager.SHORT_LIVING_OBJECT);

        // The cache should not have expired for this object = notNull
        assertNotNull(myMods1);

        // This one should have expired
        assertNull(myMods2);

        // This one should not
        assertNotNull(myMods3);
    }

    /**
     * we use the LRU (Least Recently Used) algorithm. This is a test of it
     */
    //@Test
    public void testAlgorithm(){
        // Populate with two objects
        manager.put("images:luftfo:2011:may:luftfoto:object62132",new CachebleResponse(mods1, null));
        manager.put("images:luftfo:2011:may:luftfoto:object62134",new CachebleResponse(mods2, null));

        // Check the size. 2 = Cache.capacity
        Assert.assertEquals(2, manager.cacheSize());
        Assert.assertEquals(manager.getCapacity(), manager.cacheSize());

        // Get the first object a couple of times
        manager.get("images:luftfo:2011:may:luftfoto:object62134", CacheManager.SHORT_LIVING_OBJECT);
        manager.get("images:luftfo:2011:may:luftfoto:object62134", CacheManager.SHORT_LIVING_OBJECT);
        manager.get("images:luftfo:2011:may:luftfoto:object62132", CacheManager.SHORT_LIVING_OBJECT);

        // This should cause the cache to remove one of its entries
        manager.put("images:luftfo:2011:may:luftfoto:object62136", new CachebleResponse(mods3, null));

        // the size is still 2
        Assert.assertEquals(2, manager.cacheSize());

        // thisOne should still be in the cache, since it was used last
        CachebleResponse myMods1 = manager.get("images:luftfo:2011:may:luftfoto:object62132", CacheManager.SHORT_LIVING_OBJECT);
        assertNotNull(myMods1);

        // this one should not, even though it is more popular
        CachebleResponse myMods2 = manager.get("images:luftfo:2011:may:luftfoto:object62134", CacheManager.SHORT_LIVING_OBJECT);
        assertNull(myMods2);

        // this one should have taken the place in the cache
        CachebleResponse myMods3 = manager.get("images:luftfo:2011:may:luftfoto:object62136", CacheManager.SHORT_LIVING_OBJECT);
        assertNotNull(myMods3);
    }



    /**
     *  Wait n seconds
      * @param n
     */
     private static void waiting (int n){
        long t0, t1;
        t0 =  System.currentTimeMillis();
        do{
            t1 = System.currentTimeMillis();
        }
        while ((t1 - t0) < (n * 1000));
    }


    public static void prePopulateCache(){
        Document mods = null;
        try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                myLogger.debug(e.getMessage());
            }
            try {
                mods = builder.parse(new File(MODS_FILE_1));
            } catch (SAXException e) {
                myLogger.error("Some weird SAX exception in the test: " + e.getMessage());
            } catch (IOException e) {
                myLogger.error("Some weird IO exception in the test: " + e.getMessage());
            }

        manager.put("images:luftfo:2011:may:luftfoto:object62132",new CachebleResponse(mods, null));
        assertTrue(true);
    }


}

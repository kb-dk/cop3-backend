package dk.kb.cop3.backend.crud.oai.server.catalog;

import ORG.oclc.oai.server.catalog.RecordFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: dgj
 * Date: 01-11-11
 * Time: 10:17
 * To change this template use File | Settings | File Templates.
 */
public class COP2RecordFactory extends RecordFactory {
    private static final Logger logger = LoggerFactory.getLogger(COP2RecordFactory.class);

   /**
     * Construct an COPRecordFactory capable of producing the Crosswalk(s)
     * specified in the properties file.
     * @param properties Contains information to configure the factory:
     *                   specifically, the names of the crosswalk(s) supported
     * @exception IllegalArgumentException Something is wrong with the argument.
     */
    public COP2RecordFactory (Properties properties) throws IllegalArgumentException {
        super(properties);
    }

   /**
     * Utility method to parse the 'local identifier' from the OAI identifier
     *
     * @param s OAI identifier (e.g. oai:kb.dk:Kistebilleder/12345)
     * @return local identifier (e.g. Kistebilleder:12345).
     */
    @Override
    public String fromOAIIdentifier(String s) {
        if (s == null) return null;
        return s.replace( "oai:kb.dk" , "" ).replaceAll( ":" , "/" );
    }

    /**
     * Allows classes that implement RecordFactory to override the default create() method.
     * This is useful, for example, if the entire &lt;record&gt; is already packaged as the native
     * record. Return null if you want the default handler to create it by calling the methods
     * above individually.
     *
     * @param nativeItem the native record
     * @param schemaLocation the schemaURL desired for the response
     * @param metadataPrefix the metadataPrefix from the request
     * @return a String containing the OAI &lt;record&gt; or null if the default method should be
     * used.
     */
    @Override
    public String quickCreate( Object nativeItem , String schemaLocation , String metadataPrefix ) {
        // We Don't perform quick creates
        return null;
    }


    /**
     * Construct an OAI identifier from the native item
     *
     * @param nativeItem native Item object
     * @return OAI identifier
     */
    @Override
    public String getOAIIdentifier( Object nativeItem ) {
        return "oai:kb.dk" + ((OaiRecordData)nativeItem).getId().replaceAll( "/" , ":" );
    }

    /**
     * get the datestamp from the item
     *
     * @param nativeItem a native item presumably containing a datestamp somewhere
     * @return a String containing the datestamp for the item
     * @exception IllegalArgumentException Something is wrong with the argument.
     */
    @Override
    public String getDatestamp( Object nativeItem )
    throws IllegalArgumentException  {
        return ((OaiRecordData)nativeItem).getDateStamp();
    }

    /**
     * get the setspec from the item
     *
     * @param nativeItem a native item presumably containing a setspec somewhere
     * @return a String containing the setspec for the item
     * @exception IllegalArgumentException Something is wrong with the argument.
     */
    @Override
    public Iterator getSetSpecs( Object nativeItem )throws IllegalArgumentException  {
        ArrayList list = new ArrayList();
        list.add( ((OaiRecordData)nativeItem).getSetInfo() );
        return list.iterator();
    }

    /**
     * Is the record deleted?
     *
     * @param nativeItem a native item presumably containing a possible delete indicator
     * @return true if record is deleted, false if not
     * @exception IllegalArgumentException Something is wrong with the argument.
     */
    @Override
    public boolean isDeleted( Object nativeItem ) throws IllegalArgumentException {
        // we never delete anything
        return false;
    }

    /**
     * Get the about elements from the item
     *
     * @param nativeItem a native item presumably containing about information somewhere
     * @return a Iterator of Strings containing &lt;about&gt;s for the item
     * @exception IllegalArgumentException Something is wrong with the argument.
     */
    @Override
    public Iterator getAbouts( Object nativeItem ) throws IllegalArgumentException {
        return null;
    }
}

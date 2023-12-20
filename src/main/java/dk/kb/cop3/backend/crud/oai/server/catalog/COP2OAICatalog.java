package dk.kb.cop3.backend.crud.oai.server.catalog;

import ORG.oclc.oai.server.catalog.AbstractCatalog;
import ORG.oclc.oai.server.verb.*;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: dgj
 * Date: 31-10-11
 * Time: 14:31
 * To change this template use File | Settings | File Templates.
 *
 * COP2OAICatalog interfaces with the COP repository over digitized assets
 * of the Royal Library.
 *
*/

public class COP2OAICatalog extends AbstractCatalog {
    private static final Logger logger = LoggerFactory.getLogger(COP2OAICatalog.class);
    private static final String elementName = "md:mods";
    private static final String elementStart = "<" + elementName;
    private static final String elementEnd = elementName + "/>";

    private int maxListSize;
    private I_OaiDbLayer db;


    public COP2OAICatalog( Properties properties ) {
        String maxListSize = properties.getProperty( "COP2OAICatalog.maxListSize" );
        if ( maxListSize == null ) {
            throw new IllegalArgumentException( "COP2OAICatalog.maxListSize is missing from the properties file" );
        } else {
            this.maxListSize = Integer.parseInt( maxListSize );
        }
        String dbLayer = properties.getProperty( "COP2OAICatalog.dbLayer" );
        if ( maxListSize == null ) {
            throw new IllegalArgumentException( "COP2OAICatalog.dbLayer is missing from the properties file" );
        } else {
            try {
                this.db = (I_OaiDbLayer)(Class.forName( dbLayer ).newInstance());
            } catch ( Exception e ) {
                throw new IllegalArgumentException( "dbLayer " + dbLayer + "not found" );
            }
            this.db.init( properties );
        }
    }


    /**
     * Retrieve a list of sets that satisfy the specified criteria
     *
     * @return a Map object containing "sets" Iterator object (contains
     * <setSpec/> XML Strings) as well as an optional resumptionMap Map.
     * @exception OAIInternalServerError signals an http status code 500 problem (db problem)
     */
    @Override
    public Map listSets() throws OAIInternalServerError {
        Map listSetsMap = new HashMap();
        ArrayList sets = new ArrayList();

        /* fetch list of sets from database */
        List<Edition> dbSets = db.getSets();
        int count;

        /* load the sets ArrayList */
        for ( count = 0 ; count < maxListSize && count < dbSets.size() ; ++count ) {
            sets.add(constructSet(dbSets.get(count)));
        }

        /* decide if you're done */
        if ( count < dbSets.size() ) {
            // Construct the resumptionToken
            StringBuffer resumptionTokenSb = new StringBuffer();
            resumptionTokenSb.append( "KBSET" );
            resumptionTokenSb.append( "!" );
            resumptionTokenSb.append( Integer.toString( count ) );

            listSetsMap.put("resumptionMap",getResumptionMap(resumptionTokenSb.toString(),dbSets.size(),0));
        }

        listSetsMap.put("sets", sets.iterator());
        return listSetsMap;
    }

    /**
     * Retrieve the next set of sets associated with the resumptionToken
     *
     * @param resumptionToken implementation-dependent format taken from the
     * previous listSets() Map result.
     * @return a Map object containing "sets" Iterator object (contains
     * <setSpec/> XML Strings) as well as an optional resumptionMap Map.
     * @exception BadResumptionTokenException the value of the resumptionToken
     * @exception OAIInternalServerError signals an http status code 500 problem (db problem)
     * is invalid or expired.
     */
    @Override
    public Map listSets(String resumptionToken)throws BadResumptionTokenException, OAIInternalServerError {
        Map listSetsMap = new HashMap();
        ArrayList sets = new ArrayList();

        //parse the resumptionToken

        StringTokenizer tokenizer = new StringTokenizer( resumptionToken , "!" );
        String resumptionId;
        int oldCount;
        try {
            resumptionId = tokenizer.nextToken();
            oldCount = Integer.parseInt( tokenizer.nextToken() );
        } catch ( NoSuchElementException e ) {
            throw new BadResumptionTokenException();
        }

        /* Get some more sets */
        List<Edition > dbSets = db.getSets();
        if ( dbSets == null ) {
            throw new BadResumptionTokenException();
        }
        int count;

        /* load the sets ArrayList */
        for ( count = 0 ; count < maxListSize && count + oldCount < dbSets.size() ; ++count ) {
            sets.add( constructSet( dbSets.get( count + oldCount ) ) );
        }

        /* decide if we're done */
        if ( count + oldCount < dbSets.size() ) {
            // Construct the resumptionToken String
            StringBuffer resumptionTokenSb = new StringBuffer();
            resumptionTokenSb.append( resumptionId );
            resumptionTokenSb.append( "!" );
            resumptionTokenSb.append( Integer.toString( oldCount + count ) );

            listSetsMap.put( "resumptionMap" , getResumptionMap( resumptionTokenSb.toString(),
                                                                 dbSets.size(),
                                                                 oldCount ) );
        }
        listSetsMap.put( "sets" , sets.iterator() );
        return listSetsMap;
    }

    /**
     * Retrieve a list of schemaLocation values associated with the specified
     * identifier.
     *
     * @param identifier the OAI identifier
     * @return a Vector containing schemaLocation Strings
     * @exception IdDoesNotExistException the specified identifier can't be found
     * @exception NoMetadataFormatsException the specified identifier was found
     * but the item is flagged as deleted and thus no schemaLocations (i.e.
     * metadataFormats) can be produced.
     */
    @Override
    public Vector getSchemaLocations( String identifier ) throws IdDoesNotExistException, NoMetadataFormatsException, OAIInternalServerError {
        Object nativeItem = db.getRecord(getRecordFactory().fromOAIIdentifier( identifier ));
        /*
         * We let recordFactory decide which schemaLocations
         * (i.e. metadataFormats) it can produce from the record.
         * Doing this preserves the separation of database access
         * (which happens here) from the record content interpretation
         * (which is the responsibility of the RecordFactory implementation).
         */
        if ( nativeItem == null ) {
            throw new IdDoesNotExistException( identifier );
        } else {
            return getRecordFactory().getSchemaLocations( nativeItem );
        }
    }
    @Override
    /**
     * Retrieve a list of identifiers that satisfy the specified criteria
     *
     * @param from beginning date using the proper granularity
     * @param until ending date using the proper granularity
     * @param set the set name or null if no such limit is requested
     * @param metadataPrefix the OAI metadataPrefix or null if no such limit is requested
     * @return a Map object containing entries for "headers" and "identifiers" Iterators
     * (both containing Strings) as well as an optional "resumptionMap" Map.
     * It may seem strange for the map to include both "headers" and "identifiers"
     * since the identifiers can be obtained from the headers. This may be true, but
     * AbstractCatalog.listRecords() can operate quicker if it doesn't
     * need to parse identifiers from the XML headers itself. Better
     * still, do like I do below and override AbstractCatalog.listRecords().
     * AbstractCatalog.listRecords() is relatively inefficient because given the list
     * of identifiers, it must call getRecord() individually for each as it constructs
     * its response. It's much more efficient to construct the entire response in one fell
     * swoop by overriding listRecords() as we do here.
     */
    public Map listIdentifiers(String from, String until, String set, String format) throws BadArgumentException, CannotDisseminateFormatException, NoItemsMatchException, NoSetHierarchyException, OAIInternalServerError {
        Map listIdentifiersMap = new HashMap();
        ArrayList headers = new ArrayList();
        ArrayList identifiers = new ArrayList();

        int totalNumberOfRecords = db.countRecordsInSet(from,until,getRecordFactory().fromOAIIdentifier(set));

        if (totalNumberOfRecords == 0)
            throw new NoItemsMatchException();

        List<OaiRecordData> nativeItems = db.getRecords(from,until,getRecordFactory().fromOAIIdentifier(set),0,maxListSize);

        /* load the headers and identifiers ArrayLists. */
        for (OaiRecordData item: nativeItems) {
            /* Use the RecordFactory to extract header/identifier pairs for each item */
            String[] header = getRecordFactory().createHeader(item);
            headers.add(header[0]);
            identifiers.add(header[1]);
        }


        if (maxListSize < totalNumberOfRecords) {
            //  We are not done yet issue resumptionToken
            String resumptionToken = "KB!"+maxListSize+"!"+format+"!"+from+"!"+until+"!"+set;
            listIdentifiersMap.put("resumptionMap", getResumptionMap(resumptionToken,totalNumberOfRecords,0));
        }
        listIdentifiersMap.put( "headers" , headers.iterator() );
        listIdentifiersMap.put( "identifiers" , identifiers.iterator() );
        return listIdentifiersMap;
    }

    @Override
    public Map listIdentifiers(String resumptionToken) throws BadResumptionTokenException, OAIInternalServerError {
        Map listIdentifiersMap = new HashMap();
        ArrayList headers = new ArrayList();
        ArrayList identifiers = new ArrayList();

        String resumptionId;
        int count;
        String metadataPrefix;
        String from;
        String until;
        String set;

        //Parse the resumptionToken
        StringTokenizer tokenizer = new StringTokenizer( resumptionToken , "!" );
                try {
            resumptionId = tokenizer.nextToken();
            count = Integer.parseInt( tokenizer.nextToken() );
            metadataPrefix = tokenizer.nextToken();
            from = tokenizer.nextToken();
            until = tokenizer.nextToken();
            set = tokenizer.nextToken();
        } catch ( NoSuchElementException e ) {
            throw new BadResumptionTokenException();
        } catch (NumberFormatException e) {
            logger.debug("count is not a number "+e.getMessage());
            throw new BadResumptionTokenException();
        }
        if (set.equals("null")) set = null;
        try {
            int totalNumberOfRecords = db.countRecordsInSet(from,until,getRecordFactory().fromOAIIdentifier(set));
            List<OaiRecordData> nativeItems = db.getRecords(from,until,getRecordFactory().fromOAIIdentifier(set),count,maxListSize);

            /* load the headers and identifiers ArrayLists. */
            for (OaiRecordData item: nativeItems) {
                /* Use the RecordFactory to extract header/identifier pairs for each item */
                String[] header = getRecordFactory().createHeader(item);
                headers.add(header[0]);
                identifiers.add(header[1]);
            }

            logger.debug(count+" "+maxListSize+" "+totalNumberOfRecords);
            if (count + maxListSize < totalNumberOfRecords) {
                //  We are not done yet issue resumptionToken
                String newResumptionToken = "KB!"+maxListSize+"!"+set+"!"+from+"!"+until+"!"+set;
                listIdentifiersMap.put("resumptionMap", getResumptionMap(newResumptionToken,totalNumberOfRecords,count));
            }
            listIdentifiersMap.put( "headers" , headers.iterator() );
            listIdentifiersMap.put( "identifiers" , identifiers.iterator() );

        } catch (BadArgumentException ex) {
            logger.debug(set+" not a set "+ex.getMessage());
            throw new BadResumptionTokenException();
        }
        return listIdentifiersMap;
    }

    @Override
    public String getRecord(String id, String format) throws IdDoesNotExistException, CannotDisseminateFormatException, OAIInternalServerError {
        //TODO: support for oai_dc ?
        logger.debug("Getting record id:'"+id+"' format="+format);
        OaiRecordData data = db.getRecord(getRecordFactory().fromOAIIdentifier(id));
        if (data == null) throw new IdDoesNotExistException(id);
        return constructRecord(data,format);
    }

    /* We implement listRecords though not strictly needet to avoid fetching all records from the db twice */

     /**
     * Retrieve a list of records that satisfy the specified criteria. Note, though,
     * that unlike the other OAI verb type methods implemented here, both of the
     * listRecords methods are already implemented in AbstractCatalog rather than
     * abstracted. This is because it is possible to implement ListRecords as a
     * combination of ListIdentifiers and GetRecord combinations. Nevertheless,
     * we override both the AbstractCatalog.listRecords methods
     * here since it will probably improve the performance if you create the
     * response in one fell swoop rather than construct it one GetRecord at a time.
     *
     * @param from beginning date using the proper granularity
     * @param until ending date using the proper granularity
     * @param set the set name or null if no such limit is requested
     * @param metadataPrefix the OAI metadataPrefix or null if no such limit is requested
     * @return a Map object containing entries for a "records" Iterator object
     * (containing XML <record/> Strings) and an optional "resumptionMap" Map.
     */
    @Override
    public Map listRecords(String from , String until , String set , String metadataPrefix) throws OAIInternalServerError, BadArgumentException, NoItemsMatchException, CannotDisseminateFormatException {
        Map listRecordsMap = new HashMap();
        ArrayList records = new ArrayList();
        logger.debug("set ="+set);
        int totalNumberOfRecords = db.countRecordsInSet(from,until,getRecordFactory().fromOAIIdentifier(set));

        if (totalNumberOfRecords == 0)
            throw new NoItemsMatchException();

        List<OaiRecordData> nativeItems = db.getRecords(from,until,getRecordFactory().fromOAIIdentifier(set),0,maxListSize);

        /* load the headers and identifiers ArrayLists. */
        for (OaiRecordData item: nativeItems) {
            records.add(constructRecord(item,metadataPrefix));
        }

        if (maxListSize < totalNumberOfRecords) {
            //  We are not done yet issue resumptionToken
            String resumptionToken = "KB!"+maxListSize+"!"+metadataPrefix+"!"+from+"!"+until+"!"+set;
            listRecordsMap.put("resumptionMap", getResumptionMap(resumptionToken,totalNumberOfRecords,0));
        }

        listRecordsMap.put("records",records.iterator());
        return listRecordsMap;
    }


    /**
     * Retrieve the next set of records associated with the resumptionToken
     *
     * @param resumptionToken implementation-dependent format taken from the
     * previous listRecords() Map result.
     * @return a Map object containing entries for "headers" and "identifiers" Iterators
     * (both containing Strings) as well as an optional "resumptionMap" Map.
     */
    @Override
    public Map listRecords( String resumptionToken ) throws BadResumptionTokenException,OAIInternalServerError {
        Map listRecordsMap = new HashMap();
        ArrayList records = new ArrayList();

                String resumptionId;
        int count;
        String metadataPrefix;
        String from;
        String until;
        String set;

        //Parse the resumptionToken
        StringTokenizer tokenizer = new StringTokenizer( resumptionToken , "!" );
        try {
            resumptionId = tokenizer.nextToken();
            count = Integer.parseInt( tokenizer.nextToken() );
            metadataPrefix = tokenizer.nextToken();
            from = tokenizer.nextToken();
            until = tokenizer.nextToken();
            set = tokenizer.nextToken();
        } catch ( NoSuchElementException e ) {
            throw new BadResumptionTokenException();
        } catch (NumberFormatException e) {
            logger.debug("count is not a number "+e.getMessage());
            throw new BadResumptionTokenException();
        }
        if (set.equals("null")) set = null;

        try {
            int totalNumberOfRecords = db.countRecordsInSet(from,until,getRecordFactory().fromOAIIdentifier(set));
            List<OaiRecordData> nativeItems = db.getRecords(from,until,getRecordFactory().fromOAIIdentifier(set),count,maxListSize);

            /* load the headers and identifiers ArrayLists. */
            for (OaiRecordData item: nativeItems) {
                records.add(constructRecord(item,metadataPrefix));
            }

            if (count + maxListSize < totalNumberOfRecords) {
                //  We are not done yet issue resumptionToken
                String newResumptionToken = "KB!"+(count+maxListSize)+"!"+metadataPrefix+"!"+from+"!"+until+"!"+set;
                listRecordsMap.put("resumptionMap", getResumptionMap(newResumptionToken,totalNumberOfRecords,count));
            }


            listRecordsMap.put("records",records.iterator());
            return listRecordsMap;
        } catch (BadArgumentException ex) {
            logger.error("Bad Argument Exception "+ex.getMessage());
            throw new BadResumptionTokenException();
        } catch (CannotDisseminateFormatException ex) {
            logger.error("Cannot Dissiminate Record  +ex.getMessage()");
            throw new BadResumptionTokenException();
        }
    }


    @Override
    public void close() {
        db.close();
    }

    private String constructSet(Edition ed ) {
        StringBuffer sb = new StringBuffer();
        sb.append( "<set>" );

        sb.append( "<setSpec>" + ("oai:kb.dk" + ed.getId()).replace("/",":") + "</setSpec>" );
        sb.append( "<setName>" + ed.getName() + "</setName>" );
        sb.append( "<setDescription><oai_dc:dc xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd\">" );
        sb.append( "<dc:description>" + ed.getDescription() + "</dc:description>" );
        sb.append( "</oai_dc:dc></setDescription>" );
        sb.append( "</set>" );

        return sb.toString();
    }

    /**
     * Utility method to construct a Record object for a specified
     * metadataFormat from a native record
     *
     * @param nativeItem native item from the dataase
     * @param metadataPrefix the desired metadataPrefix for performing the crosswalk
     * @return the <record/> String
     * @exception CannotDisseminateFormatException the record is not available
     * for the specified metadataPrefix.
     */
    private String constructRecord(OaiRecordData nativeItem, String metadataPrefix) throws CannotDisseminateFormatException, OAIInternalServerError {
        String schemaURL = null;
        Iterator setSpecs = getRecordFactory().getSetSpecs(nativeItem);
        Iterator abouts = getRecordFactory().getAbouts(nativeItem);

        if (metadataPrefix != null) {
            if ((schemaURL = getCrosswalks().getSchemaURL(metadataPrefix)) == null)
                throw new CannotDisseminateFormatException(metadataPrefix);
        }
        return getRecordFactory().create(nativeItem, schemaURL, metadataPrefix, setSpecs, abouts);
    }

}

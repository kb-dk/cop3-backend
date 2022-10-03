package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.database.hibernate.Category;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.database.hibernate.Type;
import dk.kb.cop3.backend.crud.database.hibernate.Object;

import java.math.BigDecimal;

/**
 * Interface for various metadata sources such as a relational
 * database, native XML database, a lucene index or even a file
 * system. All the basic functionalities of the metadata sources are
 * declared here.
 *
 * @author Sigfrid Lundberg (slu@kb.dk)
 * @version $rev$ last changed $LastChangedDate$
 */
public interface MetadataSource {


    /**
     * There are some data that are known only from the configuration file
     *
     * @param config Global configuration property object
     */
    public void setConfiguration(java.util.Properties config);

    /*
     * Below we list a number of get & set methods used for setting (or
     * getting) search criteria
     */

    /**
     * For some operations it helps if the MetadataSource has an
     * overview of what editions there is out there.
     *
     * @param id of the Edition to which an object belongs
     */
    public void setEdition(String id);

    /**
     * @return the Edition bean of the object
     */
    public Edition getEdition();

    /**
     * Selects data belowing the the category given as parameter
     *
     * @param category category
     */
    public void setCategory(String category);

    public Category getCategory();

    public void setType(String type);

    public Type getType();

    /**
     * Selects data with geo position within the bounding box given in a
     * string of the form
     * <p/>
     * lower left x, lower left y, upper right x, upper right y
     * <p/>
     * The get method returns the bbo in the same format
     */
    public void setBoundingBox(String boundingBox);

    public String getBoundingBox();

    /**
     * Various dates useful for search and/or sorting
     */
    public void setNotBefore(String date);

    public String getNotBefore();

    public void setNotAfter(String date);

    public String getNotAfter();


    /**
     * Modified before and Modified after is used by oai server
     */
    public void setModifiedBefore(String date);
    public String getModifiedBefore(String date);
    public void setModifiedAfter(String date);
    public String getModifiedAfter(String date);


    /**
     *  Limit the result to show only public or only hidden results


    public String setVisibleToPublic(String visible_to_public);

    public String getVisibleToPublic();
      */
    /**
     * @return The search terms
     */
    public void setSearchterms(String terms);

    /**
     * for search in specific fields supported by the database. Eg., creator,
     * title, location, person etc
     */
    public void setSearchterms(String field, String terms);

    public String getSearchterms();

    /*
     * The set & get methods for resultset navigation
     */

    /**
     * @return Record sequence number requested
     */
    public void setOffset(int offset);

    public int getOffset();


    /**
     *
     */
    public void setCorrectness(BigDecimal correctness);

    public BigDecimal getCorrectness();

    /**
     * @return Number of records per page
     */
    public void setNumberPerPage(int number);

    public int getNumberPerPage();

    /**
     * @param the fraction of hits to be returned
     */
    public void setRandom(double fraction);

    /**
     * @return the fraction of hits to be used
     */
    public double getRandom();

    /**
     * Execute a search to produce a resultset
     * throws Exception if search fails
     */

    public void setSortcolumn(String column);
    public String getSortcolumn();

    /**
     *
     * @param order
     * -1: descending
     * 1: ascending
     *
     */
    public void setSortorder(int order);
    public int getSortorder();

    public void execute();


    /*
     * methods for retrieval
     */

    /**
     * Returns the number of hits for a given MetadataSource
     *
     * @return the total number of hits in the result set
     */
    public java.lang.Long getNumberOfHits();

    /**
     * Selects data belowing the the category given as parameter
     *
     * @param offset the first record to be returned
     * @param number the maximum number of record to be returned
     */

    public boolean hasMore();

    /**
     * Retrieve another record
     *
     * @return another XML object as a string
     */
    public Object getAnother();

}


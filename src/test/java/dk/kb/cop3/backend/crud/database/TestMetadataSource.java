package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.database.hibernate.Category;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.database.hibernate.Type;
import oracle.spatial.geometry.JGeometry;

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
public class TestMetadataSource implements MetadataSource {
    private boolean delivered = false;

    org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(TestMetadataSource.class.getPackage().getName());


    /**
     * There are some data that are known only from the configuration file
     *
     * @param config Global configuration property object
     */
    public void setConfiguration(java.util.Properties config) {

    }

    /**
     * Determine whether the source can deliver metadata matching some
     * text search criterion
     *
     * @return true if searching is supported
     */
    public boolean canDeliverDataBySearch() {
        return false;
    }

    /**
     * Determine whether the source can deliver metadata belonging to a
     * given category
     *
     * @return true if searching is supported
     */
    public boolean canDeliverDataByCategory() {
        return true;
    }


    /**
     * @return The search terms
     */
    public String getSearchTerms() {
        return "";
    }


    /**
     * Get a single record given its ID
     */
    public void selectById(String id) {

    }

    public void selectBySearch(String searchTerms) {

    }

    /**
     * Performs a search for the terms submitted.
     *
     * @param searchTerms a string containing the terms to search for. The exact syntax of the string may depend upon the metadata source used
     * @param offset      the first record to be returned
     * @param number      the maximum number of record to be returned
     * @return number of hits to be retrieved
     */
    public void selectBySearch(String searchTerms, int offset, int number) {

    }

    /**
     * Selects data belowing the the category given as parameter
     *
     * @param category category
     * @return number of hits to be retrieved
     */
    public void selectByCategory(String category) {

    }

    /**
     * Returns the number of hits for a given MetadataSource
     *
     * @return the total number of hits in the result set
     */
    public java.lang.Long getNumberOfHits() {
        return new java.lang.Long("84");
    }

    public void setBoundingBox(String boundingBox) {} 
    public String getBoundingBox() { return ""; }


    /**
     * The size of the current chunk of the result set
     *
     * @return the number of hits to be returned in the current chunk.
     */
    public int getReturnHits() {
        return 40;
    }

    /**
     * Selects data belowing the the category given as parameter
     *
     * @param offset the first record to be returned
     * @param number the maximum number of record to be returned
     */
    public void selectByCategory(String searchTerms, int offset, int number) {

    }

    /**
     * Determines whether there is more data to retrieve
     *
     * @return true if there is at least one more metadata object to be retrieved
     */
    public boolean hasMore() {
        return !this.delivered;
    }

    /**
     * Retrieve another record
     *
     * @return another XML object as a string
     */
    public dk.kb.cop3.backend.crud.database.hibernate.Object getAnother() {
        this.delivered = true;
        try {
            java.io.File file = new java.io.File("misc/mods-example/judaica_manuscript.xml");

            logger.debug("Reading from file " + file.getName());
            java.lang.StringBuilder text = new java.lang.StringBuilder();
            String NL = System.getProperty("line.separator");
            java.util.Scanner scanner = new java.util.Scanner(new java.io.FileInputStream(file));
            try {
                while (scanner.hasNextLine()) {
                    text.append(scanner.nextLine() + NL);
                }
            } finally {
                scanner.close();
            }
            logger.debug("Text read");
            dk.kb.cop3.backend.crud.database.hibernate.Object obj =
                    new dk.kb.cop3.backend.crud.database.hibernate.Object();
            obj.setMods(text.toString());
            return obj;
        } catch (java.io.FileNotFoundException noFile) {
            logger.error(noFile.getMessage());
            return new dk.kb.cop3.backend.crud.database.hibernate.Object();
        }
    }

    /**
     * Retrieve another record in a given format
     *
     * @param format any supported format
     * @return another XML object as a string
     */
    public String getAnother(String format) {
        return "";
    }

    /**
     * Retrieve the ID of the current record
     *
     * @return another ID as a string
     */
    public java.lang.String getMetadataId() {
        return "/images/luftfo/2011/apr/luftfo/object42";
    }

    public void execute() {
    }

    public void setGeometry(JGeometry polygon) {
    }

    public JGeometry getGeometry() {
        return null;
    }

    public void setNotBefore(String date) {
    }

    public String getNotBefore() {
        return "";
    }

    public void setNotAfter(String date) {
    }

    public String getNotAfter() {
        return "";
    }

    public void setEdition(String id) {

    }

    public Edition getEdition() {
        return null;
    }

    public void setCategory(String category) {

    }

    public Category getCategory() {
        return null;
    }

    public void setType(String type) {

    }

    public Type getType() {
        return (Type)null;
    }

    public void setNumberPerPage(int number) {

    }

    @Override
    public BigDecimal getCorrectness() {
        return null;
    }

    @Override
    public void setCorrectness(BigDecimal correctness) {

    }

    public int getNumberPerPage() {
        return 40;
    }

    public void setOffset(int offset) {

    }

    public int getOffset() {
        return 0;
    }

    public void setSearchterms(String terms) {
    }

    public void setSearchterms(String field, String terms) {}

    public String getSearchterms() {
        return "";
    }

    /**
     * @param the fraction of hits to be returned
     */ 
    public void setRandom(double fraction) {}

    /**
     * @return the fraction of hits to be used
     */
    public double getRandom() {  return 3.14159; }

    @Override
    public void setSortcolumn(String column) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getSortcolumn() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setSortorder(int order) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getSortorder() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setModifiedBefore(String s) {

    }

    @Override
    public String getModifiedBefore(String s) {
        return null;
    }

    @Override
    public void setModifiedAfter(String s) {

    }

    @Override
    public String getModifiedAfter(String s) {
        return null;
    }
}


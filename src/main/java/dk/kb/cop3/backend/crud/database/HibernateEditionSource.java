package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.lang.Long;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of MetadataSource interface for retrieving data about editions rather than objects
 *
 * @author Sigfrid Lundberg based on code by David Grove Jorgensen (dgj@kb.dk)
 */
public class HibernateEditionSource {

    private static Logger logger = Logger.getLogger(HibernateEditionSource.class);

    Edition edition = null;

    private Session session;
    private List<Edition> resultSet = null;
    private Iterator<Edition> resultIterator = null;

    /**
     * Initialize the source
     *
     * @param session an open hibernate session to use
     */
    public HibernateEditionSource(Session session) {
        this.session = session;
    }

    public void setEdition(String id) {
        try {
            this.edition = (Edition) session.load(Edition.class, id);
        } catch (HibernateException ex) {
            logger.error("Error when setting edition for id: " + id, ex);
            throw new NullPointerException("setEdition(" + id + ") error:" + ex.getMessage());
        }
    }

    public Edition getEdition() {
        return this.edition;
    }

    /**
     * Execute a search to produce a resultset
     * throws Exception if search fails
     */
    public void execute() {
        /* check if search has already been executed */
        if (this.resultSet == null) {
            try {
                // The second worst hack in history of modern Computing.
                // DGJ and ABWE is the masterminds behinds this.
                // Do not display editions where where visible to public is equal to 0.
                resultSet = session.createQuery("from Edition where VISIBLE_TO_PUBLIC ='1'").list();
                resultIterator = resultSet.iterator();
            } catch (HibernateException ex) {
                logger.error("The search failed", ex);
                throw new RuntimeException("Hibernate metadata search failed: " + ex.getMessage());
            }
        }

    }

    /**
     * Returns the number of hits for a given MetadataSource
     *
     * @return the total number of hits in the result set
     */
    public Long getNumberOfHits() {
        if (this.resultSet == null) {
            logger.error("Unable to get number of hits: search has not been executed");
            throw new NullPointerException("Unable to get number of hits: search has not been executed");
        }
        return new Long("" + this.resultSet.size());
    }


    /**
     * Determines whether there is more data to retrieve
     *
     * @return true if there is at least one more metadata object to be retrieved
     */
    public boolean hasMore() {
        if (this.resultSet == null) {
            logger.error("hasMore error: search has not been executed");
            throw new NullPointerException("hasMore error: search has not been executed");
        }
        return this.resultIterator.hasNext();
    }

    /**
     * Retrieve another record
     *
     * @return another XML object as a string
     */
    public Edition getAnother() {
        if (this.resultSet == null) {
            logger.error("Unable to get next object: search has not been executed");
            throw new NullPointerException("Unable to get next object: search has not been executed");
        }
        return this.resultIterator.next();
    }
}


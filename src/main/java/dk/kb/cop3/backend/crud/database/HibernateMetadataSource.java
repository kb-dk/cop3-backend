package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.database.hibernate.Category;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.database.hibernate.Tag;
import dk.kb.cop3.backend.crud.database.hibernate.Type;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.*;
import org.hibernate.transform.DistinctRootEntityResultTransformer;
import org.hibernate.type.StringType;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Implementation of MetadataSource interface, that uses a hibernate session to perform searches and retreive data.
 *
 * @author David Grove Jorgensen (dgj@kb.dk)
 */
public class HibernateMetadataSource implements MetadataSource {

    private static Logger logger = Logger.getLogger(HibernateMetadataSource.class);

    protected static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    protected org.hibernate.Session session;

    protected Category category = null;
    protected Edition edition = null;
    protected Type type = null;
    protected String boundingBox = null;
    protected String notBefore = null;
    protected String notAfter = null;
    protected String modifiedBefore = null;
    protected String modifiedAfter = null;
    protected char visible_to_public = '1';

    private Set<String> allowedSearchTerms = new HashSet<String>();
    public  Map<String, String> searchterms = new HashMap<String, String>();

    private int offset = 0;
    private int numberPerPage = -1; //-1 means get me everything
    protected String sortColumn;
    protected int sortOrder;
    private double random = 0.0;

    protected BigDecimal correctness = null;

    /* list containing the result set 
     is null if the search has not been performed yet */
    private List<Object> resultSet = null;
    private Iterator<Object> resultIterator = null;
    protected Long numberOfHits;

    /**
     * Initialize the source
     *
     * @param session an open hibernate session to use
     */
    public HibernateMetadataSource(org.hibernate.Session session) {
        this.session = session;
        initializeAllowedSearchTerms();
    }



    /**
     * There are some data that are known only from the configuration file
     *
     * @param config Global configuration property object
     */
    public void setConfiguration(java.util.Properties config) {
    }

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
    public void setEdition(String id) {
        try {
            this.edition = (Edition) session.load(Edition.class, id);
        } catch (HibernateException ex) {
            logger.error("setEdition(" + id + ") error:" + ex.getMessage());
            throw new NullPointerException("setEdition(" + id + ") error:" + ex.getMessage());
        }
    }

    public Edition getEdition() {
        return this.edition;
    }

    public void setCategory(String id) {
        logger.debug("setting category "+id);
        try {
            this.category = (Category) session.load(Category.class, id);
        } catch (HibernateException ex) {
            logger.error("setCategory(" + id + ") error:" + ex.getMessage());
            throw new NullPointerException("setCategory(" + id + ") error:" + ex.getMessage());
        }
    }

    public Category getCategory() {
        return this.category;
    }

    public void setType(String id) {
        try {
            this.type = (Type) session.load(Type.class, new java.math.BigDecimal(id));
        } catch (HibernateException ex) {
            logger.error("setType(" + id + ") error:" + ex.getMessage());
            throw new NullPointerException("setType(" + id + ") error:" + ex.getMessage());
        }
    }

    public Type getType() {
        return this.type;
    }

    /**
     * Selects data with geo position within the bounding box given in a
     * string of the form
     * <p/>
     * upper left x, upper left y, lower right x, lower right y
     * <p/>
     * The get method returns the bbo in the same format
     */
    public void setBoundingBox(String boundingBox) {
        this.boundingBox = boundingBox;
    }

    public String getBoundingBox() {
        return this.boundingBox;
    }

    /**
     * Various dates useful for search and/or sorting
     */
    public void setNotBefore(String date) {
        this.notBefore = date;
    }

    public String getNotBefore() {
        return this.notBefore;
    }

    public String getModifiedBefore() { return modifiedBefore;}

    public void setModifiedBefore(String modifiedBefore) { this.modifiedBefore = modifiedBefore;}

    public String getModifiedAfter() { return modifiedAfter;}

    public void setModifiedAfter(String modifiedAfter) {this.modifiedAfter = modifiedAfter;}

    public void setNotAfter(String date) {
        this.notAfter = date;
    }

    public String getNotAfter() {
        return this.notAfter;
    }

    @Override
    public String getModifiedBefore(String s) {
        return this.modifiedAfter;
    }

    @Override
    public String getModifiedAfter(String s) {
        return this.getModifiedBefore();
    }

    public char getVisible_to_public() {
        return visible_to_public;
    }

    public void setVisible_to_public(char visible_to_public) {
        this.visible_to_public = visible_to_public;
    }

    public void setSearchterms(String terms) {
        this.searchterms.put("mods", terms);
    }

    /**
     * for search in specific fields supported by the database. Eg., creator,
     * title, location, person etc
     */
    public void setSearchterms(String field, String terms) {

//	if (!allowedSearchTerms.contains(field)) {
//		logger.error("Search term '"+field+"' not defined ");
        //TODO: throw an exception ?
//	} else
        this.searchterms.put(field, terms);
    }

    public String getSearchterms() {
        return this.searchterms.toString();
        //TODO: make it look nicer
    }

    @Override
    public BigDecimal getCorrectness() {
        return this.correctness;
    }

    @Override
    public void setCorrectness(BigDecimal correctness) {
        this.correctness = correctness;
    }

    /**
     * @return Record sequence number requested
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return this.offset;
    }

    /**
     * @return Number of records per page
     */
    public void setNumberPerPage(int number) {
        this.numberPerPage = number;
    }

    public int getNumberPerPage() {
        return this.numberPerPage;
    }


    /**
     * @param fraction of hits to be returned
     */
    public void setRandom(double fraction) {
        this.random = fraction;
    }

    /**
     * @return the fraction of hits to be used
     */
    public double getRandom() {
        return this.random;
    }

    @Override
    public void setSortcolumn(String column) {
        this.sortColumn = column;
    }

    @Override
    public String getSortcolumn() {
        return sortColumn;
    }

    @Override
    public void setSortorder(int order) {
        this.sortOrder = order;
    }

    @Override
    public int getSortorder() {
        return sortOrder;
    }

    /**
     * Execute a search to produce a resultset
     * throws Exception if search fails
     */
    public void execute() {
        /* check if search has allready been executed */
        if (this.resultSet == null) {
            /* build query */
            try {
                Criteria criteria = session.createCriteria(Object.class);
                Criteria countCriteria = session.createCriteria(Object.class);


                Set<String> fields = searchterms.keySet();
                for (String field : fields) {
                    if (isAllowedSearchField(field)) {
                        // We do not have a text index on id, so use eq

                        if ("id".equals(field)) {
                            criteria.add(Restrictions.eq("id", searchterms.get(field)));
                            countCriteria.add(Restrictions.eq("id", searchterms.get(field)));
                        } else {

                            if ("mods".equals(field)) {
                                logger.debug("searching in tags");
/*                                String contains = String.format("(contains(%s,?)>0 or contains(%s,?)>0)", "mods","tag_value");
                                String[] values = {searchterms.get(field),searchterms.get(field)};
                                org.hibernate.type.Type[] types = {Hibernate.STRING,Hibernate.STRING};

                                criteria.createAlias("keywords","tags",CriteriaSpecification.LEFT_JOIN)
                                        .add(Restrictions.sqlRestriction(contains,values,types));
                                countCriteria.createAlias("keywords","tags",CriteriaSpecification.LEFT_JOIN)
                                        .add(Restrictions.sqlRestriction(contains,values,types));
*/

                                String tagContains = String.format("(contains(%s,?)>0)", "tag_value");
                                Criteria tagCrit =   session.createCriteria(Tag.class);
                                tagCrit.add(Restrictions.sqlRestriction(tagContains,searchterms.get(field),StringType.INSTANCE));
                                tagCrit.createAlias("objects","objs");
                                tagCrit.setProjection(Projections.projectionList().add(Projections.distinct(Projections.property("objs.id"))));

                                String contains = String.format("(contains(%s,?)>0)", "mods");
                                List<Tag> tagObjs = tagCrit.list();
                                Criterion criterion;

                                if (tagObjs.size() > 0)
                                    criterion = Restrictions.or(Restrictions.sqlRestriction(contains,searchterms.get(field),
                                                    StringType.INSTANCE),
								Restrictions.in("id",tagObjs));
                                else
                                    criterion = Restrictions.sqlRestriction(contains,searchterms.get(field), StringType.INSTANCE);
                                criteria.add(criterion);
                                countCriteria.add(criterion);
                            } else {
                                String contains = String.format("contains(%s,?)>0", field);
                                Criterion criterion = Restrictions.sqlRestriction(contains, searchterms.get(field), StringType.INSTANCE);
                                criteria.add(criterion);
                                countCriteria.add(criterion);
                            }
                        }
                    } else {
                        logger.debug(String.format("Illegal search term: %s", field));
                    }
                }
                if (this.boundingBox != null) {
                    criteria.add(Restrictions.sqlRestriction("sdo_filter(point,sdo_geometry(2003,null,null," +
                            "sdo_elem_info_array(1,1003,3)," +
                            "sdo_ordinate_array(" + this.boundingBox + ")))='TRUE'"));
                    countCriteria.add(Restrictions.sqlRestriction("sdo_filter(point,sdo_geometry(2003,null,null," +
                            "sdo_elem_info_array(1,1003,3)," +
                            "sdo_ordinate_array(" + this.boundingBox + ")))='TRUE'"));
                }
                //criteria.add(Restrictions.ge("randomNumber", new BigDecimal(this.random)));
                if (this.notBefore != null) {
                    criteria.add(Restrictions.ge("notBefore", sdf.parse(this.notBefore)));
                    countCriteria.add(Restrictions.ge("notBefore", sdf.parse(this.notBefore)));
                }
                if (this.notAfter != null) {
                    criteria.add(Restrictions.le("notAfter", sdf.parse(this.notAfter)));
                    countCriteria.add(Restrictions.le("notAfter", sdf.parse(this.notAfter)));
                }
                if (this.modifiedAfter != null) {
                    criteria.add(Restrictions.ge("lastModified",oaidateToTimestamp(this.modifiedAfter)));
                    countCriteria.add(Restrictions.ge("lastModified",oaidateToTimestamp(this.modifiedAfter)));
                }
                if (this.modifiedBefore != null) {
                    criteria.add(Restrictions.le("lastModified",oaidateToTimestamp(this.modifiedBefore)));
                    countCriteria.add(Restrictions.le("lastModified",oaidateToTimestamp(this.modifiedBefore)));
                }

                if (this.edition != null) {
                    criteria.add(Restrictions.eq("edition.id", this.edition.getId()));
                    countCriteria.add(Restrictions.eq("edition.id", this.edition.getId()));
                }
                if (this.type != null) {
                    criteria.add(Restrictions.eq("type.id", this.type.getId()));
                    countCriteria.add(Restrictions.eq("type.id", this.type.getId()));
                }
                if (this.correctness != null) {
                    criteria.add(Restrictions.eq("correctness", this.getCorrectness()));
                    countCriteria.add(Restrictions.eq("correctness",this.getCorrectness()));
                }

                // The second worst hack in history of modern Computing.
                // DGJ and ABWE is the masterminds behinds this.
                // IF a WIDE search is being conducted, do not include copjects from an edition where visible to public is equal to 0.
                // The hack is extended to check if we are searching for a specific object, then we also dont care about visibility
                if ( (this.edition == null || this.edition.getId() == null) &&
                        !this.searchterms.containsKey("id")) {

                    criteria = criteria.createAlias("edition", "edit").add(Restrictions.eq("edit.visiblePublic", this.visible_to_public));
                    countCriteria = countCriteria.createAlias("edition", "edit").add(Restrictions.eq("edit.visiblePublic", this.visible_to_public));

                    //criteria.add(Restrictions.eq("edition.visiblePublic", this.visible_to_public));
                    //countCriteria.add(Restrictions.eq("edition.visiblePublic", this.visible_to_public));
                }
                if (this.category != null) {
                    criteria = criteria.createAlias("categories", "cats")
                            .add(Restrictions.eq("cats.id", this.category.getId()));
                    countCriteria = countCriteria.createAlias("categories", "cats")
                            .add(Restrictions.eq("cats.id", this.category.getId()));
                }

                // Get total number of hits
                ProjectionList pl = Projections.projectionList();
   //             pl.add(Projections.countDistinct("id"));
                pl.add(Projections.count("id"));
                countCriteria.setProjection(pl);
              //  countCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

                this.numberOfHits = new Long( countCriteria.list().get(0).toString() );

                // remove count projection
                // criteria.setProjection(null);

                // add page and numberPerPage
                if (this.numberPerPage > -1)
                    criteria.setMaxResults(this.numberPerPage);

                if ((this.offset - 1) > 0) // hibernate counts like this 0,1,2,3,4
                    criteria.setFirstResult(this.offset - 1);

                // add default order by random
                logger.debug("order by :"+sortColumn);
                if (sortColumn != null && !"".equals(sortColumn)) {
                    if (sortOrder < 0)
                        criteria.addOrder(Order.desc(sortColumn));
                    else
                        criteria.addOrder(Order.asc(sortColumn));
                } else  {
                    criteria.addOrder(Order.desc("interestingess"));
                    criteria.addOrder(Order.desc("randomNumber"));
                }
              //  criteria.setProjection(Projections.projectionList()
              //          .add(Projections.property("id"))
              //          .add(Projections.property("randomNumber")));
                criteria.setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE);

                this.resultSet = criteria.list();
                this.resultIterator = this.resultSet.iterator();
            } catch (ParseException ex) {
                logger.error("Search failed (format for notBefore and notAfter should be yyyy-MM-dd)", ex);
                throw new RuntimeException("Hibernate metadata search failed (format for notBefore and notAfter should be yyyy-MM-dd). " + ex.getMessage());
            } catch (HibernateException ex) {
                logger.error("Search failed: " + ex.getMessage(), ex);
                throw new RuntimeException("Hibernate metadata search failed: " + ex.getMessage());
            }
        }

    }

    protected boolean isAllowedSearchField(String field) {
        return allowedSearchTerms.contains(field);
    }

    protected long oaidateToTimestamp(String date) throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(date).getTime();

    }

    /**
     * Returns the number of hits for a given MetadataSource
     *
     * @return the total number of hits in the result set
     */
    public java.lang.Long getNumberOfHits() {
        if (this.resultSet == null) {
            logger.error("Unable to get number of hits: search has not been executed");
            throw new NullPointerException("Unable to get number of hits: search has not been executed");
        }
        return numberOfHits;
    }


    /**
     * Determines whether there is more data to retrieve
     *
     * @return true if there is at least one more metadata object to be retrieved
     */
    public boolean hasMore() {
        if (this.resultSet == null) {
            logger.error("hasmore error: search has not been executed");
            throw new NullPointerException("hasmore error: search has not been executed");
        }
        return this.resultIterator.hasNext();
    }

    /**
     * Retrieve another record
     *
     * @return another XML object as a string
     */
    public Object getAnother() {
        if (this.resultSet == null) {
            logger.error("Unable to get next object: search has not been executed");
            throw new NullPointerException("Unable to get next object: search has not been executed");
        }
        return this.resultIterator.next();
    }


    /**
     * Initilize the set of allowed searchterms
     * currently just a hardcoded set of searchterm
     * TODO: build this from database (hibernate) and/or configuration-file
     */
    private void initializeAllowedSearchTerms() {
        this.allowedSearchTerms.add("mods");
        this.allowedSearchTerms.add("id");
        this.allowedSearchTerms.add("building");
        this.allowedSearchTerms.add("creator");
        this.allowedSearchTerms.add("location");
        this.allowedSearchTerms.add("person");
        this.allowedSearchTerms.add("title");
        this.allowedSearchTerms.add("sted");
    }


}


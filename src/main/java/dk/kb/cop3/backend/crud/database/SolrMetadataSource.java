package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.crud.database.hibernate.Category;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;


import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.crud.database.hibernate.Type;
import org.apache.log4j.Logger;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.*;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrDocument;
import org.hibernate.query.Query;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Implementation of MetadataSource interface, that uses both Solr
 * server and a hibernate session to perform searches and retrieve
 * data, respectively
 *
 */
public class SolrMetadataSource implements MetadataSource {

    private static final Logger logger = Logger.getLogger(SolrMetadataSource.class);

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

    private Set<String> allowedSearchTerms = new HashSet<>();
    public  Map<String, String> searchterms = new HashMap<>();

    private int offset = 0;
    private int numberPerPage = -1; //-1 means get me everything
    protected String sortcolumn;
    protected int sortorder;
    private double random = 0.0;

    protected BigDecimal correctness = null;

    long numberOfHits;

    private List<Object> hibernateResultSet = null;
    private Iterator<Object> hibernateResultIterator = null;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public void setCategory(String id) {
        logger.debug("setting category "+id);
        try {
            this.category = session.load(Category.class, id);
        } catch (HibernateException ex) {
            logger.error("setCategory(" + id + ") error:" + ex.getMessage());
            throw new NullPointerException("setCategory(" + id + ") error:" + ex.getMessage());
        }
    }

    @Override
    public Category getCategory() {
        return this.category;
    }

    @Override
    public Edition getEdition() {
        return edition;
    }

    @Override
    public void setEdition(String id) {
        try {
            this.edition = session.load(Edition.class, id);
        } catch (HibernateException ex) {
            logger.error("setEdition(" + id + ") error:" + ex.getMessage());
            throw new NullPointerException("setEdition(" + id + ") error:" + ex.getMessage());
        }
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(String id) {
        try {
            this.type = session.load(Type.class, new java.math.BigDecimal(id));
        } catch (HibernateException ex) {
            logger.error("setType(" + id + ") error:" + ex.getMessage());
            throw new NullPointerException("setType(" + id + ") error:" + ex.getMessage());
        }
    }

    @Override
    public String getBoundingBox() {
        return boundingBox;
    }

    @Override
    public void setBoundingBox(String boundingBox) {
        this.boundingBox = boundingBox;
    }

    @Override
    public String getNotBefore() {
        return notBefore;
    }

    @Override
    public void setNotBefore(String notBefore) {
        this.notBefore = notBefore;
    }

    @Override
    public String getNotAfter() {
        return notAfter;
    }

    @Override
    public void setNotAfter(String notAfter) {
        this.notAfter = notAfter;
    }

    public String getModifiedBefore() {
        return modifiedBefore;
    }

    @Override
    public void setModifiedBefore(String modifiedBefore) {
        this.modifiedBefore = modifiedBefore;
    }

    public String getModifiedAfter() {
        return modifiedAfter;
    }

    @Override
    public void setModifiedAfter(String modifiedAfter) {
        this.modifiedAfter = modifiedAfter;
    }

    public char getVisible_to_public() {
        return visible_to_public;
    }

    public void setVisible_to_public(char visible_to_public) {
        this.visible_to_public = visible_to_public;
    }

    public Set<String> getAllowedSearchTerms() {
        return allowedSearchTerms;
    }

    public void setAllowedSearchTerms(Set<String> allowedSearchTerms) {
        this.allowedSearchTerms = allowedSearchTerms;
    }


    public void setSearchterms(Map<String, String> searchterms) {
        this.searchterms = searchterms;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public int getNumberPerPage() {
        return numberPerPage;
    }

    @Override
    public void setNumberPerPage(int numberPerPage) {
        this.numberPerPage = numberPerPage;
    }

    @Override
    public String getSortcolumn() {
        return sortcolumn;
    }

    @Override
    public void setSortcolumn(String sortcolumn) {
        this.sortcolumn = sortcolumn;
    }

    @Override
    public int getSortorder() {
        return sortorder;
    }

    @Override
    public void setSortorder(int sortorder) {
        this.sortorder = sortorder;
    }

    @Override
    public double getRandom() {
        return random;
    }

    @Override
    public void setRandom(double random) {
        this.random = random;
    }

    @Override
    public BigDecimal getCorrectness() {
        return correctness;
    }

    @Override
    public void setCorrectness(BigDecimal correctness) {
        this.correctness = correctness;
    }

    SolrDocumentList solrResults = null;
    java.util.ListIterator<SolrDocument> solrResultIterator = null;


    /**
     * Initialize the source
     *
     * @param session an open hibernate session to use
     */
    public SolrMetadataSource(org.hibernate.Session session) {
        this.session = session;
    }


    public Long getNumberOfHits() {
        if (getSingleObject()) return (long) hibernateResultSet.size();
        return solrResults.getNumFound();
    }

    /**
     * Determines whether there is more data to retrieve
     *
     * @return true if there is at least one more metadata object to be retrieved
     */
    public boolean hasMore() {
        if (getSingleObject()) return hibernateResultIterator.hasNext();

        if (this.solrResults == null) {
            logger.error("hasmore error: search has not been executed");
            throw new NullPointerException("hasmore error: search has not been executed");
        }
        return this.solrResultIterator.hasNext();
    }

    /**
     * Retrieve another record
     *
     * @return another XML object as a string
     */
    public Object getAnother() {

        if (getSingleObject()) {
            if(hibernateResultIterator.hasNext()) {
                return hibernateResultIterator.next();
            }
            return null;
        }

        if (this.solrResults == null) {
            logger.error("Unable to get next object: search has not been executed");
            throw new NullPointerException("Unable to get next object: search has not been executed");
        }

        SolrDocument doc = this.solrResultIterator.next();
	    Object copject = new Object();

        copject.setId((String) doc.getFieldValue("id"));
        copject.setMods((String) doc.getFieldValue("mods_ts"));

	if(doc.getFieldValue("dcterms_spatial") != null) {
	    String latlng = (String)doc.getFieldValue("dcterms_spatial");

	    String lat = latlng.split(",")[0];
	    String lon = latlng.split(",")[1];

        GeometryFactory geoFactory = JTSFactoryFinder.getGeometryFactory();
        copject.setPoint(geoFactory.createPoint(new Coordinate(Double.parseDouble(lat),Double.parseDouble(lon))));
	} else {
        GeometryFactory geoFactory = JTSFactoryFinder.getGeometryFactory();
        copject.setPoint(geoFactory.createPoint(new Coordinate(0,0)));
	}

	java.math.BigDecimal correctness = new java.math.BigDecimal("-1");
	if(doc.getFieldValue("cobject_correctness_isi") != null) {
	    correctness = new java.math.BigDecimal((Integer)doc.getFieldValue("cobject_correctness_isi"));
	}

	copject.setCorrectness(correctness);

	if(doc.getFieldValue("cobject_last_modified_lsi") != null) {
	    copject.setLastModified("" + doc.getFieldValue("cobject_last_modified_lsi") );
	} else {
	    copject.setLastModified("1234567890");
            logger.error("we have to fake lastModified, at least for " +  doc.getFieldValue("id") );
	}
        return copject;
    }


    public void execute() {
	if (getSingleObject()) {
	        this.getSingleObjectFromDB();
	    } else {
	        this.solrSearch();
	    }
    }

    private void getSingleObjectFromDB() {
        Query query = session.createQuery("from dk.kb.cop3.backend.crud.database.hibernate.Object where id ='"+this.searchterms.get("id")+"'");
        this.hibernateResultSet = query.list();
        this.hibernateResultIterator = this.hibernateResultSet.iterator();
        this.numberOfHits = hibernateResultSet.size();
    }


    private void solrSearch() {
        try {
            String solr_url = CopBackendProperties.getCopBackendUrl();
            logger.debug("Solr url "+solr_url);
            HttpSolrServer solr = new HttpSolrServer(solr_url);

            SolrQuery query = new SolrQuery();

            Set<String> fields = searchterms.keySet();
            String solr_q = "";
            for (String field : fields) {
                logger.debug("search field '"+field+"' value '"+searchterms.get(field)+"'");
                if (isAllowedSearchField(field)) {
                    /*
                        if searchfield is "mods" or "query", search in all fields
                     */
                    if ("mods".equals(field)) {
                        solr_q += "{!edismax qf=\"title_tdsim author_tsim creator_tsim coverage_tdsim subject_tdsim description_tsim citySection_street_tsim citySection_housenumber_tsim citySection_zipcode_tsim local_id_fngsi\"}"+searchterms.get(field);
                    }
                    /*
                        special searchfield "sted" for searching in boeth vejnavn and
                     */
                    if ("sted".equals(field)) {
                        if (!"".equals(solr_q)) solr_q += " AND ";
                        solr_q += "{!edismax qf=\"area_area_tsim citySection_street_tsim cobject_location_tsim\"}"+searchterms.get(field);
                    }

                    /* search in specific fields, current allowed fields are
                            building, creator, location, person, title
                     */
                    if ("title".equals(field)) {
                        if (!"".equals(solr_q)) solr_q += " AND ";
                        solr_q += "full_title_tsim:"+searchterms.get(field);
                    }

                    if ("creator".equals(field)) {
                        if (!"".equals(solr_q)) solr_q += " AND ";
                        solr_q += "creator_tsim:"+searchterms.get(field);
                    }

                    if ("author".equals(field)) {
                        if (!"".equals(solr_q)) solr_q += " AND ";
                        solr_q += "author_tsim:"+searchterms.get(field);
                    }

                    if ("person".equals(field)) {
                        if (!"".equals(solr_q)) solr_q += " AND ";
                        solr_q += "cobject_person_tsim:"+searchterms.get(field);
                    }

                    if ("location".equals(field)) {
                        if (!"".equals(solr_q)) solr_q += " AND ";
                        solr_q += "cobject_location_tsim:"+searchterms.get(field);
                    }

                    if ("building".equals(field)) {
                        if (!"".equals(solr_q)) solr_q += " AND ";
                        solr_q += "area_building_tsim:" + searchterms.get(field);
                    }

                    /* TODO the same for the rest of the fields (or something smarter) */
                }
            }
            if (this.boundingBox != null) {
                logger.debug("got a bounding box "+this.boundingBox);
                // Solr expects lower left TO upper right and not upper right to lower left corner
                // as we do in oracle spatial
                String[] tokens = this.boundingBox.split(",");

		//String solr_bb = "["+tokens[2]+","+tokens[3]+" TO "+tokens[0]+","+ tokens[1]+"]";
		String solr_bb =   "["+tokens[3]+","+tokens[2]+" TO "+tokens[1]+","+ tokens[0]+"]";
                logger.debug("solr bounding box dcterms_spatial:"+solr_bb);

                query.addFilterQuery("dcterms_spatial:"+solr_bb);

            }
            //criteria.add(Restrictions.ge("randomNumber", new BigDecimal(this.random)));
            if (this.notBefore != null) {
                String solrDate = getSolrDate(this.notBefore);
                // if getSolrDate returns null the notBefore query param is not valid and thus ignored
                if (solrDate != null) {
                    query.addFilterQuery("cobject_not_after_dtsi:["+solrDate+" TO *]");
                }
            }
            if (this.notAfter != null) {
                String solrDate = getSolrDate(this.notAfter);
                // if getSolrDate returns null the notBefore query param is not valid and thus ignored
                if (solrDate != null) {
                    query.addFilterQuery("cobject_not_before_dtsi:[* TO "+solrDate+"]");
                }
            }
            if (this.edition != null) {
                logger.debug("setting edition "+this.edition.getId());
                query.addFilterQuery("cobject_edition_ssi:\""+this.edition.getId()+"\"");
            }
            if (this.type != null) {
                logger.debug("adding type "+this.type.getTypeText());
                query.addFilterQuery("luftfo_type_ssim:"+this.type.getTypeText());
            }
            if (this.correctness != null) {
                logger.debug("adding correctnes "+ this.correctness);
                query.addFilterQuery("cobject_correctness_isi:\""+this.correctness.toString()+"\"");
            }
            if (this.category != null) {
                String categoryId = this.category.getId();
                /* Here is another hack, perhaps not in the top 10 of worst hacks in the
                 * history of modern computing, but close
                 *
                 * We do need to remove language from category id before searching in solr
                 */
                categoryId = categoryId.replaceAll("/da/","").replaceAll("/en/","");
                logger.debug("adding category "+categoryId);
                query.addFilterQuery("subject_topic_id_ssim:\""+categoryId+"\"");
            }
            if (this.modifiedAfter != null || this.modifiedBefore != null) {
                String filter = (this.modifiedAfter != null) ?  ""+this.modifiedAfter : "*";
                filter += " TO ";
                filter += (this.modifiedBefore != null) ? this.modifiedBefore : "*";
                query.addFilterQuery("cobject_last_modified_lsi:"+filter);
            }

            // The second worst hack in history of modern Computing.
            // DGJ and ABWE is the masterminds behinds this.
            // IF a WIDE search is being conducted, do not include copjects from an edition where visible to public is equal to 0.
            // The hack is extended to check if we are searching for a specific object, then we also dont care about visibility
            if ( (this.edition == null || this.edition.getId() == null) &&
                    !this.searchterms.containsKey("id")) {

                List<Edition> editions = this.session.createCriteria(Edition.class).add(Restrictions.eq("visiblePublic",'0')).list();
                for(Edition ed : editions) {
                    logger.debug("Excluding edition "+ed.getId());
                    query.addFilterQuery("!cobject_edition_ssi:\""+ed.getId()+"\"");
                }
            }

            /*
                Translate Hibernate columnnames into solr_fields for sorting
            */
            if (this.sortcolumn !=null && !"".equals(this.sortcolumn)) {
                logger.debug("sort column is "+this.sortcolumn);
                String sort_field = "cobject_interestingness_isi";
                switch(this.sortcolumn) {
                    case "correctness":
                        sort_field = "cobject_correctness_isi";
                        break;
                    case "interestingness":
                        sort_field = "cobject_interestingness_isi";
                        break;
                    case "notBefore":
                        sort_field = "cobject_not_before_dtsi";
                        break;
                    case "notAfter":
                        sort_field = "cobject_not_after_dtsi";
                        break;
                    case "title":
                        sort_field = "cobject_title_ssi";
                        break;
                    case "filename":
                        sort_field = "local_id_ssi";
                        break;
                }

                if (this.sortorder < 0) {
                    query.addSort(sort_field, SolrQuery.ORDER.desc);
                } else {
                    query.addSort(sort_field, SolrQuery.ORDER.asc);
                }
            } else {
                query.addSort("cobject_interestingness_isi",SolrQuery.ORDER.desc );
            }

            int start  =  this.getOffset();
            int number =  this.getNumberPerPage();
            query.setStart(Math.max(start - 1, 0));
            query.setRows(number);
            query.set("mm",0);
	        logger.debug("solr_q" + solr_q);
            query.setQuery(solr_q);
            logger.debug(query.toString());
            QueryResponse solrResponse = solr.query(query);
            this.solrResults = solrResponse.getResults();
            logger.debug("Found number of records "+this.solrResults.getNumFound());
            this.numberOfHits = this.solrResults.getNumFound();
            this.solrResultIterator = solrResults.listIterator();

        } catch(SolrServerException serverProblem) {
            logger.error(serverProblem.getMessage(),serverProblem);
        }
    }

    private String getSolrDate(String date) {
        try {
            SimpleDateFormat solrDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date parsedDate = sdf.parse(date);
            return solrDateFormat.format(parsedDate);
        } catch (ParseException e) {
            logger.warn("invalid date format "+date);
            return null;
        }
    }

    protected boolean isAllowedSearchField(String field) {
        return allowedSearchTerms.contains(field);
    }
    /*
    Returns true if the request is for a single object
     */
    private boolean getSingleObject() {
        return searchterms.containsKey("id");
    }

    public String getModifiedBefore(String s) {
        return this.modifiedAfter;
    }

    @Override
    public String getModifiedAfter(String s) {
        return this.getModifiedBefore();
    }

    public String getSearchterms() {
        return this.searchterms.toString();
    }

    @Override
    public void setConfiguration(java.util.Properties config) {
    }

    @Override
    public void setSearchterms(String terms) {
        this.searchterms.put("mods", terms);
    }

    @Override
    public void setSearchterms(String field, String terms) {
        this.searchterms.put(field, terms);
    }

}

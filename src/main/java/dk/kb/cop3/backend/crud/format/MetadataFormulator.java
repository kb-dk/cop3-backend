package dk.kb.cop3.backend.crud.format;

import dk.kb.cop3.backend.constants.ConfigurableConstants;
import dk.kb.cop3.backend.crud.database.MetadataSource;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.database.hibernate.Tag;
import dk.kb.cop3.backend.crud.database.hibernate.Comment;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Geometry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Iterator;

/**
 * Reads a MetadataSource returning mods data, creating a modsCollection document
 *
 * @author Sigfrid Lundberg (slu@kb.dk)
 * @version $Revision$, last modified $Date$ by $Author$
 *          $Id$
 */

public abstract class MetadataFormulator {

    Logger log = Logger.getLogger(MetadataFormulator.class);

    javax.xml.transform.TransformerFactory trans_fact
            = new org.apache.xalan.processor.TransformerFactoryImpl();

    private java.lang.String xslt_file = "conf/build_mods.xsl";
    private java.lang.String mods_collection_template = "conf/template_mods_collection.xml";

    private javax.xml.parsers.DocumentBuilderFactory dfactory =
            javax.xml.parsers.DocumentBuilderFactory.newInstance();


    private java.io.OutputStream stream = null;
    private MetadataSource data_source = null;

    private Edition edition = null;
    private javax.servlet.http.HttpServletRequest request = null;
    private javax.servlet.ServletContext context = null;
    private java.lang.String format = "mods";

    private String lastModifiedTimeStamp = "";
    private java.lang.String language = "da";
    private ConfigurableConstants consts = ConfigurableConstants.getInstance();
    protected String currentRawMods;

    org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(ModsMetadataFormulator.class.getPackage().getName());

    /**
     * An empty constructor
     */
    public MetadataFormulator() {

    }

    public void setOutPutStream(java.io.OutputStream stream) {
        this.stream = stream;
    }

    public void setDataSource(MetadataSource source) {
        this.data_source = source;
    }

    public void setEditions(java.util.HashMap<java.lang.String, Edition> editions) {
    }

    public void setEdition(Edition edition) {
        this.edition = edition;
    }

    public void setRequest(javax.servlet.http.HttpServletRequest request) {
        this.request = request;
    }

    public javax.servlet.http.HttpServletRequest getRequest() {
        return this.request;
    }

    public String getLastModifiedTimeStamp() {
        return lastModifiedTimeStamp;
    }

    public void setLastModifiedTimeStamp(String lastModifiedTimeStamp) {
        this.lastModifiedTimeStamp = lastModifiedTimeStamp;
    }

    public void setServletContext(javax.servlet.ServletContext context) {
        this.context = context;
    }

    public String serialize(org.w3c.dom.Document doc) {
        java.lang.String str = "";

        try {
            org.w3c.dom.bootstrap.DOMImplementationRegistry registry =
                    org.w3c.dom.bootstrap.DOMImplementationRegistry.newInstance();

            org.w3c.dom.ls.DOMImplementationLS impl =
                    (org.w3c.dom.ls.DOMImplementationLS) registry.getDOMImplementation("LS");

            org.w3c.dom.ls.LSSerializer writer = impl.createLSSerializer();
            str = writer.writeToString(doc);

        } catch (java.lang.ClassNotFoundException classNotFound) {
            logger.error(classNotFound.getMessage());
        } catch (java.lang.InstantiationException instantiationPrblm) {
            logger.error(instantiationPrblm.getMessage());
        } catch (java.lang.IllegalAccessException accessPrblm) {
            logger.error(accessPrblm.getMessage());
        }
        return str;
    }

    public void setLanguage(java.lang.String language) {
        this.language = language;
    }

    public java.lang.String getLanguage() {
        return this.language;
    }

    /**
     * @param destination
     * @param insertElement
     * @param start         StartIndex: the index of the first search result desired by the search client
     * @param itemsPerPage
     * @param totalHits
     * @param editionId
     * @param searchTerms
     * @param objectID
     * @param type
     */
    private void insertOpenSearchStuff(Document destination,
                                       Element insertElement,
                                       String start,
                                       String itemsPerPage,
                                       String totalHits,
                                       String editionId,
                                       String searchTerms,
                                       String objectID,
                                       String type) {

        /* <opensearch:startIndex>1</opensearch:startIndex> */
        // http://www.opensearch.org/Specifications/OpenSearch/1.1#The_.22startIndex.22_parameter
        // The "startIndex" parameter
        //Replaced with the index of the first search result desired by the search client.
        org.w3c.dom.Element element = destination.createElementNS("http://a9.com/-/spec/opensearch/1.1/",
                "startIndex");

        org.w3c.dom.Node text = destination.createTextNode(start + "");
        element.appendChild(text);
        insertElement.appendChild(element);

        /*    <opensearch:itemsPerPage>40</opensearch:itemsPerPage> */
        element = destination.createElementNS("http://a9.com/-/spec/opensearch/1.1/",
                "itemsPerPage");
        text = destination.createTextNode(itemsPerPage + "");
        element.appendChild(text);
        insertElement.appendChild(element);

        /* <opensearch:Query role="request" startPage="1" searchTerms=""/> */
        //
        // Needed for general open search clients out there!!!!
        // here should be some hopefully correct info
        //

        element = destination.createElementNS("http://a9.com/-/spec/opensearch/1.1/",
                "Query");
        element.setAttribute("role", "request");
        element.setAttribute("startPage", "" + start);
        element.setAttribute("searchTerms", searchTerms);
        insertElement.appendChild(element);

        element = destination.createElementNS("http://a9.com/-/spec/opensearch/1.1/", "totalResults");
        text = destination.createTextNode(totalHits + "");
        element.appendChild(text);
        insertElement.appendChild(element);
        element = destination.createElementNS("http://www.w3.org/2005/Atom", "link");

        String copBaseUrl = this.consts.getConstants().getProperty("cop2_backend.baseurl");
        String baseUrl = copBaseUrl + "/syndication";

        element.setAttribute("rel", "search");
        element.setAttribute("type", "application/opensearchdescription+xml");
        element.setAttribute("href", copBaseUrl + editionId);

        insertElement.appendChild(element);

        /* <atom:link
         rel="search"
         rel="self" type="application/atom+xml"
         href="http://me_uri_here"/> */
        // TODO ABW: this is a duplicate element! Lets clean that up as soon as possible.

        element = destination.createElementNS("http://www.w3.org/2005/Atom",
                "link");
        element.setAttribute("rel", "search");
        element.setAttribute("type", type);
        element.setAttribute("href", baseUrl + editionId + "/" + objectID);
        insertElement.appendChild(element);
    }

    public javax.ws.rs.core.MediaType mediaType() {
        return javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE;
    }

    public org.w3c.dom.Document formulate() {
        return this.formulate(this.format,
                this.mods_collection_template,
                this.xslt_file);
    }

    public org.w3c.dom.Document formulate(java.lang.String format,
                                          java.lang.String template,
                                          java.lang.String xsl) {

        if (data_source == null) {
            throw new NullPointerException("No data source available");
        }

        String baseUrl = this.consts.getConstants().getProperty("cop2_backend.baseurl");

        javax.xml.transform.Transformer transformer = null;

        try {
            //transformer = trans_fact.newTransformer(new javax.xml.transform.stream.StreamSource(xsl));
            logger.debug("xsl path is :" + this.getClass().getResourceAsStream(xsl));

            // xsltSystemId = new File(systemID).toURL().toExternalForm( );
            // xsltStreamSource.setSystemId(xsltFile.toURI().toString());
            javax.xml.transform.stream.StreamSource ss =
                    new javax.xml.transform.stream.StreamSource(this.getClass().getResourceAsStream(xsl));

            transformer = trans_fact.newTransformer(ss);
            logger.debug("Transformer fra xsl " + xsl + " transformer.toString() " + transformer.toString());
        } catch (javax.xml.transform.TransformerConfigurationException transformerPrblm) {
            logger.warn("problem might be: " + transformerPrblm.getMessage());
            transformerPrblm.printStackTrace();
        }

        // Ooops these are hard-coded. If you find them here after the project, something is wrong.
        // Or we haven't found a better way,

        String editionId = "/editions/any/2009/jul/editions";

        Edition edition = data_source.getEdition();
        if (edition != null) {
            editionId = edition.getId();
        }

        int itemsPerPage = data_source.getNumberPerPage();

        org.w3c.dom.Document resultSet = null;
        javax.xml.parsers.DocumentBuilder dBuilder = null;

        try {
            dfactory.setNamespaceAware(true);
            dBuilder = dfactory.newDocumentBuilder();
            InputStream in = this.getClass().getResourceAsStream(template);
            resultSet = dBuilder.parse(in);
        } catch (javax.xml.parsers.ParserConfigurationException parserPrblm) {
            logger.error(parserPrblm.getMessage());
        } catch (org.xml.sax.SAXException xmlPrblm) {
            logger.error(xmlPrblm.getMessage());
        } catch (java.io.IOException ioPrblm) {
            logger.error(ioPrblm.getMessage());
        }

        java.lang.Long hits = this.data_source.getNumberOfHits();
        if (logger.isDebugEnabled()) {
            logger.debug(".. found in total " + hits);
        }
        if (hits >= 0) {

            org.w3c.dom.Element root = resultSet.getDocumentElement();
            org.w3c.dom.Element insert_here = insertElementAt(resultSet, root);
            java.lang.String opensearch_relevant = "xxx mods atom rss kml";

            if (opensearch_relevant.indexOf(format) > 0) {
                String type = "application/atom+xml";

                String searchTerms = "";
                if (this.data_source.getSearchterms() != null) {
                    searchTerms = this.data_source.getSearchterms();

                }
                int offset = data_source.getOffset();
                if (offset <= 0) {
                    offset = 1;
                }
                this.insertOpenSearchStuff(resultSet,
                        insert_here,
                        offset + "", // ((data_source.getOffset() / itemsPerPage) +1) + "",
                        data_source.getNumberPerPage() + "",
                        hits + "",
                        editionId,
                        searchTerms + "",
                        "",
                        "application/" + format + "+xml");
            }

            int hit_number = 0;
            while (this.data_source.hasMore()) {
                hit_number++;
                String latitude = "";
                String longitude = "";
                String correctness = "";
                String interestingness = "";

                String keywordsModsSubjectTopic = "";
                org.w3c.dom.Document keywords = null;
                String comments = null;


                javax.xml.transform.dom.DOMSource source_dom = null;
                java.lang.String recordId = "";

                Object cobject = null;

                try {
                    cobject = this.data_source.getAnother();

                    // Generating tags in ModsXML
                    if (hit_number <= 1) {
                        Iterator iter = cobject.getKeywords().iterator();
                        keywordsModsSubjectTopic = "<subject xmlns='urn:x'>";
                        log.debug("getting tags");
                        while (iter.hasNext()) {
                            Tag tag = (Tag) iter.next();
                            keywordsModsSubjectTopic += "<topic>" + tag.getTag_value() + "</topic>";
                        }
                        keywordsModsSubjectTopic += "</subject>";
                        java.io.Reader keywordReader = new java.io.StringReader(keywordsModsSubjectTopic);
                        keywords = dBuilder.parse(new org.xml.sax.InputSource(keywordReader));

                        iter = cobject.getComments().iterator();
                        log.debug("getting comments");
                        while (iter.hasNext()) {
                            if (comments != null)
                                comments += "|";
                            else
                                comments = "";
                            comments += ((Comment) iter.next()).getText();
                        }
                    }

                    currentRawMods = cobject.getMods();
                    this.lastModifiedTimeStamp = cobject.getLastModified();
                    Geometry point = cobject.getPoint();
                    if (point != null) {
                        latitude = "" + point.getLat();
                        longitude = "" + point.getLng();
                    }
                    //logger.debug("lat="+latitude+" lng="+longitude);
                    recordId = cobject.getId();
                    if (cobject.getCorrectness() != null) {
                        correctness = cobject.getCorrectness().toString();
                    } else {
                        correctness = "";
                    }

                    if (cobject.getInterestingess() != null) {
                        interestingness = cobject.getInterestingess().toString();
                    } else {
                        interestingness = "";
                    }

                    java.io.Reader reader = new java.io.StringReader(currentRawMods);
                    org.w3c.dom.Document source_mods =
                            dBuilder.parse(new org.xml.sax.InputSource(reader));
                    source_dom = new javax.xml.transform.dom.DOMSource(source_mods);
                } catch (org.xml.sax.SAXException flawedMods) {
                    logger.error(flawedMods.getMessage());
                } catch (java.io.IOException ioPrblms) {
                    logger.error(ioPrblms.getMessage());
                }

                javax.xml.transform.dom.DOMResult result = new javax.xml.transform.dom.DOMResult(insert_here);

                try {

		    if(cobject != null) {
			transformer.setParameter("cobject_id",cobject.getId());
			if(cobject.getTitle() != null) transformer.setParameter("cobject_title",cobject.getTitle());
			if(cobject.getEdition() != null) {
                transformer.setParameter("cobject_edition",cobject.getEdition().getId());
                transformer.setParameter("cumulus_catalog",cobject.getEdition().getCumulusCatalog());
            }
			if(cobject.getBookmark() != null) transformer.setParameter("cobject_bookmark",cobject.getBookmark().toString());
			if(cobject.getRandomNumber() != null ) {
			    transformer.setParameter("cobject_random_number",cobject.getRandomNumber().toString());
			}
			if(cobject.getLikes() != null) transformer.setParameter("cobject_likes",cobject.getLikes().toString());
			if(cobject.getNotBefore() != null) transformer.setParameter("cobject_not_before",cobject.getNotBefore().toString());
			if(cobject.getNotAfter() != null ) transformer.setParameter("cobject_not_after",cobject.getNotAfter().toString());
			if(cobject.getLastModified() != null) transformer.setParameter("cobject_last_modified",cobject.getLastModified());
            if(cobject.getLastModifiedBy() != null) {
                if (cobject.getLastModifiedBy().startsWith("cumulus") || cobject.getLastModifiedBy().startsWith("cop+pdf+creator+client"))
                  transformer.setParameter("cobject_last_modified_by", "cumulus");
                else
                  transformer.setParameter("cobject_last_modified_by", "crowd");
            }
            if ((cobject.getObjVersion() != null) && (cobject.getObjVersion().compareTo(new BigDecimal(1)) > 0)
                    && cobject.getEdition().getId().contains("luftfoto")) {
                transformer.setParameter("ccs_ready","true");
            }
            if(cobject.getBuilding() != null) transformer.setParameter("cobject_building",cobject.getBuilding());
			if(cobject.getLocation() != null) transformer.setParameter("cobject_location",cobject.getLocation());
			if(cobject.getPerson() != null) transformer.setParameter("cobject_person",cobject.getPerson());
            if(comments != null) transformer.setParameter("comments",comments);
		    }


                    transformer.setParameter("latitude", latitude);
                    transformer.setParameter("longitude", longitude);
                    transformer.setParameter("correctness", correctness);
                    transformer.setParameter("interestingness",interestingness);
                    transformer.setParameter("metadata_context", baseUrl);
                    transformer.setParameter("content_context", this.consts.getConstants().getProperty("gui.uri"));
                    transformer.setParameter("record_id", recordId);
                    transformer.setParameter("language", this.getLanguage());
                    if(keywords!=null){
                        transformer.setParameter("keywords", keywords );
                    }
                    transformer.transform(source_dom, result);
                } catch (javax.xml.transform.TransformerException trnsFrmPrblm) {
                    logger.error(trnsFrmPrblm.getMessage());
                }
            }
        }
        return resultSet;
    }

    protected Element insertElementAt(Document resultSet, Element root) {
        return root;
    }


}

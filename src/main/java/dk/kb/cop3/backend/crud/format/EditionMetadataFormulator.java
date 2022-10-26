package dk.kb.cop3.backend.crud.format;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.crud.database.HibernateEditionTool;
import dk.kb.cop3.backend.crud.database.MetadataSource;
import dk.kb.cop3.backend.crud.database.SolrMetadataSource;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Reads a MetadataSource returning a hibernate bean and creates a suitable xml object
 *
 * @author Sigfrid Lundberg (slu@kb.dk)
 * @version $Revision: 1090 $, last modified $Date: 2011-04-26 15:56:35 +0200 (Tue, 26 Apr 2011) $ by $Author: slu $
 * $Id: ModsMetadataFormulator.java 1090 2011-04-26 13:56:35Z slu $
 */

public class EditionMetadataFormulator extends MetadataFormulator {


    private CopBackendProperties constants = CopBackendProperties.getInstance();

    private java.lang.String xslt = "/build_edition_rss.xsl";
    private java.lang.String template = "/template_edition_rss.xml";
    private java.lang.String format = "rss";
    private java.lang.String language = "da";
    private org.hibernate.Session session = null;

    private HibernateEditionTool source = null;

    private DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

    public EditionMetadataFormulator() {
    }

    public void setDataSource(HibernateEditionTool source) {
        this.source = source;
        this.source.execute();
    }

    public void setSession(org.hibernate.Session session) {
        this.session = session;
    }


    public Document formulate(String format,
                              String template,
                              String xsl) {

        logger.debug("I'm going to use " + xsl + " on " + template);

        org.w3c.dom.Document result = null;
        javax.xml.parsers.DocumentBuilder dBuilder = null;
        try {
            dfactory.setNamespaceAware(true);
            dBuilder = dfactory.newDocumentBuilder();
            java.io.InputStream in = this.getClass().getResourceAsStream(template);
            result = dBuilder.parse(in);
        } catch (ParserConfigurationException parserPrblm) {
            logger.warn(parserPrblm.getMessage());
        } catch (SAXException xmlPrblm) {
            logger.debug(xmlPrblm.getMessage());
        } catch (IOException ioPrblm) {
            logger.error(ioPrblm.getMessage());
        }

        Element root = result.getDocumentElement();
        Element insert_here;

        if (format.equals("rss")) {
            insert_here = result.createElement("channel");
            root.appendChild(insert_here);
        } else {
            insert_here = root;
        }

        String copGuiUri = this.constants.getConstants().getProperty("gui.uri");

        Transformer transformer = null;
        try {
            logger.debug("xsl path is :" + xsl);
            javax.xml.transform.stream.StreamSource streamSource =
                    new javax.xml.transform.stream.StreamSource(this.getClass().getResourceAsStream(xsl));
            transformer = trans_fact.newTransformer(streamSource);

            logger.debug("Transformer fra xsl " + xsl + " transformer.toString() " + transformer.toString());

        } catch (javax.xml.transform.TransformerConfigurationException transformerPrblm) {
            logger.warn("problem might be: " + transformerPrblm.getMessage());
            transformerPrblm.printStackTrace();
        }
        logger.debug(".. about to create a dom_result");
        javax.xml.transform.dom.DOMResult dom_result =
                new javax.xml.transform.dom.DOMResult(insert_here);

        while (this.source.hasMore()) {

            try {
                logger.debug("In while because more editions");
                Edition edition = this.source.getAnother();
                if (edition == null) {
                    return result;
                }
                java.lang.String subject_uri = edition.getId();
                transformer.clearParameters();
                transformer.setParameter("edition_name", edition.getName());
                transformer.setParameter("base_uri", copGuiUri);

                if (edition.getDescription() != null) {
                    transformer.setParameter("description", edition.getDescription());
                } else {
                    transformer.setParameter("description", "");
                }


                MetadataSource modsSource =
                        new SolrMetadataSource(session);

                String subjectId = edition.getCumulusTopCatagory();
                String retrieve_subject =
                        subject_uri +
                                "/subject" +
                                subjectId +
                                "/" + this.language + "/";

                modsSource.setEdition(edition.getId());

                logger.debug(".. subject uri " + subject_uri);
                logger.debug(".. subject " + subjectId);
                logger.debug(".. retrieve subject " + retrieve_subject);

                modsSource.setNumberPerPage(1);
                modsSource.execute();

                transformer.setParameter("uri", subject_uri + "/" + this.language + "/");

                String mods = "";
                if (modsSource.hasMore()) {
                    logger.debug(".. There are hits");
                    mods = modsSource.getAnother().getMods();

                    if (mods.length() > 0) {
                        logger.debug(".. mods has lenghth");
                    }

                    DOMSource source_dom = null;
                    try {
                        Reader reader = new StringReader(mods);
                        Document source_mods =
                                dBuilder.parse(new org.xml.sax.InputSource(reader));
                        source_dom = new DOMSource(source_mods);
                        logger.debug(".. we've parsed the mods");
                    } catch (SAXException flawedMods) {
                        logger.warn(flawedMods.getMessage());
                    } catch (IOException ioPrblms) {
                        logger.warn(ioPrblms.getMessage());
                    }

                    if (source_dom == null) {
                        logger.debug(".. source_dom is null");
                    } else {
                        logger.debug(".. source_dom is OK");
                    }
                    transformer.transform(source_dom, dom_result);
                    logger.debug(".. tranforme OK");
                }
            } catch (TransformerException trnsFrmPrblm) {
                logger.warn(trnsFrmPrblm.getMessage());
            }
        }
        return result;
    }

    public void setFormat(String format) {
        this.format = format;
        if (format.equals("mods")) {
            this.xslt = "/build_edition_mods_feed.xsl";
            this.template = "/template_mods_collection.xml";
        } else {
            this.xslt = "/build_edition_rss.xsl";
            this.template = "/template_edition_rss.xml";
        }
    }

    public Document formulate() {
        if (this.format.equals("solr"))
            return formulateSolr();
        else
            return this.formulate(this.format,
                    this.template,
                    this.xslt);
    }

    /*
     * Create a simple solr document for each edition
     */
    private Document formulateSolr() {
        Document result = null;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            result = docBuilder.newDocument();
            Element add = result.createElement("add");
            while (this.source.hasMore()) {
                Edition edition = this.source.getAnother();
                Element doc = result.createElement("doc");
                doc.appendChild(createSolrField(result, "id", edition.getId()));
                doc.appendChild(createSolrField(result, "name_ssi", edition.getName()));
                doc.appendChild(createSolrField(result, "name_en_ssi", edition.getNameEn()));
                doc.appendChild(createSolrField(result, "top_cat_ssi", edition.getId() + "/subject" + edition.getCumulusTopCatagory()));
                doc.appendChild(createSolrField(result, "description_tdsim", edition.getDescription()));
                doc.appendChild(createSolrField(result, "description_tesim", edition.getDescriptionEn()));
                doc.appendChild(createSolrField(result, "collection_da_ssi", edition.getCollectionDa()));
                doc.appendChild(createSolrField(result, "collection_en_ssi", edition.getCollectionEn()));
                doc.appendChild(createSolrField(result, "department_da_ssi", edition.getDepartmentDa()));
                doc.appendChild(createSolrField(result, "department_en_ssi", edition.getDepartmentEn()));
                doc.appendChild(createSolrField(result, "contact_email_ssi", edition.getContactEmail()));
                doc.appendChild(createSolrField(result, "medium_ssi", "editions"));
                add.appendChild(doc);
            }
            result.appendChild(add);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Element createSolrField(Document res, String name, String value) {
        Element field = res.createElement("field");
        field.setAttribute("name", name);
        field.setTextContent(value);
        return field;
    }
}

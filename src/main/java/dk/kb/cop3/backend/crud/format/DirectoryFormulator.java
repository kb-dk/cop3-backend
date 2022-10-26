package dk.kb.cop3.backend.crud.format;

import dk.kb.cop3.backend.crud.database.HibernateEditionTool;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

/**
 * Reads a MetadataSource returning a hibernate bean and creates a suitable xml object
 *
 * @author Sigfrid Lundberg (slu@kb.dk)
 * @version $Revision: 1090 $, last modified $Date: 2011-04-26 15:56:35 +0200 (Tue, 26 Apr 2011) $ by $Author: slu $
 * $Id: ModsMetadataFormulator.java 1090 2011-04-26 13:56:35Z slu $
 */

public class DirectoryFormulator extends MetadataFormulator {

    private String xslt = "/build_directory_opml.xsl";
    private String template = "/template_directory_opml.xml";
    private String format = "opml";
    private String language = "da";
    private HibernateEditionTool source = null;

    private DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

    public DirectoryFormulator() {
    }

    public void setDataSource(HibernateEditionTool source) {
        this.source = source;
        this.source.execute();
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return this.language;
    }

    public org.w3c.dom.Document formulate(String format,
                                          String template,
                                          String xsl) {
        Document result = null;
        DocumentBuilder dBuilder;
        Element root;
        Element insert_here;
        try {
            dfactory.setNamespaceAware(true);
            dBuilder = dfactory.newDocumentBuilder();
            java.io.InputStream in = this.getClass().getResourceAsStream(template);
            result = dBuilder.parse(in);
            root = result.getDocumentElement();
            insert_here = result.createElement("body");
            root.appendChild(insert_here);
        } catch (ParserConfigurationException parserPrblm) {
            logger.warn(parserPrblm.getMessage());
        } catch (SAXException xmlPrblm) {
            logger.error(xmlPrblm.getMessage());
        } catch (IOException ioPrblm) {
            logger.error(ioPrblm.getMessage());
        }


        Transformer transformer = null;
        try {
            StreamSource streamSource = new StreamSource(this.getClass().getResourceAsStream(xsl));
            transformer = trans_fact.newTransformer(streamSource);
        } catch (TransformerConfigurationException transformerPrblm) {
            logger.warn("problem might be: " + transformerPrblm.getMessage());
            transformerPrblm.printStackTrace();
        }

        while (this.source.hasMore()) {

            try {
                Edition edition = this.source.getAnother();
                if (edition == null) {
                    return result;
                }
                java.lang.String subject_uri = edition.getId();
                transformer.clearParameters();
                transformer.setParameter("text", edition.getName());
                transformer.setParameter("html_uri", subject_uri);
                java.lang.String node_id = subject_uri;
                node_id = node_id.replaceFirst("/[^/]+/[^/]+/", "");
                node_id = node_id.replaceAll("/", "-");
                transformer.setParameter("node_id", node_id);

                if (edition.getDescription() != null) {
                    transformer.setParameter("description", edition.getDescription());
                } else {
                    transformer.setParameter("description", "");
                }
                DOMSource source_dom = new DOMSource(result);
                DOMResult dom_result = new DOMResult();

                transformer.transform(source_dom, dom_result);
                result = (Document) dom_result.getNode();
            } catch (TransformerException trnsFrmPrblm) {
                logger.warn(trnsFrmPrblm.getMessage());
            }

        }

        return result;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public org.w3c.dom.Document formulate() {
        return this.formulate(this.format,
                this.template,
                this.xslt);
    }


}

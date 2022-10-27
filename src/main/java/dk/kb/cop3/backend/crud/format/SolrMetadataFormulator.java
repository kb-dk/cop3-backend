package dk.kb.cop3.backend.crud.format;

import dk.kb.cop3.backend.constants.CopBackendProperties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xalan.processor.TransformerFactoryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

/**
 * Reads a MetadataSource returning mods data, creating SOLR an add document
 *
 * @author Sigfrid Lundberg (slu@kb.dk)
 * @version $Revision$, last modified $Date$ by $Author$
 *          $Id$
 */

public class SolrMetadataFormulator extends MetadataFormulator {

    private CopBackendProperties consts = CopBackendProperties.getInstance();
    private String copBaseUrl = this.consts.getConstants().getProperty("cop2_backend.baseurl");
    private String baseUrl = copBaseUrl + "/syndication";
    private String copIntBaseUrl = this.consts.getConstants().getProperty("cop2_backend.internal.baseurl");
    private String internalBaseUrl = copIntBaseUrl + "/syndication";

    Logger logger = LogManager.getLogger(SolrMetadataFormulator.class.getPackage().getName());

    private String format = "mods";
    private String xslt = "/build_mods.xsl";
    private String template = "/template_mods_collection.xml";

    TransformerFactory trans_fact = new TransformerFactoryImpl();
    private Transformer[] steps = new Transformer[2];

    public SolrMetadataFormulator() {
        this.steps[0] = this.trInit("/mods2ese.xsl");
        this.steps[1] = this.trInit("/ese_solrizr.xsl");
    }

    public Document formulate() {
        Document solr_doc = null;
        Document src = this.formulate(this.format, this.template, this.xslt);
        DOMSource dom_source = new DOMSource(src);
        DOMResult dom_result = new DOMResult();

        for (int i = 0; i < steps.length; i++) {
            if (steps[i] == null) {
                return null;
            }

            steps[i].setParameter("metadata_context", copBaseUrl);
            steps[i].setParameter("url_prefix", baseUrl);
            steps[i].setParameter("internal_url_prefix", internalBaseUrl);
            steps[i].setParameter("raw_mods", this.currentRawMods);
            steps[i].setParameter("content_context", this.consts.getConstants().getProperty("gui.uri"));
            try {
                steps[i].transform(dom_source, dom_result);
                solr_doc = (Document) dom_result.getNode();
                if (i < steps.length) {
                    dom_source = new DOMSource(solr_doc);
                    dom_result = new DOMResult();
                }
            } catch (TransformerException trnsFrmPrblm) {
                logger.debug(trnsFrmPrblm.getMessage());
            }
        }
        return solr_doc;
    }

    private Transformer trInit(String xsl) {

        String xfile = xsl;
        Transformer transform = null;

        try {
            StreamSource source =
                    new StreamSource(this.getClass().getResource(xfile).toString());

            transform =
                    trans_fact.newTransformer(source);
        } catch (TransformerConfigurationException transformerPrblm) {
            logger.debug("problem might be: " + transformerPrblm.getMessage());
            transformerPrblm.printStackTrace();
        }

        if (transform == null) {
            logger.debug("transform for " + xfile + " didn't work as expected");
        }
        return transform;
    }

    @Override
    protected Element insertElementAt(Document resultSet, Element root) {
        return root;
    }
}

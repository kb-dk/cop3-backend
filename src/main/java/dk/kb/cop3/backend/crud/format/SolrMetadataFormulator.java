package dk.kb.cop3.backend.crud.format;

import dk.kb.cop3.backend.constants.CopBackendProperties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

    org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(SolrMetadataFormulator.class.getPackage().getName());

    private java.lang.String format = "mods";
    private java.lang.String xslt = "/build_mods.xsl";
    private java.lang.String template = "/template_mods_collection.xml";

    javax.xml.transform.TransformerFactory trans_fact = new org.apache.xalan.processor.TransformerFactoryImpl();
    private javax.xml.transform.Transformer[] steps = new javax.xml.transform.Transformer[2];

    public SolrMetadataFormulator() {
        logger.debug("constructing SolrMetadataFormulator");
        this.steps[0] = this.trInit("/mods2ese.xsl");
        logger.debug("got first transform");
        this.steps[1] = this.trInit("/ese_solrizr.xsl");
        logger.debug("done constructing SolrMetadataFormulator");
    }

    public org.w3c.dom.Document formulate() {
        logger.debug("before anything");

        org.w3c.dom.Document solr_doc = (org.w3c.dom.Document) null;

        org.w3c.dom.Document src = this.formulate(this.format, this.template, this.xslt);
        logger.debug("transformed mods " + src);
        javax.xml.transform.dom.DOMSource dom_source = new javax.xml.transform.dom.DOMSource(src);

        javax.xml.transform.dom.DOMResult dom_result = new javax.xml.transform.dom.DOMResult();

	    String exhibition = this.getRequest().getParameter("spotlight_exhibition") == null ? "" : this.getRequest().getParameter("spotlight_exhibition");

        logger.debug("about to transform");

        for (int i = 0; i < steps.length; i++) {
            logger.debug("before step " + i);
            if (steps[i] == null) {
                logger.debug("end of civilization: steps[" + i + "] is null");
                return null;
            }

            steps[i].setParameter("metadata_context", copBaseUrl);
            steps[i].setParameter("url_prefix", baseUrl);
            steps[i].setParameter("internal_url_prefix", internalBaseUrl);
            steps[i].setParameter("spotlight_exhibition",exhibition);
            steps[i].setParameter("raw_mods", this.currentRawMods);
            //	    steps[i].setParameter("url_prefix",copBaseUrl);
            steps[i].setParameter("content_context", this.consts.getConstants().getProperty("gui.uri"));
            try {
                steps[i].transform(dom_source, dom_result);
                solr_doc = (org.w3c.dom.Document) dom_result.getNode();
                logger.debug("transformed solr_doc " + solr_doc);
                if (i < steps.length) {
                    dom_source = new javax.xml.transform.dom.DOMSource(solr_doc);
                    dom_result = new javax.xml.transform.dom.DOMResult();
                }
            } catch (javax.xml.transform.TransformerException trnsFrmPrblm) {
                logger.debug(trnsFrmPrblm.getMessage());
            }
        }
        return solr_doc;
    }

    private javax.xml.transform.Transformer trInit(java.lang.String xsl) {

        String xfile = xsl;
        logger.debug("initializing transform for " + xfile);

        // getResource() returns a file URL

        logger.debug("xsl_path_is :" + this.getClass().getResource(xfile));

        javax.xml.transform.Transformer transform = null;

        try {
            logger.debug("about to make transform for " + xfile);

            // this.getClass().getResource(xfile).toString() the URL object as a systemID

            javax.xml.transform.stream.StreamSource source =
                    new javax.xml.transform.stream.StreamSource(this.getClass().getResource(xfile).toString());

            transform =
                    trans_fact.newTransformer(source);
            logger.debug("hopefully made transform for " + xfile);
        } catch (javax.xml.transform.TransformerConfigurationException transformerPrblm) {
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

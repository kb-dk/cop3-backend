package dk.kb.cop3.backend.crud.format;

import dk.kb.cop3.backend.constants.CopBackendProperties;

import dk.kb.cop3.backend.crud.util.TransformErrorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

/**
 * Reads a MetadataSource returning mods data, creating SOLR an add document
 *
 * @author Sigfrid Lundberg (slu@kb.dk)
 * @version $Revision$, last modified $Date$ by $Author$
 *          $Id$
 */

public class SolrMetadataFormulator extends MetadataFormulator {

    private String copBaseUrl = CopBackendProperties.getCopBackendUrl();
    private String baseUrl = copBaseUrl + "/syndication";
    private String copIntBaseUrl = CopBackendProperties.getCopBackendInternalBaseurl();
    private String internalBaseUrl = copIntBaseUrl + "/syndication";

    protected Logger logger = LoggerFactory.getLogger(SolrMetadataFormulator.class.getPackage().getName());

    private java.lang.String format = "mods";
    private java.lang.String xslt = "/build_mods.xsl";
    private java.lang.String template = "/template_mods_collection.xml";

    javax.xml.transform.TransformerFactory trans_fact = new org.apache.xalan.processor.TransformerFactoryImpl();

    public SolrMetadataFormulator() {
    }

    public Document formulate() {
        Document solr_doc = (org.w3c.dom.Document) null;

        Document src = this.formulate(this.format, this.template, this.xslt);
        DOMSource dom_source = new DOMSource(src);
        DOMResult dom_result = new DOMResult();

        Transformer mods2solr = this.trInit("/mods_solrizr.xsl");
        mods2solr.setErrorListener(new TransformErrorListener("mods_solrizr.xsl"));
        mods2solr.setParameter("metadata_context", copBaseUrl);
        mods2solr.setParameter("url_prefix", baseUrl);
        mods2solr.setParameter("internal_url_prefix", internalBaseUrl);
        mods2solr.setParameter("raw_mods", this.currentRawMods);
        mods2solr.setParameter("content_context", CopBackendProperties.getGuiUri());
        try {
            mods2solr.transform(dom_source,dom_result);
        } catch (javax.xml.transform.TransformerException trnsFrmPrblm) {
            logger.warn("transformer problem "+trnsFrmPrblm.getMessage());
        }
        solr_doc = (Document) dom_result.getNode();

        return solr_doc;
    }

    private javax.xml.transform.Transformer trInit(java.lang.String xsl) {

        String xfile = xsl;
        // getResource() returns a file URL
        javax.xml.transform.Transformer transform = null;

        try {
            // this.getClass().getResource(xfile).toString() the URL object as a systemID
            javax.xml.transform.stream.StreamSource source =
                    new javax.xml.transform.stream.StreamSource(this.getClass().getResource(xfile).toString());

            transform =
                    trans_fact.newTransformer(source);
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

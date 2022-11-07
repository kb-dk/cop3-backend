package dk.kb.cop3.backend.crud.format;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import dk.kb.cop3.backend.constants.CopBackendProperties;

/**
 * Reads a MetadataSource returning mods data, creating a modsCollection document
 * @author Sigfrid Lundberg (slu@kb.dk)
 * @version $Revision$, last modified $Date$ by $Author$
 * $Id$
 */

public class OsdMetadataFormulator extends MetadataFormulator {

    private String copBaseUrl = CopBackendProperties.getCopBackendUrl();
    private String baseUrl    = copBaseUrl + "/syndication";

    org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(OsdMetadataFormulator.class.getPackage().getName());

    private java.lang.String xslt     = "/build_mods.xsl";
    private java.lang.String template = "/template_mods_collection.xml";
    private java.lang.String osd_xslt  = "/build_osd.xsl";
    private java.lang.String format   = "osd";

    public org.w3c.dom.Document formulate() {
	org.w3c.dom.Document doc = null;
	org.w3c.dom.Document src = this.formulate(this.format, this.template, this.xslt);
	javax.xml.transform.dom.DOMSource dom_source = new javax.xml.transform.dom.DOMSource(src);
	javax.xml.transform.dom.DOMResult dom_result = new javax.xml.transform.dom.DOMResult();

	logger.debug("about to transform");

	javax.xml.transform.Transformer	mk = trInit(osd_xslt);

	javax.servlet.http.HttpServletRequest request = this.getRequest();

	if(request.getParameter("osd_id") != null) mk.setParameter("osd_id",request.getParameter("osd_id"));
	if(request.getParameter("showNavigator") != null) mk.setParameter("showNavigator",request.getParameter("showNavigator"));
	if(request.getParameter("initialPage") != null) mk.setParameter("initialPage",request.getParameter("initialPage"));
	if(request.getParameter("defaultZoomLevel") != null) mk.setParameter("defaultZoomLevel",request.getParameter("defaultZoomLevel"));
	if(request.getParameter("sequenceMode") != null) mk.setParameter("sequenceMode",request.getParameter("sequenceMode"));

	try {
	    mk.transform(dom_source,dom_result);
	    doc =  (org.w3c.dom.Document)dom_result.getNode();
	} catch (javax.xml.transform.TransformerException trnsFrmPrblm) {
		logger.debug(trnsFrmPrblm.getMessage());
	}

	return doc;
	
    }

    private javax.xml.transform.Transformer trInit (java.lang.String xsl) {

	String xfile =  xsl;
	logger.debug("initializing transform for " + xfile );

	// getResource() returns a file URL

	logger.debug("xsl_path_is :" + this.getClass().getResource(xfile));

	javax.xml.transform.Transformer transform= null;

        try {
            logger.debug("about to make transform for " + xfile );

	    // this.getClass().getResource(xfile).toString() the URL object as a systemID

	    javax.xml.transform.stream.StreamSource source =
		new javax.xml.transform.stream.StreamSource(this.getClass().getResource(xfile).toString());

	    transform =
		trans_fact.newTransformer(source);
            logger.debug("hopefully made transform for " + xfile );
        } catch (javax.xml.transform.TransformerConfigurationException transformerPrblm) {
            logger.debug("problem might be: " + transformerPrblm.getMessage());
            transformerPrblm.printStackTrace();
        }

	if(transform == null) {
            logger.debug("transform for " + xfile + " didn't work as expected");
	}

	return transform;

    }

    @Override
    protected Element insertElementAt(Document resultSet, Element root) {
        return root;
    }

    @Override
    public javax.ws.rs.core.MediaType mediaType() {
	return javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
    }


}

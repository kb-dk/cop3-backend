package dk.kb.cop3.backend.crud.format;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Reads a MetadataSource returning mods data, creating a modsCollection document
 *
 * @author Sigfrid Lundberg (slu@kb.dk)
 * @version $Revision$, last modified $Date$ by $Author$
 *          $Id$
 */

public class RssMetadataFormulator extends MetadataFormulator {

    //private java.lang.String xslt     = "conf/build_rss.xsl";
    private java.lang.String xslt     = "/build_rss.xsl";
    private java.lang.String template = "/template_rss.xml";
    private java.lang.String format   = "rss";

    public org.w3c.dom.Document formulate() {
        return this.formulate(this.format,
			      this.template,
			      this.xslt);
    }

    @Override
    protected Element insertElementAt(Document resultSet, Element root) {
        Element insert_here = resultSet.createElement("channel");

	root.appendChild(insert_here);
        return insert_here;

    }

    @Override
    public javax.ws.rs.core.MediaType mediaType() {
	return javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE;
    }

}

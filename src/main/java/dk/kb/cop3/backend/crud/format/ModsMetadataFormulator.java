package dk.kb.cop3.backend.crud.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Reads a MetadataSource returning mods data, creating a modsCollection document
 * @author Sigfrid Lundberg (slu@kb.dk)
 * @version $Revision$, last modified $Date$ by $Author$
 * $Id$
 */

public class ModsMetadataFormulator extends MetadataFormulator {

    protected Logger logger = LoggerFactory.getLogger(ModsMetadataFormulator.class.getPackage().getName());

    private java.lang.String xslt     = "/build_mods.xsl";
    private java.lang.String template = "/template_mods_collection.xml";
    private java.lang.String format   = "mods";

    public org.w3c.dom.Document formulate() {
	return this.formulate(this.format,
			      this.template,
			      this.xslt);
    }

    @Override
    protected Element insertElementAt(Document resultSet, Element root) {
        return root;
    }



}

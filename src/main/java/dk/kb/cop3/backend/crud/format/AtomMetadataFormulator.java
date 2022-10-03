package dk.kb.cop3.backend.crud.format;

/**
 * Reads a MetadataSource returning mods data, creating a modsCollection document
 * @author Sigfrid Lundberg (slu@kb.dk)
 * @version $Revision$, last modified $Date$ by $Author$
 * $Id$
 */

public class AtomMetadataFormulator extends MetadataFormulator {
    
    private java.lang.String xslt       = "/build_atom.xsl";
    private java.lang.String template   = "/template_atom.xml";
    private java.lang.String format     = "atom";
    
    public org.w3c.dom.Document formulate() {
	return this.formulate(this.format,
			      this.template,
			      this.xslt);
    }
}

package dk.kb.cop3.backend.crud.format;

import org.w3c.dom.Document;
import org.w3c.dom.Element;



/**
 * Reads a MetadataSource returning mods data, creating a modsCollection document
 *
 * @author Sigfrid Lundberg (slu@kb.dk)
 * @version $Revision: 995 $, last modified $Date: 2011-04-15 11:35:08 +0200 (Fri, 15 Apr 2011) $ by $Author: slu $
 *          $Id: ModsMetadataFormulator.java 995 2011-04-15 09:35:08Z slu $
 */

public class KmlMetadataFormulator extends MetadataFormulator {

    private String xslt = "/build_kml.xsl";
    private String template = "/template_kml.xml";
    private String format = "kml";

    public Document formulate() {
        return this.formulate(this.format, this.template, this.xslt);
    }

    @Override
    protected Element insertElementAt(Document resultSet, Element root) {
        return (Element) root.getElementsByTagName("Document").item(0);
    }

}

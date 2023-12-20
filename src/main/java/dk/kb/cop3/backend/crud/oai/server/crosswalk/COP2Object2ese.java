package dk.kb.cop3.backend.crud.oai.server.crosswalk;

import ORG.oclc.oai.server.crosswalk.Crosswalk;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: dgj
 * Date: 02-11-11
 * Time: 12:39
 * To change this template use File | Settings | File Templates.
 *
 */
public class COP2Object2ese extends Crosswalk {
    private static final Logger logger = LoggerFactory.getLogger(COP2Object2ese.class);
    private MetadataCreator md = new MetadataCreator();

    public COP2Object2ese(Properties properties) {
        super("http://www.europeana.eu/schemas/ese/ http://www.europeana.eu/schemas/ese/ESE-V3.3.xsd");
        String xsltfile = properties.getProperty( "COP2OAICatalog.mods2ese_xslt" );

        if (xsltfile == null) {

            // if xslt file not found in property, use hardcoded default.
            md.setXsltFile("/mods2ese.xsl");
            //throw new IllegalArgumentException("Missing property 'COP2OAICatalog.mods2ese' RELYING ON DEFAULT VALUES mods2ese.xsl");
            logger.warn("Missing property 'COP2OAICatalog.mods2ese' RELYING ON DEFAULT VALUES mods2ese.xsl");
	} else {
	    md.setXsltFile(xsltfile);
	}
        String urlprefix = "http://"+(String) properties.get("COP2OAICatalog.COPServerName");
	md.setUrlPrefix(urlprefix);
    }

    @Override
    public boolean isAvailableFor(Object o) {
        // ESE is always available
        return true;
    }

    @Override
    public String createMetadata(Object obj) throws CannotDisseminateFormatException {
	return md.transform(obj,"ese");
    }

}

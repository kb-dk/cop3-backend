package dk.kb.cop3.backend.crud.oai.server.crosswalk;

import ORG.oclc.oai.server.crosswalk.Crosswalk;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: dgj
 * Date: 02-11-11
 * Time: 12:39
 * To change this template use File | Settings | File Templates.
 *
 */
public class COP2Object2oai_dc extends Crosswalk {
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(COP2Object2oai_dc.class);
    private MetadataCreator md = new MetadataCreator();

    public COP2Object2oai_dc(Properties properties) {
        super( "http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd" );
        String xsltfile = properties.getProperty( "COP2OAICatalog.mods2dc_xslt" );

        if (xsltfile == null) {
            throw new IllegalArgumentException("Missing property 'COP2OAICatalog.mods2dc_xslt'");
	} else {
	    md.setXsltFile(xsltfile);
	}
        String urlprefix = "http://"+(String) properties.get("COP2OAICatalog.COPServerName");
	md.setUrlPrefix(urlprefix);
    }

    @Override
    public boolean isAvailableFor(Object o) {
        // DC is always available
        return true;
    }

    @Override
    public String createMetadata(Object obj) throws CannotDisseminateFormatException {
	return md.transform(obj,"oai_dc");
    }

}

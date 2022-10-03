package dk.kb.cop3.backend.crud.oai.server.crosswalk;

import ORG.oclc.oai.server.crosswalk.Crosswalk;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;

import java.util.Properties;

/**
 * The Original Code is XML2oai_dc.java.  The Initial Developer of the
 * Original Code is Jeff Young.  Portions created by The Royal Library are
 * Copyright (C) 2009 The Royal Library. All Rights Reserved.
 * Contributor(s):Carl Alex Friis Nielsen, David Grove Jørgensen and Sigfrid
 * Lundberg.
 */
public class COP2Object2mods extends Crosswalk {
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(COP2Object2mods.class);
    private MetadataCreator md = new MetadataCreator();

    /**
     * The constructor assigns the schemaLocation associated with this crosswalk. Since
     * the crosswalk is trivial in this case, no properties are utilized.
     *
     * @param properties properties that are needed to configure the crosswalk.
     */
    public COP2Object2mods(Properties properties) {
        super("http://www.loc.gov/standards/mods/v3/mods-3-3/ http://www.loc.gov/standards/mods/v3/mods-3-3.xsd");

        String xsltfile = properties.getProperty("COP2OAICatalog.mods2mods_xslt");

        if (xsltfile == null) {

            logger.error("Property COP2OAICatalog.mods2mods_xslt=" + properties.getProperty("COP2OAICatalog.mods2mods_xslt"));
            logger.error("Property COP2OAICatalog.COPServerName=" + properties.getProperty("COP2OAICatalog.COPServerName"));
            logger.info("PROPERTIES: ### ");
            try {
                for (Object o : properties.keySet()) {
                    logger.info(" key " + (String) o + " value " + properties.getProperty((String) o));

                }
            } catch (Exception e) {
                logger.error("couldn't print out all properties...");
            }

            // if xslt file not found in property, use hardcoded default.
            md.setXsltFile("/mods2mods.xsl");
            logger.warn("Missing property 'COP2OAICatalog.mods2mods_xslt' RELYING ON DEFAULT VALUES /mods2mods.xsl");
            //throw new IllegalArgumentException("Missing property 'COP2OAICatalog.mods2mods_xslt' RELYING ON DEFAULT VALUES /mods2mods.xsl");
        } else {
            md.setXsltFile(xsltfile);
        }

        String urlprefix = "http://" + (String) properties.get("COP2OAICatalog.COPServerName");
        md.setUrlPrefix(urlprefix);

    }

    @Override
    public boolean isAvailableFor(Object o) {
        // mods is always available in COP
        return true;
    }

    @Override
    public String createMetadata(Object obj) throws CannotDisseminateFormatException {
        return md.transform(obj, "mods");
    }

}

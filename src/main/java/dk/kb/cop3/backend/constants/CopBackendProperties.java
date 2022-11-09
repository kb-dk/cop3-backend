package dk.kb.cop3.backend.constants;

import org.apache.log4j.Logger;
import java.io.*;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: jac
 * Date: 8/23/11
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class CopBackendProperties {

     private static Logger logger = Logger.getLogger(CopBackendProperties.class);

    private static Properties props = null;

    public static Properties getProperties() {return props;}

    public static synchronized void initialize(InputStream input) {
        props = new Properties();
        try {
            props.loadFromXML(input);
        } catch (InvalidPropertiesFormatException e) {
            logger.fatal("Invalid properties");
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.fatal("Error reading properties");
            throw new RuntimeException(e);
        }
    }

    public static String getDatabaseUrl() {
        return (String) props.get("database.url");
    }

    public static String getDatabaseUser() {
        return (String) props.get("database.user");
    }

    public static String getDatabasePassword() {
        return (String) props.get("database.password");
    }

    public static String getCopBackendUrl() {
        return (String) props.getProperty("cop2_backend.baseurl");
    }

    public static String getGuiUri() {
        return (String) props.getProperty("gui.uri");
    }

    public static String getSolrBaseurl() {
        return (String) props.getProperty("solr.baseurl");
    }

    public static String getCopBackendInternalBaseurl() {
        return props.getProperty("cop2_backend.internal.baseurl");
    }

    public static String getDefaultTemplate() {
        return props.getProperty("template.default");
    }
}

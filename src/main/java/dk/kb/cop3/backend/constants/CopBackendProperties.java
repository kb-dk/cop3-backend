package dk.kb.cop3.backend.constants;

import org.apache.log4j.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

    private Properties props = null;

    private static CopBackendProperties ourInstance = new CopBackendProperties();

    public static CopBackendProperties getInstance() {
        return ourInstance;
    }

    private CopBackendProperties() {
        String propFile = "/cop_config.xml";
        this.setConstants(propFile);
    }

    public void setConstants(String propFile) {
        this.props = new Properties();
        try {
            InputStream in = this.getClass().getResourceAsStream(propFile);
            props.loadFromXML(in);
        } catch (FileNotFoundException fileNotFound) {
            logger.error(String.format("The file '%s' was not found", propFile), fileNotFound);
        } catch (IOException ioException) {
            logger.error(String.format("An exception occurred while reading from the file '%s' ", propFile), ioException);
        }
    }

     public Properties getConstants() {
        return this.props;
    }
}

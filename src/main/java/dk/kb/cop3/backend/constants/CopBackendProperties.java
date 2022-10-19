package dk.kb.cop3.backend.constants;

import org.apache.log4j.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static dk.kb.cop3.backend.constants.ObjSubPag.object;

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
        InputStream in;
        String propFile;
        try {
            InitialContext ctx = new InitialContext();
            propFile = (String) ctx.lookup("java:comp/env/copBackendProperties");
            in = new FileInputStream(propFile);
        } catch (NamingException e) {
            logger.warn("Using default cop properties");
            in = this.getClass().getResourceAsStream("/cop_config.xml");
        } catch (FileNotFoundException e) {
            logger.fatal("Configfile not found ",e);
            throw new RuntimeException("Configfile not found ",e);
        }
        this.setConstants(in);
    }

    public void setConstants(InputStream in) {
        this.props = new Properties();
        try {
            props.loadFromXML(in);
        } catch (FileNotFoundException fileNotFound) {
            logger.error(String.format("The file '%s' was not found"), fileNotFound);
        } catch (IOException ioException) {
            logger.error(String.format("An exception occurred while reading from the file"), ioException);
        }
    }

    public String getDatabaseUrl() {
        return (String) this.props.get("database.url");
    }

    public String getDatabaseUser() {
        return (String) this.props.get("database.user");
    }

    public String getDatabasePassword() {
        return (String) this.props.get("database.password");
    }

     public Properties getConstants() {
        return this.props;
    }
}

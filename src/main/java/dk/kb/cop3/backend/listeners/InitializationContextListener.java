package dk.kb.cop3.backend.listeners;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class InitializationContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(InitializationContextListener.class);
    public static String version;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        version = getClass().getPackage().getImplementationVersion();
        InputStream input;
        try {
            InitialContext ctx = new InitialContext();
            String configfile = (String) ctx.lookup("java:/comp/env/copBackendProperties");
            input = new FileInputStream(configfile);
        } catch (NamingException e) {
            logger.info("No properties file in context using default properties");
            input = getClass().getResourceAsStream("/cop_config.xml");

        } catch (FileNotFoundException e) {
            logger.error("Cannot open propertiesfile",e);
            throw new RuntimeException(e);
        }
        CopBackendProperties.initialize(input);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // do nothing
    }

    private void initializeCopBackendProperties() {
    }
}

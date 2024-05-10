package dk.kb.cop3.backend.listeners;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.util.Files;
import dk.kb.util.Resolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.*;
import java.net.MalformedURLException;

public class InitializationContextListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(InitializationContextListener.class);
    public static String version;
    public static final String LOGBACK_ENV = "java:/comp/env/logbackConfigFile";


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        version = getClass().getPackage().getImplementationVersion();
        initLogging();
        InputStream input;
        try {
            InitialContext ctx = new InitialContext();
            String configfile = (String) ctx.lookup("java:/comp/env/copBackendProperties");
            input = new FileInputStream(configfile);
        } catch (NamingException e) {
            log.info("No properties file in context using default properties");
            input = getClass().getResourceAsStream("/cop_config.xml");

        } catch (FileNotFoundException e) {
            log.error("Cannot open propertiesfile",e);
            throw new RuntimeException(e);
        }
        CopBackendProperties.initialize(input);
    }

    private void initLogging() {
        try {
            if (Resolver.resolveURL("logback-test.xml") != null) {
                log.info("Logback config 'logback-test.xml' found. Running in test mode");
                return;
            }

        } catch (Exception e) {
            //logback-test not found
        }

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        String logbackFile = null;
        try {
            InitialContext ctx = new InitialContext();

            // Resolve the configured setup for logback
            try {
                logbackFile = (String) ctx.lookup(LOGBACK_ENV);
            } catch (NamingException e) {
                logbackFile = System.getenv("logbackConfigFile");
            }

            // Check for logback setup file existence (throws an exception if it fails)
            Resolver.resolveURL(logbackFile);

            // Create a temporary file that includes the real config file (see JavaDoc for this method for details)
            File redirect = createRedirect(logbackFile);

            // https://dennis-xlc.gitbooks.io/the-logback-manual/content/en/chapter-3-configuration/configuration-in-logback/invoking-joranconfigurator-directly.html
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            configurator.doConfigure(redirect);
            log.info("Successfully reconfigured logback with '{}'", logbackFile);
        } catch (NamingException e) {
            log.warn("Failed to lookup logback config file from context '{}'. Continuing", LOGBACK_ENV, e);
        } catch (JoranException e) {
            StatusPrinter.printInCaseOfErrorsOrWarnings(context);
            throw new RuntimeException("Failed to configure logger from '" + logbackFile + "'", e);
        } catch (MalformedURLException | FileNotFoundException e) {
            log.warn("Resolved '{}' to path '{}', which does not exist. Continuing", LOGBACK_ENV, logbackFile);
        }
    }

    private static File createRedirect(String logbackFile) {
        File redirectFile;
        try {
            redirectFile = File.createTempFile("logback-loader_", ".xml");
        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to create temporary file for logback configuration of '" + logbackFile + "'", e);
        }
        redirectFile.deleteOnExit();

        try {
            String redirectContent =
                    "<configuration>\n" +
                            "<include file=\"" + logbackFile + "\"/>\n" +
                            "</configuration>\n";
            Files.saveString(redirectContent, redirectFile);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to save redirect logback for '" + logbackFile + "' to temp file '" + redirectFile + "'", e);
        }

        return redirectFile;
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // do nothing
    }

    private void initializeCopBackendProperties() {
    }
}

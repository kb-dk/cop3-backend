package dk.kb.cop3.backend.migrate;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import dk.kb.cop3.backend.constants.CopBackendProperties;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MigrationUtils {


    public static void initializeMigration() {
        String configFile = System.getProperty("dk.kb.cop.propertiesFile");
        if (configFile == null) {
            configFile = "src/main/resources/cop_config.xml";
        }
        try {
            CopBackendProperties.initialize(new FileInputStream(configFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("unable to read properties "+configFile,e);
        }
    }

    public static Session getOracleSession() {
        Configuration oraConf = new Configuration().configure("oracle/hibernate-oracle.cfg.xml");
        oraConf.setProperty("hibernate.connection.url", CopBackendProperties.getProperties().getProperty("oradatabase.url"));
        SessionFactory oracSessfac = oraConf.buildSessionFactory();
        Session oraSession = oracSessfac.openSession();
        return oraSession;
    }

    public static SessionFactory getPostgresSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml")
                    .setProperty(Environment.URL, CopBackendProperties.getDatabaseUrl())
                    .setProperty(Environment.USER,CopBackendProperties.getDatabaseUser())
                    .setProperty(Environment.PASS,CopBackendProperties.getDatabasePassword());
            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new RuntimeException("unable to initilize postgres",ex);
        }
    }
}

package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateDB {
    private static final Logger logger = LoggerFactory.getLogger(CreateDB.class);
    public static void main(String[] args) {
        MigrationUtils.initializeMigration();
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml")
                .setProperty(Environment.URL, CopBackendProperties.getDatabaseUrl())
                .setProperty(Environment.USER,CopBackendProperties.getDatabaseUser())
                .setProperty(Environment.PASS,CopBackendProperties.getDatabasePassword())
                .setProperty(Environment.HBM2DDL_AUTO,"update");
        configuration.buildSessionFactory();
        logger.info("Database Created");
    }
}

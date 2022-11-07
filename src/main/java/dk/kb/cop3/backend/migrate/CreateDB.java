package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

public class CreateDB {
    private static Logger logger = Logger.getLogger(CreateDB.class);
    public static void main(String[] args) {
        MigrationUtils.initializeMigration();
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml")
                .setProperty(Environment.URL, CopBackendProperties.getDatabaseUrl())
                .setProperty(Environment.USER,CopBackendProperties.getDatabaseUser())
                .setProperty(Environment.PASS,CopBackendProperties.getDatabasePassword())
                .setProperty(Environment.HBM2DDL_AUTO,"update");
        configuration.buildSessionFactory();
    }
}

package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.migrate.hibernate.EditionOracle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CheckDB {

    private static final Logger log = LoggerFactory.getLogger(CheckDB.class);
    public static void main(String[] args) {
        MigrationUtils.initializeMigration();
        CopBackendProperties.getProperties().list(System.out);
        SessionFactory psqlSessfac = MigrationUtils.getPostgresSessionFactory();
        Session oraSession = MigrationUtils.getOracleSession();
        List<EditionOracle> editions = oraSession.createQuery("from dk.kb.cop3.backend.migrate.hibernate.EditionOracle").list();

        for(EditionOracle eo: editions) {
            log.info("got edition "+eo.getId());
        }

        List<Edition> psqlEditions = psqlSessfac.openSession().createQuery("from dk.kb.cop3.backend.crud.database.hibernate.Edition").list();

        System.out.println("postgres editions");
        for(Edition e: psqlEditions) {
            log.info("got edition "+e.getId());
        }

        log.info("Database Created");
    }


}

package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.migrate.hibernate.EditionOracle;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class MigrateEditions {
    private static Logger logger = Logger.getLogger(MigrateEditions.class);


    public static void main(String[] args) {
        MigrationUtils.initializeMigration();
        Session oraSession = MigrationUtils.getOracleSession();
        SessionFactory psqlSessfac = MigrationUtils.getPostgresSessionFactory();

        List<EditionOracle> editions = oraSession.createQuery("from dk.kb.cop3.backend.migrate.hibernate.EditionOracle").list();
        editions.stream()
                .map(oraEdition -> ObjectConverter.convertEdition(oraEdition))
                .forEach(edition-> { saveEditionInPostgres(psqlSessfac,edition);
                });
    }

    private static void saveEditionInPostgres(SessionFactory psqlSessFac, Edition edition) {
        logger.info("Saving edition "+edition.getId());
        Session psqlSession = psqlSessFac.openSession();
        Transaction trans = psqlSession.beginTransaction();
        psqlSession.save(edition);
        trans.commit();
        psqlSession.close();
    }

}

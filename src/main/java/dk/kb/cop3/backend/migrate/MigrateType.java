package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.crud.database.hibernate.Type;
import dk.kb.cop3.backend.migrate.hibernate.TypeOracle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MigrateType {
    private static final Logger logger = LoggerFactory.getLogger(MigrateType.class);


    public static void main(String[] args) {
        MigrationUtils.initializeMigration();
        Session oraSession = MigrationUtils.getOracleSession();
        SessionFactory psqlSessfac = MigrationUtils.getPostgresSessionFactory();

        List<TypeOracle> editions = oraSession.createQuery("from dk.kb.cop3.backend.migrate.hibernate.TypeOracle").list();
        editions.stream()
                .map(oraType -> ObjectConverter.convertType(oraType))
                .forEach(type-> { saveTypeInPostgres(psqlSessfac,type);
                });
    }

    private static void saveTypeInPostgres(SessionFactory psqlSessFac, Type type) {
        logger.info("Saving type "+type.getId());
        Session psqlSession = psqlSessFac.openSession();
        Transaction trans = psqlSession.beginTransaction();
        psqlSession.save(type);
        trans.commit();
        psqlSession.close();
    }

}

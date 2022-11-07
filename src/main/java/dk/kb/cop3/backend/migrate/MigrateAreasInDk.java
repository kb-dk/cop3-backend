package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.migrate.hibernate.AreasInDkOracle;
import dk.kb.cop3.backend.migrate.ObjectConverter;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class MigrateAreasInDk {
    private static Logger logger = Logger.getLogger(MigrateAreasInDk.class);

    public static void main(String[] args) {
        MigrationUtils.initializeMigration();
        Session oraSession = MigrationUtils.getOracleSession();
        SessionFactory psqlSessfac = MigrationUtils.getPostgresSessionFactory();

        List<AreasInDkOracle> areas = oraSession.createQuery("from dk.kb.cop3.backend.migrate.hibernate.AreasInDkOracle").list();

        areas.stream()
                .map(oraArea -> {
                    return ObjectConverter.convertArea(oraArea);})
                .forEach(area-> {
                    logger.debug("saving area "+ area.getPolygonCol());
                    Session psqlSession = psqlSessfac.openSession();
                    Transaction trans = psqlSession.beginTransaction();
                    psqlSession.save(area);
                    trans.commit();
                    psqlSession.close();
                });
    }
}

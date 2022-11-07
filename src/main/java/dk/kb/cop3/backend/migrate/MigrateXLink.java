package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.migrate.hibernate.XLinkOracle;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class MigrateXLink {
    private static Logger logger = Logger.getLogger(MigrateXLink.class);

    public static void main(String[] args) {
        MigrationUtils.initializeMigration();
        Session oraSession = MigrationUtils.getOracleSession();
        SessionFactory psqlSessfac = MigrationUtils.getPostgresSessionFactory();

        List<XLinkOracle> xlinks = oraSession.createQuery("from dk.kb.cop3.backend.migrate.hibernate.XLinkOracle").list();
        xlinks.stream()
                .map(oraXLink -> {
                    return ObjectConverter.convertXLink(oraXLink);})
                .forEach(xlink-> {
                    logger.info("Saving xlink "+xlink.getId());
                    Session psqlSession = psqlSessfac.openSession();
                    Transaction trans = psqlSession.beginTransaction();
                    psqlSession.save(xlink);
                    trans.commit();
                    psqlSession.close();
                });
    }

}

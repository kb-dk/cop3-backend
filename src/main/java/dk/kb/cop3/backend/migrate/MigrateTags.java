package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.migrate.hibernate.TagOracle;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class MigrateTags {
    private static final Logger logger = Logger.getLogger(MigrateTags.class);

    public static void main(String[] args) {
        MigrationUtils.initializeMigration();
        Session oraSession = MigrationUtils.getOracleSession();
        SessionFactory psqlSessfac = MigrationUtils.getPostgresSessionFactory();

        List<TagOracle> tags = oraSession.createQuery("from dk.kb.cop3.backend.migrate.hibernate.TagOracle").list();
        tags.stream()
                .map(oraTag -> {
                    return ObjectConverter.convertTag(oraTag);})
                .forEach(tag-> {
                    logger.debug("saving tag "+tag.getTag_value());
                    Session psqlSession = psqlSessfac.openSession();
                    Transaction trans = psqlSession.beginTransaction();
                    psqlSession.save(tag);
                    trans.commit();
                    psqlSession.close();
                });
    }
}

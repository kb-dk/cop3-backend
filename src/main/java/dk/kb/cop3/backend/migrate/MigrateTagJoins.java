package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.crud.database.hibernate.Tag;
import dk.kb.cop3.backend.crud.database.hibernate.TagJoin;
import dk.kb.cop3.backend.migrate.hibernate.TagJoinOracle;
import dk.kb.cop3.backend.migrate.hibernate.TagOracle;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class MigrateTagJoins {
    private static final Logger logger = Logger.getLogger(MigrateTagJoins.class);


    public static void main(String[] args) {
        MigrationUtils.initializeMigration();
        Session oraSession = MigrationUtils.getOracleSession();
        SessionFactory psqlSessfac = MigrationUtils.getPostgresSessionFactory();
        Session psqlSession = psqlSessfac.openSession();

        List<TagJoinOracle> tagJoinOracles = oraSession.createQuery("from dk.kb.cop3.backend.migrate.hibernate.TagJoinOracle").list();
        tagJoinOracles.stream().forEach(tagJoinOracle -> {
            TagJoin tagJoin = new TagJoin();
            Object object = psqlSession.get(Object.class,tagJoinOracle.getOid());
            Tag tag = psqlSession.get(Tag.class,tagJoinOracle.getTid());
            if (object != null && tag !=null) {
                tagJoin.setTag(tag);
                tagJoin.setObject(object);
                tagJoin.setCreator(tagJoinOracle.getCreator());
                tagJoin.setTimestamp(tagJoinOracle.getTimestamp());
                try {
                    Transaction tx = psqlSession.beginTransaction();
                    psqlSession.save(tagJoin);
                    tx.commit();
                } catch (Exception ex) {
                    logger.error("error saving tagjoin ",ex);
                }
            } else {
                logger.error("object or tag not migrated "+tagJoinOracle.toString());
            }
        });
    }
}

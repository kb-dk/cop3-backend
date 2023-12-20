package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.crud.database.hibernate.Comment;
import dk.kb.cop3.backend.migrate.hibernate.CommentOracle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MigrateComments {
    private static final Logger logger = LoggerFactory.getLogger(MigrateComments.class);

    public static void main(String[] args) {
        MigrationUtils.initializeMigration();
        Session oraSession = MigrationUtils.getOracleSession();
        SessionFactory psqlSessfac = MigrationUtils.getPostgresSessionFactory();
        List<CommentOracle> comments = oraSession.createQuery("FROM CommentOracle").list();

        comments.stream()
                .map(ObjectConverter::convertComment)
                .forEach(comment -> saveCommentInPostgres(psqlSessfac,comment));

    }

    private static void saveCommentInPostgres(SessionFactory sessFac, Comment comment) {
        logger.info("Saving comment "+comment);
        try {
            Session session = sessFac.openSession();
            Transaction trans = session.beginTransaction();
            session.save(comment);
            trans.commit();
            session.close();
        } catch (RuntimeException ex) {
            logger.error("Error saving comment ",ex);
        }
    }
}

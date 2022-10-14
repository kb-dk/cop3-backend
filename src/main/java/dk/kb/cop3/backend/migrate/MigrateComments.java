package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.crud.database.hibernate.Comment;
import dk.kb.cop3.backend.migrate.hibernate.CommentOracle;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class MigrateComments {
    private static final Logger logger = Logger.getLogger(MigrateComments.class);

    public static void main(String[] args) {
        Session oraSession = getOracleSession();
        List<CommentOracle> comments = oraSession.createQuery("FROM CommentOracle").list();

        SessionFactory psqlSessfac = new Configuration().configure("hibernate.cfg.xml")
                .buildSessionFactory();

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


    private static Session getOracleSession() {
        Configuration oraConf = new Configuration().configure("oracle/hibernate-oracle.cfg.xml");
        SessionFactory oracSessfac = oraConf.buildSessionFactory();
        return oracSessfac.openSession();
    }

}

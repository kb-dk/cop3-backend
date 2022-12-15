package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.crud.database.hibernate.Category;
import dk.kb.cop3.backend.migrate.hibernate.CategoryOracle;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class MigrateCategories {
    private static Logger logger = Logger.getLogger(MigrateCategories.class);

    public static void main(String[] args) {
        MigrationUtils.initializeMigration();
        Session oraSession = MigrationUtils.getOracleSession();
        SessionFactory psqlSessfac = MigrationUtils.getPostgresSessionFactory();
        List<CategoryOracle> categories = oraSession.createQuery("FROM CategoryOracle c where c.id like '/images/luftfo/2011/maj/luftfoto%'").list();

        categories.stream()
                .map(ObjectConverter::convertCategory)
                .forEach(category -> saveCategoryInPostgres(psqlSessfac,category));

    }

    private static void saveCategoryInPostgres(SessionFactory sessFac, Category category) {
        logger.debug("Saving category "+category);
        Session session = sessFac.openSession();
        Transaction trans = session.beginTransaction();
        session.saveOrUpdate(category);
        trans.commit();
        session.close();
    }
}

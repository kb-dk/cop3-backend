package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.migrate.hibernate.ObjectOracle;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MigrateObjects {

    private static final Logger logger = LoggerFactory.getLogger(MigrateObjects.class);

    public static void main(String[] args) {
        MigrationUtils.initializeMigration();
        Session oraSession = MigrationUtils.getOracleSession();
        SessionFactory psqlSessfac = MigrationUtils.getPostgresSessionFactory();

        int pageSize = 1000;
        int startPage = 0;
        if (args.length > 0) {
            startPage = Integer.parseInt(args[0]);
        }

        List<ObjectOracle> objects = new ArrayList<>();
        for (int pageNo = startPage; pageNo == startPage || !objects.isEmpty(); pageNo++) {
            logger.info("fetching object from oracle. Firstresult:"+pageNo*pageSize);
            objects = oraSession.createQuery("from ObjectOracle o where o.edition.id = '/images/luftfo/2011/maj/luftfoto'")
                    .setMaxResults(pageSize)
                    .setFirstResult(pageNo * pageSize)
                    .list();
            Session psqlSession = psqlSessfac.openSession();
            Transaction trans = psqlSession.beginTransaction();
            objects.stream()
                    .filter(oraObject -> {return "/images/luftfo/2011/maj/luftfoto".equals(oraObject.getEdition().getId());})
                    .map(oraObject -> ObjectConverter.convertObject(oraObject))
                    .forEach(Object -> {
                        saveObjectInPostgres(psqlSession, Object);
                    });
            logger.info("Commiting");
            trans.commit();
            logger.info("closing");
            psqlSession.close();
            logger.info("clearing");
            oraSession.clear();
        }
    }

    private static void saveObjectInPostgres(Session session, Object object) {
        logger.info("saving "+object.getId());
        session.saveOrUpdate(object);
    }

}

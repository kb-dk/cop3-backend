package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.migrate.hibernate.ObjectOracle;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.List;

public class MigrateObjects {

    private static Logger logger = Logger.getLogger(MigrateObjects.class);



    public static void main(String[] args) {
        Session oraSession = MigrationUtils.getOracleSession();

        SessionFactory psqlSessfac = new Configuration().configure("hibernate.cfg.xml")
                .setProperty("hibernate.jdbc.batch_size", "1000")
                .buildSessionFactory();
        int pageSize = 1000;
        int startPage = 0;
        if (args.length > 0) {
            startPage = Integer.parseInt(args[0]);
        }

        List<ObjectOracle> objects = new ArrayList<>();
        for (int pageNo = startPage; pageNo == startPage || !objects.isEmpty(); pageNo++) {
            logger.info("fetching object from oracle. Firstresult:"+pageNo*pageSize);
            objects = oraSession.createQuery("from ObjectOracle")
                    .setMaxResults(pageSize)
                    .setFirstResult(pageNo * pageSize)
                    .list();
            Session psqlSession = psqlSessfac.openSession();
            Transaction trans = psqlSession.beginTransaction();
            logger.info("Saving objects");
            objects.stream()
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
        session.saveOrUpdate(object);
    }

}

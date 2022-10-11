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
                .buildSessionFactory();
        int pageSize = 100000;

        List<ObjectOracle> objects = new ArrayList<>();
        for (int pageNo = 0; pageNo == 0 || !objects.isEmpty(); pageNo++) {
            logger.info("migrating objects "+pageNo*pageSize);
            objects = oraSession.createQuery("from ObjectOracle")
                    .setMaxResults(pageSize)
                    .setFirstResult(pageNo * pageSize)
                    .list();
            objects.stream()
                    .map(oraObject -> ObjectConverter.convertObject(oraObject))
                    .forEach(Object -> {
                        saveObjectInPostgres(psqlSessfac, Object);
                    });
        }
    }

    private static void saveObjectInPostgres(SessionFactory psqlSessFac, Object object) {
        logger.info("Saving Object "+object.getId());
        Session psqlSession = psqlSessFac.openSession();
        Transaction trans = psqlSession.beginTransaction();
        psqlSession.save(object);
        trans.commit();
        psqlSession.close();
    }

}

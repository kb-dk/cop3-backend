package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.migrate.hibernate.ObjectOracle;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class MigrateObjects {

    private static Logger logger = Logger.getLogger(MigrateObjects.class);


    public static void main(String[] args) {
        Session oraSession = MigrationUtils.getOracleSession();

        SessionFactory psqlSessfac = new Configuration().configure("hibernate.cfg.xml")
                .buildSessionFactory();


        List<ObjectOracle> editions = oraSession.createQuery("from ObjectOracle")
                .setMaxResults(1000)
                .list();
        editions.stream()
                .map(oraObject -> ObjectConverter.convertObject(oraObject))
                .forEach(Object-> { saveObjectInPostgres(psqlSessfac,Object);
                });
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

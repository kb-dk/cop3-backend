package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.crud.database.hibernate.Type;
import dk.kb.cop3.backend.migrate.hibernate.TypeOracle;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class MigrateType {
    private static Logger logger = Logger.getLogger(MigrateType.class);


    public static void main(String[] args) {
        Session oraSession = MigrationUtils.getOracleSession();

        SessionFactory psqlSessfac = new Configuration().configure("hibernate.cfg.xml")
                .buildSessionFactory();


        List<TypeOracle> editions = oraSession.createQuery("from dk.kb.cop3.backend.migrate.hibernate.TypeOracle").list();
        editions.stream()
                .map(oraType -> ObjectConverter.convertType(oraType))
                .forEach(type-> { saveTypeInPostgres(psqlSessfac,type);
                });
    }

    private static void saveTypeInPostgres(SessionFactory psqlSessFac, Type type) {
        logger.info("Saving type "+type.getId());
        Session psqlSession = psqlSessFac.openSession();
        Transaction trans = psqlSession.beginTransaction();
        psqlSession.save(type);
        trans.commit();
        psqlSession.close();
    }

}

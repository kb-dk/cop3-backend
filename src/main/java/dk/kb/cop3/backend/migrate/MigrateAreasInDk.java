package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.migrate.hibernate.AreasInDkOracle;
import dk.kb.cop3.backend.migrate.ObjectConverter;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class MigrateAreasInDk {
    private static Logger logger = Logger.getLogger(MigrateAreasInDk.class);

    public static void main(String[] args) {
        Session oraSession = getOracleSession();

        SessionFactory psqlSessfac = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();


        List<AreasInDkOracle> areas = oraSession.createQuery("from dk.kb.cop3.backend.migrate.hibernate.AreasInDkOracle").list();
        areas.stream()
                .map(oraArea -> {
                    return ObjectConverter.convertArea(oraArea);})
                .forEach(xlink-> {
			//                    logger.info("Saving area "+oraArea.getAreaId());
                    Session psqlSession = psqlSessfac.openSession();
                    Transaction trans = psqlSession.beginTransaction();
                    psqlSession.save(xlink);
                    trans.commit();
                    psqlSession.close();
                });
    }

    private static Session getOracleSession() {
        Configuration oraConf = new Configuration().configure("oracle/hibernate-oracle.cfg.xml");
        SessionFactory oracSessfac = oraConf.buildSessionFactory();
        Session oraSession = oracSessfac.openSession();
        return oraSession;
    }

}

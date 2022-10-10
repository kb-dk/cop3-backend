package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.migrate.hibernate.EditionOracle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class MigrateEditions {
    public static void main(String[] args) {
        Session oraSession = getOracleSession();

        SessionFactory psqlSessfac = new Configuration().configure("hibernate.cfg.xml")
                .buildSessionFactory();


        List<EditionOracle> editions = oraSession.createQuery("from dk.kb.cop3.backend.migrate.hibernate.EditionOracle").list();
        editions.stream()
                .map(oraEdition -> {
                    System.out.println(oraEdition.getNameEn());
                    return ObjectConverter.convertEdition(oraEdition);})
                .forEach(edition-> {
                    Session psqlSession = psqlSessfac.openSession();
                    Transaction trans = psqlSession.beginTransaction();
                    psqlSession.save(edition);
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

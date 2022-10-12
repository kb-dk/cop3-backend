package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.migrate.hibernate.XLinkOracle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class MigrateXLink {
    public static void main(String[] args) {
        Session oraSession = getOracleSession();

        SessionFactory psqlSessfac = new Configuration().configure("hibernate.cfg.xml")
                .buildSessionFactory();


        List<XLinkOracle> xlinks = oraSession.createQuery("from dk.kb.cop3.backend.migrate.hibernate.XLinkOracle").list();
        xlinks.stream()
                .map(oraXLink -> {
                    return ObjectConverter.convertXLink(oraXLink);})
                .forEach(xlink-> {
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

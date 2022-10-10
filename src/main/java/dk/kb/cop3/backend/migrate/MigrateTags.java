package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.migrate.hibernate.TagOracle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class MigrateTags {
    public static void main(String[] args) {
        Session oraSession = getOracleSession();

        SessionFactory psqlSessfac = new Configuration().configure("hibernate.cfg.xml")
                .buildSessionFactory();


        List<TagOracle> tags = oraSession.createQuery("from dk.kb.cop3.backend.migrate.hibernate.TagOracle").list();
        tags.stream()
                .map(oraTag -> {
                    System.out.println("getXlink_to");
                    System.out.println(oraTag.getXlink_to());
                    return ObjectConverter.convertTag(oraTag);})
                .forEach(tag-> {
                    Session psqlSession = psqlSessfac.openSession();
                    Transaction trans = psqlSession.beginTransaction();
                    psqlSession.save(tag);
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

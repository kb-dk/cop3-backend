package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.migrate.hibernate.EditionOracle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class MigrateEditions {
    public static void main(String[] args) {
        Configuration conf = new Configuration().configure("oracle/hibernate-oracle.cfg.xml");
        SessionFactory sessfac = conf.buildSessionFactory();
        Session session = sessfac.openSession();
        List<EditionOracle> editions = session.createQuery("from dk.kb.cop3.backend.migrate.hibernate.EditionOracle").list();

        for (EditionOracle edition : editions) {
            System.out.println(edition.getId());
        }

    }

}

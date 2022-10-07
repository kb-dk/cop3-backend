package dk.kb.cop3.backend.migrate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import dk.kb.cop3.backend.crud.database.hibernate.Object;

public class TestDBRead {
    public static void main(String[] args) {
        SessionFactory sessfac = new Configuration().configure("hibernate.cfg.xml")
                .buildSessionFactory();
        Session session = sessfac.openSession();

        Object retreivedObject = (Object) session.load(Object.class, "test1234");
        System.out.println(retreivedObject.getPoint());

    }
}

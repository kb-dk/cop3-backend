package dk.kb.cop3.backend.migrate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class CreateDB {
    public static void main(String[] args) {
        SessionFactory sessfac = new Configuration().configure("hibernate.cfg.xml")
                .setProperty(org.hibernate.cfg.Environment.HBM2DDL_AUTO, "create")
                .buildSessionFactory();
    }
}

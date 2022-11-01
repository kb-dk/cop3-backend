package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.database.hibernate.Object;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.constraints.AssertTrue;


/**
 * Created by IntelliJ IDEA.
 * User: dgj
 * Date: 04-08-11
 * Time: 13:13
 * To change this template use File | Settings | File Templates.
 */
public class HibernateMetadataSourceTest{
    private static Logger logger = Logger.getLogger(HibernateMetadataSourceTest.class);
    private static Session session;
    private static HibernateMetadataSource mds;


    @BeforeClass
    public static void initTest() {
        Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
        SessionFactory sessions = cfg.buildSessionFactory();
        session = sessions.openSession();
        mds = new HibernateMetadataSource(session);
    }

    @Test
    public void testSearch() {
        mds = new HibernateMetadataSource(session);
        mds.setSearchterms("id","/images/luftfo/2011/maj/luftfoto/object60810");
        mds.execute();
        final Long numberOfHits = mds.getNumberOfHits();
        Assert.assertTrue(numberOfHits == 1);
        final Object cobject = mds.getAnother();
        Assert.assertTrue(cobject.getLocation().equalsIgnoreCase("Danmark, Fyn, Strandhuse"));
    }


    @AfterClass
    public static void close() {
        session.close();
    }

}

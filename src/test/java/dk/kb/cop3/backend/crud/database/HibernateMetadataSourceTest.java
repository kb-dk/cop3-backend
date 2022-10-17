package dk.kb.cop3.backend.crud.database;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


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
    public void testPaging() {

        mds.setSearchterms("hansen");
        mds.setNumberPerPage(5);
        mds.setOffset(0);
        mds.execute();
        logger.info("Total number of hits "+mds.getNumberOfHits());
        while (mds.hasMore()) {
            dk.kb.cop3.backend.crud.database.hibernate.Object cObject = mds.getAnother();
            logger.info("object: "+cObject.getId() +" - "+cObject.getRandomNumber());
        }
        logger.info("2nd run");
        mds = new HibernateMetadataSource(session);
        mds.setSearchterms("hansen");
        mds.setNumberPerPage(5);
        mds.setOffset(5);
        mds.execute();
        logger.info("Total number of hits "+mds.getNumberOfHits());
        while (mds.hasMore()) {
            dk.kb.cop3.backend.crud.database.hibernate.Object cObject = mds.getAnother();
            logger.info("object: "+cObject.getId()+" - "+cObject.getRandomNumber());
        }
/*         logger.info("3rd run");
        mds = new HibernateMetadataSource(session);
        mds.setSearchterms("hansen");
       mds.setCategory("/images/luftfo/2011/maj/luftfoto/subject205/da/");
        mds.execute();
       logger.info("Total number of hits "+mds.getNumberOfHits());
         while (mds.hasMore()) {
            dk.kb.cop3.backend.crud.database.hibernate.Object cObject = mds.getAnother();
            logger.info("object: "+cObject.getId()+" - "+cObject.getRandomNumber());
        } */
        logger.info("4th run");
        mds = new HibernateMetadataSource(session);
        mds.setSearchterms("id","/images/luftfo/2011/maj/luftfoto/object60810");
        mds.execute();
        logger.info("Total number of hits "+mds.getNumberOfHits());
        while (mds.hasMore()) {
            dk.kb.cop3.backend.crud.database.hibernate.Object cObject = mds.getAnother();
            logger.info("object: "+cObject.getId()+" - "+cObject.getRandomNumber());
        }
    }




    @AfterClass
    public static void close() {
        session.close();
    }

}

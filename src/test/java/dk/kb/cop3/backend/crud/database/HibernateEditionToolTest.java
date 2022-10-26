package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.util.Assert;

import java.math.BigDecimal;

public class HibernateEditionToolTest {

    private final String testEditionId = "edition1234";
    private Session session;

    @Before
    public void createTestEditionIdDb() {
        Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
        SessionFactory sessions = cfg.buildSessionFactory();
        session = sessions.openSession();

        Edition edition = new Edition();
        edition.setId(testEditionId);
        edition.setName("Test edition");
        edition.setNameEn("Test edition");
        edition.setUrlName("urlname");
        edition.setUrlMatrialType("materialtype");
        edition.setUrlPubYear(BigDecimal.valueOf(2022));   
        edition.setUrlPubMonth("month");
        edition.setUrlCollection("collection");
        edition.setCumulusCatalog("catalog");
        edition.setCumulusTopCatagory("topcategory");
        edition.setNormalisationrule("normalizationRule");
        edition.setVisiblePublic('j');

        Transaction transaction = session.beginTransaction();
        session.save(edition);
        transaction.commit();
    }

    @After
    public void deleteTestEditionAndCloseSession() {
        Transaction transaction = session.beginTransaction();
        session.delete(session.get(Edition.class,testEditionId));
        transaction.commit();
        session.close();
    }

    @Test
    public void testGetEdition() {
        HibernateEditionTool hibernateEditionTool = new HibernateEditionTool(session);
        hibernateEditionTool.setEdition(testEditionId);
        Edition ed = hibernateEditionTool.getEdition();
        Assert.equals(ed.getName(),"Test edition");
    }

    @Test
    public void testGetNonExistingEdition() {
        HibernateEditionTool hibernateEditionTool = new HibernateEditionTool(session);
        hibernateEditionTool.setEdition("wrong_edition_id");
        Edition ed = hibernateEditionTool.getEdition();
        Assert.isTrue(ed == null);
    }

    @Test
    public void testUpdateOpml() {
        HibernateEditionTool hibernateEditionTool = new HibernateEditionTool(session);
        String opml = "<opml></opml>";
        hibernateEditionTool.updateEditionOpml(testEditionId,opml);
        hibernateEditionTool.setEdition(testEditionId);
        Edition editionFromDB = hibernateEditionTool.getEdition();
        Assert.equals(opml,editionFromDB.getOpml());
    }

    @Test
    public void findAnEditionList() {
        HibernateEditionTool eds = new HibernateEditionTool(session);
        eds.execute();
        while(eds.hasMore()) {
            eds.getAnother();
        }
    }


}

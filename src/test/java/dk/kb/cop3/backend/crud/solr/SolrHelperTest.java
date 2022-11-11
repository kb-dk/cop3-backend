package dk.kb.cop3.backend.crud.solr;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.crud.util.TestUtil;
import dk.kb.cop3.backend.solr.SolrHelper;
import org.hibernate.Session;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.xpath.XPathExpressionException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class SolrHelperTest {

    @BeforeClass
    public static void initTest() throws FileNotFoundException {
        CopBackendProperties.initialize(new FileInputStream("src/test/resources/cop_config.xml"));
    }

    @Test
    public void testSolrFormulator() {
//
//        String id = "/images/luftfo/2011/maj/luftfoto/object182167";
//
//        Session session = TestUtil.openDatabaseSession();
//        HibernateMetadataWriter mdw = new HibernateMetadataWriter(session);
//        TestUtil.createAndSaveDefaultTestCobject(id, mdw, session);
//
//        SolrInputDocument solrDoc = SolrHelper.copObjectToSolrDoc(id);
//        Assert.equals(id,solrDoc.getFieldValue("id"));
//
//        TestUtil.deleteFromDatabase(Object.class,id,session);
//        TestUtil.closeDatabaseSession(session);
    }

    @Test
    public void testIndexObject() throws FileNotFoundException {
        String id = "/letters/judsam/2011/mar/dsa/object22959";

        Session session = TestUtil.openDatabaseSession();

        SolrHelper solrHelper = new SolrHelper(session);
        solrHelper.updateCobjectInSolr(id);
        session.close();
    }

    @Test
    @Ignore
    public void testSolrizeEditions() throws FileNotFoundException {
        Session session = TestUtil.openDatabaseSession();
        SolrHelper solrHelper = new SolrHelper(session);
        solrHelper.updateEditionsInSolr();
        session.close();
    }

}

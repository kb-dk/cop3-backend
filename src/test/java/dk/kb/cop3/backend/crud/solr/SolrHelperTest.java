package dk.kb.cop3.backend.crud.solr;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.crud.database.HibernateMetadataWriter;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.crud.util.TestUtil;
import dk.kb.cop3.backend.solr.SolrHelper;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.Session;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.util.Assert;

import javax.xml.xpath.XPathExpressionException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SolrHelperTest {

    @BeforeClass
    public static void initTest() throws FileNotFoundException {
        CopBackendProperties.initialize(new FileInputStream("src/test/resources/cop_config.xml"));
    }

    @Test
    public void testSolrFormulator() throws FileNotFoundException, XPathExpressionException {
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
    public void testIndexObject() throws XPathExpressionException, SolrServerException, IOException {
        String id = "/letters/judsam/2011/mar/dsa/object22959";

        Session session = TestUtil.openDatabaseSession();
        HibernateMetadataWriter mdw = new HibernateMetadataWriter(session);

        SolrHelper.indexCopObject(id);
    }

}

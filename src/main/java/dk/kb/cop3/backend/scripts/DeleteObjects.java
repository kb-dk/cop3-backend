package dk.kb.cop3.backend.scripts;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.migrate.TestSearch;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DeleteObjects {
    private static final Logger logger = LoggerFactory.getLogger(TestSearch.class);

    public static void main(String[] args) throws SolrServerException, IOException {
        String mode = args[0];

        if ("object".equals(mode)) {
            deleteObject(args[1]);
            System.exit(0);
        }
        if ("category".equals(mode)) {
            deleteAllObjectsInCategory(args[1]);
            System.exit(0);
        }
        if ("edition".equals(mode)) {
            deleteEdition(args[1]);
            System.exit(0);
        }
    }

    private static void deleteEdition(String arg) {
    }

    private static void deleteAllObjectsInCategory(String arg) {
    }

    private static void deleteObject(String objectId) throws SolrServerException, IOException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.createQuery("delete from category_join where oid").list();
        session.createQuery("delete from object where oid").list();
        String solr_url = CopBackendProperties.getSolrUrl();
        logger.debug("Solr url "+solr_url);
        HttpSolrClient solr = new HttpSolrClient.Builder(solr_url).build();
        solr.deleteById(objectId);
        transaction.commit();
        session.close();
    }


    public static void initialize() {
        String configFile = System.getProperty("dk.kb.cop.propertiesFile");
        try {
            CopBackendProperties.initialize(new FileInputStream(configFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("unable to read properties "+configFile,e);
        }
    }
}

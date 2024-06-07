package dk.kb.cop3.backend.scripts;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.hibernate.Category;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.migrate.TestSearch;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class DeleteObjects {
    private static final Logger logger = LoggerFactory.getLogger(DeleteObjects.class);
    private static HttpSolrClient solr;
    private static Session session;

    public static void main(String[] args) throws SolrServerException, IOException {
        String mode = args[0];

        if (args.length == 2 ) {
            initialize();
            if ("object".equals(mode)) {
                deleteSingleObject(args[1]);
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
        System.out.println("Usage:");
        System.out.println("Delete object:");
        System.out.println("java -Ddk.kb.cop.propertiesFile=/path/to/cop-config.xml -cp cop3-backend-cop-jar.jar dk.kb.cop3.backend.scripts.DeleteObjects object objectID");
        System.out.println("Delete category:");
        System.out.println("java -Ddk.kb.cop.propertiesFile=/path/to/cop-config.xml -cp cop3-backend-cop-jar.jar dk.kb.cop3.backend.scripts.DeleteObjects category categoryID");
        System.out.println("Delete edition:");
        System.out.println("java -Ddk.kb.cop.propertiesFile=/path/to/cop-config.xml -cp cop3-backend-cop-jar.jar dk.kb.cop3.backend.scripts.DeleteObjects edition editionID");
    }

    private static void deleteEdition(String arg) {
        Edition edition = session.get(Edition.class,arg);
        if (edition != null) {
            List<Object> objectsToBeDeleted = session.createQuery("select o from dk.kb.cop3.backend.crud.database.hibernate.Object o where o.edition.id = '"+arg+"'").list();
            objectsToBeDeleted.stream().forEach(DeleteObjects::deleteObject);
        } else {
            System.out.println("Edition not found "+arg);
        }
    }

    private static void deleteAllObjectsInCategory(String arg) {
        Category category = session.get(Category.class,arg+"/da/");
        if (category != null) {
            List<Object> objectsToBeDeleted = session.createQuery("select o from dk.kb.cop3.backend.crud.database.hibernate.Object o join o.categories c where c.id = '"+arg+"/da/'").list();
            objectsToBeDeleted.stream().forEach(DeleteObjects::deleteObject);
        } else {
            System.out.println("category does not exist");
        }
    }

    private static void deleteSingleObject(String objectID) {
        Object objectToBeDeleted = session.get(Object.class,objectID);
        if (objectToBeDeleted != null) {
            deleteObject(objectToBeDeleted);
        } else {
            System.out.println("No object found with id " + objectID);
        }
    }

    private static void deleteObject(Object objectToBeDeleted) {
        try {
            System.out.println("deleting object " + objectToBeDeleted.getId());
            Transaction transaction = session.beginTransaction();
            solr.deleteById(objectToBeDeleted.getId());
            solr.commit();
            session.delete(objectToBeDeleted);
            transaction.commit();
        } catch (SolrServerException | IOException e) {
            System.out.println("Error deleting object "+objectToBeDeleted.getId()+": "+e.getMessage());
        }
    }


    public static void initialize() {
        String configFile = System.getProperty("dk.kb.cop.propertiesFile");
        try {
            CopBackendProperties.initialize(new FileInputStream(configFile));
            String solr_url = CopBackendProperties.getSolrUrl();
            solr = new HttpSolrClient.Builder(solr_url).build();
            session = HibernateUtil.getSessionFactory().openSession();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("unable to read properties "+configFile,e);
        }
    }
}

package dk.kb.cop3.backend.scripts;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.solr.CopSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class UpdateIndex {
    private static final Logger logger = LoggerFactory.getLogger(UpdateIndex.class);

    public static void main(String[] args) {
        initialize();
        String mode = args[0];

        Session session = HibernateUtil.getSessionFactory().openSession();
        CopSolrClient solrHelper = new CopSolrClient(session);
        if ("all".equals(mode) || "edition".equals(mode) || "subject".equals(mode)) {
            int pageSize = 1000;
            int startPage = 0;
            if (args.length > 1) {
                startPage = Integer.parseInt(args[1]);
            }
            List<Object> objects = new ArrayList<>();
            for (int pageNo = startPage; pageNo == startPage || !objects.isEmpty(); pageNo++) {
                logger.info("fetching object from oracle. page: "+pageNo);
                Transaction trans = session.beginTransaction();
                Query query;
                if ("edition".equals(mode)) {
                    String eid = args[2];
                    query = session.createQuery("from Object o where o.edition.id='" + eid + "' order by o.lastModified desc");
                } else if ("subject".equals(mode))  {
                    String cid = args[2];
                    query = session.createQuery("from Object o where o.categories.id='+"+cid+"' order by o.lastModified desc");
                } else {
                    query = session.createQuery("from Object o order by o.lastModified desc");
                }
                logger.info("query "+query.getQueryString());
                objects = query.setMaxResults(pageSize).setFirstResult(pageNo * pageSize).list();
                trans.commit();
                session.clear();
                logger.info("index objects :"+objects.size());
                for(int i = 0; i < objects.size(); i++) {
                    boolean commit = i == objects.size()-1;
                    logger.info("indexing "+objects.get(i).getId());
                    solrHelper.updateCobjectInSolr(objects.get(i).getId(),commit);
                }
            }
        } else {
            try (Stream<String> stream = Files.lines(Paths.get(args[1]))) {
                stream.forEach(id ->{ solrHelper.updateCobjectInSolr(id,true);});
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                session.close();
            }
        }
        session.close();
    }

    private static void initialize() {
        String configFile = System.getProperty("dk.kb.cop.propertiesFile");
        if (configFile == null) {
            configFile = "src/main/resources/cop_config.xml";
        }
        try {
            CopBackendProperties.initialize(new FileInputStream(configFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("unable to read properties "+configFile,e);
        }
    }

}

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

    private static final String USAGE = "usage: UpdateIndex all [startPage]\n"
            + "       UpdateIndex edition <startPage> <editionId>\n"
            + "       UpdateIndex subject <startPage> <categoryId>\n"
            + "       UpdateIndex editions\n"
            + "       UpdateIndex categories <editionId or subjectId>\n"
            + "       UpdateIndex object <copId>\n"
            + "       UpdateIndex file <path to file with one copId per line>";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println(USAGE);
            System.exit(1);
        }
        initialize();
        String mode = args[0];

        boolean ok = true;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            CopSolrClient solrHelper = new CopSolrClient(session);
            switch (mode) {
                case "all":
                case "edition":
                case "subject":
                    indexObjects(session, solrHelper, args);
                    break;
                case "editions":
                    ok = solrHelper.updateEditionsInSolr();
                    break;
                case "categories":
                    // accept a subject id as the /solrizr endpoint does; categories are rebuilt per edition
                    ok = solrHelper.updateCategoriesSolrForEdition(args[1].replaceFirst("/subject[0-9]+/?$", ""));
                    break;
                case "object":
                    ok = solrHelper.updateCobjectInSolr(args[1], true);
                    break;
                default:
                    // "file" and, for backwards compatibility, any unknown mode
                    try (Stream<String> stream = Files.lines(Paths.get(args[1]))) {
                        stream.forEach(id -> solrHelper.updateCobjectInSolr(id, true));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
            }
        } finally {
            session.close();
        }
        logger.info("UpdateIndex " + mode + " done. ok=" + ok);
        if (!ok) {
            System.exit(1);
        }
    }

    private static void indexObjects(Session session, CopSolrClient solrHelper, String[] args) {
        String mode = args[0];
        int pageSize = 1000;
        int startPage = 0;
        if (args.length > 1) {
            startPage = Integer.parseInt(args[1]);
        }
        List<Object> objects = new ArrayList<>();
        for (int pageNo = startPage; pageNo == startPage || !objects.isEmpty(); pageNo++) {
            logger.info("fetching objects from database. page: " + pageNo);
            Transaction trans = session.beginTransaction();
            Query<Object> query;
            if ("edition".equals(mode)) {
                query = session.createQuery("from Object o where o.edition.id = :eid order by o.lastModified desc", Object.class)
                        .setParameter("eid", args[2]);
            } else if ("subject".equals(mode)) {
                query = session.createQuery("select o from Object o join o.categories c where c.id = :cid order by o.lastModified desc", Object.class)
                        .setParameter("cid", args[2]);
            } else {
                query = session.createQuery("from Object o order by o.lastModified desc", Object.class);
            }
            logger.info("query " + query.getQueryString());
            objects = query.setMaxResults(pageSize).setFirstResult(pageNo * pageSize).list();
            trans.commit();
            session.clear();
            logger.info("index objects :" + objects.size());
            for (int i = 0; i < objects.size(); i++) {
                boolean commit = i == objects.size() - 1;
                logger.info("indexing " + objects.get(i).getId());
                solrHelper.updateCobjectInSolr(objects.get(i).getId(), commit);
            }
        }
    }

    private static void initialize() {
        String configFile = System.getProperty("dk.kb.cop.propertiesFile");
        if (configFile == null) {
            configFile = "src/main/resources/cop_config.xml";
        }
        try {
            CopBackendProperties.initialize(new FileInputStream(configFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("unable to read properties " + configFile, e);
        }
    }

}

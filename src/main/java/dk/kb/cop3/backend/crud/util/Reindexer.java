package dk.kb.cop3.backend.crud.util;

import dk.kb.cop3.backend.constants.ConfigurableConstants;
import dk.kb.cop3.backend.crud.database.HibernateMetadataSource;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.MetadataSource;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Properties;

/**
 * Created by dgj on 09-07-2018.
 */
public class Reindexer {

    private static ConfigurableConstants consts = ConfigurableConstants.getInstance();
    Logger logger = configureLog4j();


    private int limit = 10000;
    private String solrizeUrl;
    private String solrBaseUrl;
    private String editionId;
    private String category;

    public static void main(String[] args) {
        Reindexer reindexer = null;
        if (args.length < 3) {
            System.err.println("[editionID|categoryID] solrizeurl solrUrl");
            System.exit(-1);
        }
        if (args[0].contains("subject")) {
            String edition = args[0].substring(0,args[0].indexOf("/subject"));
            reindexer = new Reindexer(args[1],args[2],edition,args[0]);
        } else {
            reindexer = new Reindexer(args[1],args[2],args[0],null);
        }
        reindexer.run();
    }


    public Reindexer(String solrizeUrl, String solrBaseUrl, String editionId, String category) {
        this.solrizeUrl = solrizeUrl;
        System.out.println(solrizeUrl);
        this.solrBaseUrl = solrBaseUrl;
        this.editionId = editionId;
        this.category = category;
    }

    public void run() {
        Session session = null;
        int offset = 0;
        int limit = 10000;
        long numfound  = 0;
        try {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            session = sessionFactory.openSession();
            while (offset == 0 || offset < numfound ) {
                try {
                    session.beginTransaction();
                    MetadataSource mds = new HibernateMetadataSource(session);

                    logger.info("edition is "+editionId);
                    mds.setEdition(editionId);

                    if (category != null) {
                        mds.setCategory(category);
                    }




                    mds.setOffset(offset);
                    mds.setNumberPerPage(limit);

                    logger.info("starting search");
                    mds.execute();
                    logger.info("search ended ");

                    numfound = mds.getNumberOfHits();
                    logger.info("Found "+numfound+" objects");
                    Object cobject;
                    while(mds.hasMore()) {
                        cobject = mds.getAnother();
                        System.out.println("got cobject "+cobject.getId());
                        HttpClient client = new HttpClient();
                        String solrize_url = this.solrizeUrl + cobject.getId();
                        if  (null != this.solrBaseUrl) solrize_url += "?solr_url="+this.solrBaseUrl;
                        logger.info(solrize_url);
                        GetMethod get = new GetMethod(solrize_url);
                        client.executeMethod(get);
                        logger.info("Status "+get.getStatusCode());
                        logger.info("result "+get.getResponseBodyAsString());
                    }
                    session.flush();
                    session.getTransaction().commit();
                    session.clear();
                    offset += limit;
                    mds = null;
                    System.gc();
                } catch (org.hibernate.HibernateException ex) {
                    ex.printStackTrace();
                    Thread.sleep(60000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isConnected()) {
                session.cancelQuery();
                session.close();
            }
        }
    }

    private static Logger configureLog4j() {
        String level = "info";
        if (System.getProperty("loglevel") != null ) level = System.getProperty("loglevel");
        String file = "reindexer.log";
        if (System.getProperty("logfile") != null) file = System.getProperty("logfile");
        Properties props = new Properties();
        props.put("log4j.rootLogger", level+", FILE");
        props.put("log4j.appender.FILE", "org.apache.log4j.DailyRollingFileAppender");
        props.put("log4j.appender.FILE.File",file);
        props.put("log4j.appender.FILE.ImmediateFlush","true");
        props.put("log4j.appender.FILE.Threshold",level);
        props.put("log4j.appender.FILE.Append","true");
        props.put("log4j.appender.FILE.layout", "org.apache.log4j.PatternLayout");
        props.put("log4j.appender.FILE.layout.conversionPattern","[%d{yyyy-MM-dd HH.mm:ss}] %-5p %C{1} %M: %m %n");
        PropertyConfigurator.configure(props);
        Logger logger = Logger.getLogger(SolrizeEdition.class);
        return logger;
    }
}

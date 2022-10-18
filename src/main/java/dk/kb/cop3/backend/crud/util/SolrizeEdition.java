package dk.kb.cop3.backend.crud.util;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.crud.database.HibernateMetadataSource;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.MetadataSource;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.jms.*;
import java.util.Properties;

/**
 * Created by dgj on 23-11-2016.
 */
public class SolrizeEdition {

	private static CopBackendProperties consts = CopBackendProperties.getInstance();

	public static void main(String args[]) {

		Logger logger = configureLog4j();

		if (args.length < 1) {
			System.out.println("Please provide an edition");
			System.exit(1);
		}
		String editionId = args[0];
		String categoryId = null;
		if (args.length > 1) {
			categoryId = args[1];
		}

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

					mds.setEdition(editionId);

					if (categoryId != null) {
						logger.info("setting category ");
						mds.setCategory(categoryId);
					}
					mds.setOffset(offset);
					mds.setNumberPerPage(limit);

					logger.info("staring search");
					mds.execute(); // Her kastes exceptions
					logger.info("search ended ");

					numfound = mds.getNumberOfHits();
					logger.info("Found "+numfound+" objects");
					while(mds.hasMore()) {
						Object cobject = mds.getAnother();
						JMSProducer producer = null;
						boolean done = false;
						while(!done) {
							try {
								String host = System.getProperty("mqHost");
								String queue = System.getProperty("queue");
								if (host == null) host = consts.getConstants().getProperty("cop2.solrizr.queue.host");
								if (queue == null) queue = consts.getConstants().getProperty("cop2.solrizr.queue.update");
								producer = new JMSProducer(host,queue);
								done = producer.sendMessage(cobject.getId());
								Thread.sleep(25);
							} catch (JMSException ex) {
								logger.error("Error sending message ",ex);
								logger.info("waiting to try again ");
								Thread.sleep(60000);
							} finally {
								if (producer != null) {
									producer.shutDownPRoducer();
								}
							}
							logger.info("Object id "+cobject.getId());
						}
					}
					session.flush();
					session.getTransaction().commit();
					session.clear();
					offset += limit;
					mds = null;
					System.gc();
				} catch (org.hibernate.HibernateException ex) {
					logger.error("Error "+ex.getMessage());
					ex.printStackTrace();
					Thread.sleep(60000);
				}
			}
			session.beginTransaction();
			Edition edition = (Edition) session.load(Edition.class, editionId);
			JMSProducer producer = new JMSProducer(
					consts.getConstants().getProperty("cop2.solrizr.queue.host"),
					consts.getConstants().getProperty("cop2.solrizr.queue.update"));
			producer.sendMessage(edition.getId()+"/subject"+edition.getCumulusTopCatagory());
			producer.sendMessage("/editions/");
			producer.shutDownPRoducer();
		} catch (Exception e) { // if getting from DB somehow fails, try to get an older entry from cache
			logger.error("Exception while getting objects from the database "+e.getMessage());
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
		String file = "solrize-edition.log";
		if (System.getProperty("logfile") != null) file = System.getProperty("logfile");
		Properties props = new Properties();
		props.put("log4j.rootLogger", level+", FILE");
		props.put("log4j.appender.FILE", "org.apache.log4j.FileAppender");
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

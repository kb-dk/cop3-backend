package dk.kb.cop3.backend.crud.oai.server.catalog;

import ORG.oclc.oai.server.verb.BadArgumentException;
import ORG.oclc.oai.server.verb.OAIInternalServerError;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: dgj
 * Date: 01-11-11
 * Time: 10:55
 * To change this template use File | Settings | File Templates.
 */
public class HibernateDbLayer implements I_OaiDbLayer {
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(HibernateDbLayer.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String copServerName;

    @Override
    public void init(Properties properties) {
        this.copServerName = (String) properties.get("COP2OAICatalog.COPServerName");
    }

    @Override
     public int countRecordsInSet(String from, String to, String set) throws OAIInternalServerError, BadArgumentException {
        logger.debug("counting records "+from+" "+to+" "+" "+set);
        Session session = null;
         try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            Edition ed = null;
            if (set != null) {
                ed = (Edition) session.get(Edition.class,set);
                if (ed == null) {
                    logger.debug("set not defined "+set);
                    throw new BadArgumentException();
                }
            }

            Criteria crit = session.createCriteria(Object.class);
            if (from != null) crit.add(Restrictions.ge("lastModified",Long.toString(sdf.parse(from).getTime())));
            if (to != null) crit.add(Restrictions.le("lastModified",Long.toString(sdf.parse(to).getTime())));
            if (ed != null) crit.add(Restrictions.eq("edition.id", ed.getId()));
            crit.addOrder(Order.desc("randomNumber"));

            ProjectionList pl = Projections.projectionList();
            pl.add(Projections.rowCount());
            crit.setProjection(pl);
            int result = Integer.parseInt(crit.list().get(0).toString());
            session.getTransaction().commit();
            return result;
        } catch (HibernateException ex) {
            logger.error("Hibernate error getting records "+ex.getMessage(),ex);
            throw new OAIInternalServerError("Error getting records");
        }  catch (ParseException ex) {
             logger.error("Invalid date ",ex);
             throw new OAIInternalServerError("Invalid date");
         }finally {
            if(session.isOpen()){
                session.close();
            }

        }
    }

    @Override
    public List<OaiRecordData> getRecords(String from, String to, String set, int offset, int limit) throws OAIInternalServerError, BadArgumentException {
        List records = new ArrayList();
        logger.debug("getting records "+from+" "+to+" "+" "+offset+" "+limit+" "+set);
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            Edition ed = null;
            if (set != null) {
                ed = (Edition) session.get(Edition.class,set);
                if (ed == null) {
                    logger.debug("set not defined "+set);
                    throw new BadArgumentException();
                }
            }

            Criteria crit = session.createCriteria(Object.class);
            if (from != null) crit.add(Restrictions.ge("lastModified",Long.toString(sdf.parse(from).getTime())));
            if (to != null) crit.add(Restrictions.le("lastModified",Long.toString(sdf.parse(to).getTime())));
            if (ed != null) crit.add(Restrictions.eq("edition.id", ed.getId()));
            crit.addOrder(Order.desc("randomNumber"));
            crit.setMaxResults(limit);
            crit.setFirstResult(offset);

            List<Object> resultSet = crit.list();
            Iterator<Object> resultIterator = resultSet.iterator();

            while (resultIterator.hasNext()) {
                //logger.debug("adding record");
                records.add(createDataRecord(resultIterator.next()));
            }
            session.getTransaction().commit();
        } catch (HibernateException ex) {
            logger.error("Hibernate error getting records "+ex.getMessage());
            throw new OAIInternalServerError("Error getting records");
        }  catch (ParseException ex) {
             logger.error("Invalid date ",ex);
             throw new OAIInternalServerError("Invalid date");
         }finally {
            if(session.isOpen()){
                session.close();
            }

        }

        return records;
    }

    @Override
    public List<OaiRecordData> getRecords(String from, String to, int offset, int limit) throws OAIInternalServerError, BadArgumentException {
        return getRecords(from, to, null, offset, limit);
    }

    @Override
    public OaiRecordData getRecord(String id) throws OAIInternalServerError {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            logger.debug("Getting record "+id);
            Object cObject =
                    (Object) session.get(Object.class,id);
            logger.debug("Got cObject "+cObject);
            session.getTransaction().commit();
            if (cObject != null)
                return createDataRecord(cObject);
            else
                return null;
        }
        catch(HibernateException ex) {
            logger.error("Error getting record "+ex.getMessage());
            throw new OAIInternalServerError("Error getting record ");
        }finally {
            if(session.isOpen()){
                session.close();
            }

        }

    }

    @Override
    public List<Edition> getSets() throws OAIInternalServerError {

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return HibernateUtil.getAllEditions(session);
    }

    public void close() {

    }

    private OaiRecordData createDataRecord(Object cObject) {
        Edition edition = cObject.getEdition();
        String protoUrl =  "http://"+copServerName+"/"+edition.getId()+"/@@@";
        String setInfo = "oai:kb.dk" + edition.getId().replace("/",":");

	java.util.Date             modified  = new java.util.Date(new java.lang.Long(cObject.getLastModified()));
	java.lang.String           pattern   = "yyyy-MM-dd'T'HH:mm:ss'Z'"; 
	// OK, here I'm lying. Our time stamps are not GMT, but who cares?
	// our time stampts will not be compared with any others
	java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(pattern);
	java.lang.String           timeStamp = formatter.format(modified);
	    
        return new OaiRecordData(cObject.getId(),
				 cObject.getMods(),
				 timeStamp,
				 setInfo,
				 protoUrl);
    }
}

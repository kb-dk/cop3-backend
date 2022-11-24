package dk.kb.cop3.backend.crud.oai.server.catalog;

import ORG.oclc.oai.server.verb.BadArgumentException;
import ORG.oclc.oai.server.verb.OAIInternalServerError;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * User: dgj
 * Date: 01-11-11
 * Time: 10:55
 * To change this template use File | Settings | File Templates.
 */
public class HibernateDbLayer implements I_OaiDbLayer {
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(HibernateDbLayer.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String copServerName;

    @Override
    public void init(Properties properties) {
        this.copServerName = (String) properties.get("COP2OAICatalog.COPServerName");
    }

    @Override
    public int countRecordsInSet(String from, String to, String set) throws OAIInternalServerError, BadArgumentException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            String queryString = getQueryStringFromOaiParams(session, from, to, set, true);
            Query query = session.createQuery(queryString);
            return ((Long)query.uniqueResult()).intValue();
        } catch (HibernateException ex) {
            logger.error("Hibernate error getting records " + ex.getMessage(), ex);
            throw new OAIInternalServerError("Error getting records");
        } finally {
            if (transaction.isActive()) {
                transaction.commit();
            }
            if (session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public List<OaiRecordData> getRecords(String from, String to, String set, int offset, int limit) throws OAIInternalServerError, BadArgumentException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            String queryString = getQueryStringFromOaiParams(session, from, to, set, false);
            Query query = session.createQuery(queryString);
            query.setMaxResults(limit);
            query.setFirstResult(offset);
            return (List<OaiRecordData>) query.list().stream()
                    .map(o -> {
                        return createDataRecord((Object) o);
                    })
                    .collect(Collectors.toList());
        } catch (HibernateException ex) {
            logger.error("Hibernate error getting records " + ex.getMessage());
            throw new OAIInternalServerError("Error getting records");
        } finally {
            if (transaction.isActive()) {
                transaction.commit();
            }
            if (session.isOpen()) {
                session.close();
            }
        }
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
            Object cObject = session.get(Object.class, id);
            session.getTransaction().commit();
            if (cObject != null)
                return createDataRecord(cObject);
            else
                return null;
        } catch (HibernateException ex) {
            logger.error("Error getting record " + ex.getMessage());
            throw new OAIInternalServerError("Error getting record ");
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }

    }

    @Override
    public List<Edition> getSets() throws OAIInternalServerError {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            return session.createQuery("SELECT e FROM Edition e", Edition.class).getResultList();
        } catch (HibernateException e) {
            logger.error("Error listing editions for oai", e);
            throw new OAIInternalServerError("Error listing editions for oai");
        } finally {
            if (transaction.isActive()) {
                transaction.commit();
            }
            session.close();
        }
    }

    @Override
    public void close() {
    }

    private String getQueryStringFromOaiParams(Session session, String from, String to, String set, boolean isCountQuery) throws BadArgumentException, OAIInternalServerError {
        StringBuilder query = new StringBuilder(1024);
        if (isCountQuery) {
            query.append("SELECT count(*) from Object o ");
        } else {
            query.append("SELECT o from Object o ");
        }
        if (set != null || from != null || to != null) {
            query.append("WHERE ");
        }
        if (set != null) {
            String editionId = getEditionIdFromSet(session, set);
            query.append("o.edition.id='" + editionId + "' ");
        }
        if (from != null) {
            if (set!=null) {query.append(" AND ");}
            String earliestTimestamp = oaiDateStringToTimestampString(from);
            query.append("o.lastModified>'" + earliestTimestamp + "' ");
        }
        if (to != null) {
            if (from!=null)  {query.append(" AND ");}
            String latestTimestamp = oaiDateStringToTimestampString(to);
            query.append("o.lastModified<'" + latestTimestamp + "' ");
        }
        if (!isCountQuery) {
            query.append("ORDER BY randomNumber");
        }
        return query.toString();
    }

    private String getEditionIdFromSet(Session session, String set) throws BadArgumentException, OAIInternalServerError {
        try {
            Edition ed = session.get(Edition.class, set);
            if (ed == null) {
                logger.error("Unknown set " + set);
                throw new BadArgumentException();
            }
            return ed.getId();
        } catch (HibernateException e) {
            logger.error("OAI provider error getting edition from set", e);
            throw new OAIInternalServerError("Error in set");
        }
    }

    private String oaiDateStringToTimestampString(String date) throws BadArgumentException {
        try {
            return Long.toString(sdf.parse(date).getTime());
        } catch (ParseException e) {
            logger.error("Bad date from oai", e);
            throw new BadArgumentException();
        }
    }

    private OaiRecordData createDataRecord(Object cObject) {
        Edition edition = cObject.getEdition();
        String protoUrl = "http://" + copServerName + "/" + edition.getId() + "/@@@";
        String setInfo = "oai:kb.dk" + edition.getId().replace("/", ":");
        String timeStamp = getOAITimestampFromObject(cObject);
        return new OaiRecordData(cObject.getId(),
                cObject.getMods(),
                timeStamp,
                setInfo,
                protoUrl);
    }

    private static String getOAITimestampFromObject(Object cObject) {
        Date modified = new Date(Long.valueOf(cObject.getLastModified()));
        String pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(modified);
    }
}

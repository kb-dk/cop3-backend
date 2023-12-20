package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.database.hibernate.Object;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * @author: Andreas B. Westh
 * Date: 2/13/12
 * Time: 13:55 PM
 */
public class CorrectnessDAOImpl implements CorrectnessDAO {

    private static final Logger logger = LoggerFactory.getLogger(CorrectnessDAOImpl.class);

    @Override
    public double getCorrectness(String objectUri) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Object copject = (Object) session.get(Object.class, objectUri);

            transaction.commit();
            if (copject != null) {
                BigDecimal bigCorrectness = copject.getCorrectness();
                return bigCorrectness == null ? 0 : bigCorrectness.doubleValue();
            } else {
                logger.error("Could not find a copject with objectUri: " + objectUri);
                throw new Exception("Copject not found");
            }


        } catch (HibernateException e) {
            logger.error("Error while accessing the database", e);
            throw new Exception("Error while accessing the database", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }

        }

    }
}

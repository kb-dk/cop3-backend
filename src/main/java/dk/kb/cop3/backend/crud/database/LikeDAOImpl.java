package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.database.hibernate.Object;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.math.BigInteger;

public class LikeDAOImpl implements LikeDAO {

    private static Logger logger = Logger.getLogger(LikeDAOImpl.class);

    @Override
    public int increaseNumberOfLikesWithOne(String objectUri) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();


            Object copject = (Object) session.get(Object.class, objectUri);

            if (copject != null) {
                BigInteger bigLikes = copject.getLikes();
                bigLikes = bigLikes == null ? BigInteger.ONE : bigLikes.add(BigInteger.ONE);
                copject.setLikes(bigLikes);
                copject.setInterestingess(copject.getInterestingess().add(new BigDecimal("1")));
                session.saveOrUpdate(copject);
                transaction.commit();
                return bigLikes.intValue();
            } else {
                logger.error("Could not find a copject with objectUri: " + objectUri);
                throw new Exception("Object not found and cannot be liked!");
            }
        } catch (HibernateException e) {
            logger.error("Error while accessing the database", e);
            if (transaction != null) {
                transaction.rollback();
            }
            throw new Exception("Error while accessing the database", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public int getNumberOfLikes(String objectUri) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Object copject = (Object) session.get(Object.class, objectUri);

            transaction.commit();
            if (copject != null) {
                BigInteger bigLikes = copject.getLikes();
                return bigLikes == null ? 0 : bigLikes.intValue();
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

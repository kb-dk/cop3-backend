package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.exception.AreaNotFoundException;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * kb.dk
 *
 * @author jatr
 *         Date: 09/10/13
 *         Time: 17:12
 */
public class GeoInfoDaoImpl implements GeoInfoDao {

    private static final Logger LOGGER = Logger.getLogger(GeoInfoDaoImpl.class);

    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Object[] getAreaDetails(double lat, double lng) throws AreaNotFoundException {

        Session session = null;
        Transaction tx;

        Object[] resultObj = null;

        try {
            LOGGER.debug("Getting area with co-ordinates "+lat + ","+ lng);
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            //note that here we reverse the order of the latitude and longitude co-ordinates because
            Query queryResult = session.createSQLQuery("select a.AREA_ID, a.NAME_OF_AREA from AREAS_IN_DK a where " +
                    "MDSYS.SDO_CONTAINS(a.POLYGON_COL, SDO_GEOMETRY(2001, NULL, " +
                    "MDSYS.SDO_POINT_TYPE(" + Double.toString(lng) + "," + Double.toString(lat) + ", NULL), NULL, NULL)) = 'TRUE'");
            LOGGER.debug("Result list size = "+ queryResult.list().size());

            if (queryResult.list().size() == 0){    // NOT IN ANY AREA
                throw new AreaNotFoundException();
            } else if (queryResult.list().size() == 1) {  // ONE RESULT, VERY LIKELY "Danmark"
                resultObj = (Object[]) queryResult.list().get(0);
            } else if(queryResult.list().size() == 2){      // Two results! "Denmark", "Kattegat" or "Denmark", "MORE PRECIESE AREA"
                resultObj = (Object[]) queryResult.list().get(1);   //
            }

            LOGGER.debug("Result obj = "+ Arrays.toString(resultObj));
            tx.commit();
            LOGGER.debug("Successfully performed DB query");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return resultObj;
    }
}

package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.model.StatisticsAllInfoPublic;
import dk.kb.cop3.backend.crud.model.StatisticsForAnEditionPublic;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author: Andreas B. Westh
 * Date: 8/27/12
 * Time: 11:37 AM
 */
public class StatisticsDaoImpl implements StatisticsDao {


    private static Logger logger = Logger.getLogger(StatisticsDaoImpl.class);

    @Override
    public StatisticsForAnEditionPublic getStatistics(String eid, String catId) throws Exception {
        logger.info("eid:" + eid + " catId " + catId);
        Session session = null;
        StatisticsForAnEditionPublic response = new StatisticsForAnEditionPublic();
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();


            Long count = (Long) session.createQuery("select count(*) from Object copject where copject.edition.id like '%" + eid + "%'").iterate().next();
            Long countCorrect = (Long) session.createQuery("select count(*) from Object copject where copject.edition.id like '%" + eid + "%' and copject.correctness > 0.0").iterate().next();
            Long countInCorrect = (Long) session.createQuery("select count(*) from Object copject where copject.edition.id like '%" + eid + "%' and copject.correctness < 1.0").iterate().next();

            if (!catId.equals("")) {
                try {
                    Query queryGetAllinCategory = session.createSQLQuery(
                            "SELECT distinct count(object.id) FROM OBJECT, category_join,category  WHERE category.id= category_join.cid AND category_join.oid = object.id AND category_join.cid LIKE '%" + eid + "%" + catId + "/da%'");
                    BigDecimal resultOfSpecialCategoryQuery = (BigDecimal) queryGetAllinCategory.list().get(0);
                    Query queryGetAllinCategoryCorrect = session.createSQLQuery(
                            "SELECT count(object.id) FROM OBJECT, category_join,category  WHERE category.id= category_join.cid AND category_join.oid = object.id AND category_join.cid LIKE '%" + eid + "%" + catId + "/da%' AND object.correctness>0.0");
                    BigDecimal resultOfSpecialCategoryCorrectPlacedQuery = (BigDecimal) queryGetAllinCategoryCorrect.list().get(0);


                    //  java.math.BigDecimal cannot be cast to java.lang.Long
                    /**  List<dk.kb.cop2.backend.crud.database.hibernate.Object> resultSet = new ArrayList();
                     resultSet = (List<dk.kb.cop2.backend.crud.database.hibernate.Object>) session.createQuery("select * from Object copject where copject.edition.id like '%" + eid + "%' and (select )copject.categories  = copject.id and catId.cid like '%" + catId + "%'").iterate().next();*/
                    logger.debug("resultOfSpecialQuery: " + resultOfSpecialCategoryQuery + " correct placed " + resultOfSpecialCategoryCorrectPlacedQuery);
                    response.setNoOfCobjectsInCategory(resultOfSpecialCategoryQuery.intValue());
                    response.setNoOfCobjectsInCategoryCorrect(resultOfSpecialCategoryCorrectPlacedQuery.intValue());

                    String percent = calculatePercentage(resultOfSpecialCategoryCorrectPlacedQuery, resultOfSpecialCategoryQuery);

                    response.setNoOfCobjectsPlacedCategoryCorrectPercentage(percent);
                } catch (Exception e) {
                    logger.error(e);
                    throw e;
                }
            } else {
                logger.debug("No category provided.");
            }


            response.setNoOfCobjects(count.intValue());
            response.setNoOfCobjectsPlacedCorrect(countCorrect.intValue());
            response.setNoOfCobjects(countInCorrect.intValue());
            logger.debug("countCorrect=" + countCorrect.intValue() + "/count" + count.intValue() + ")*100=" + (countCorrect.intValue() / count.intValue()) * 100.0f);
            String percent = calculatePercentage(countCorrect, count);
            response.setNoOfCobjectsPlacedCorrectPercentage(percent);
            response.setCategoryID(catId);
            response.setEditionID(eid);

            transaction.commit();
            return response;


        } catch (HibernateException e) {
            logger.error("Error while accessing the database", e);
            throw new Exception("Error while accessing the database", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }

        }


    }


    @Override
    public Map getAllStatistics() throws Exception {
        Session session = null;
        StatisticsForAnEditionPublic response = new StatisticsForAnEditionPublic();
        Transaction transaction = null;
        Object[] resultObj = null;
        //List<Object> resultList = new ArrayList<Object>();
        Map resultList = new TreeMap();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            try {
                //Query queryGetAllStats = session.createSQLQuery("SELECT DISTINCT area.name_of_area, area.area_id, cat.category_text,cat.id,  count(obj.id)  FROM object obj, areas_in_dk area, category_join cat_j, category cat WHERE  cat.id= cat_j.cid AND cat_j.oid = obj.id  AND cat_j.cid LIKE '%/da/'  AND sdo_relate(obj.point, area.polygon_col, 'mask=ANYINTERACT') = 'TRUE' group by cat.id, cat.category_text, area.name_of_area, area.area_id order by area.area_id");
                Query queryGetAllStats = session.createSQLQuery("select sum(obj_count) as obj_count, cat_id, category_text, area_id, name_of_area\n" +
                                                                    "from luft_stat$mv\n" +
                                                                    "group by cat_id, category_text, area_id, name_of_area");
                List results = queryGetAllStats.list();

                for (ListIterator iter = results.listIterator(); iter.hasNext(); ) {
                    StatisticsAllInfoPublic obj = new StatisticsAllInfoPublic();
                    Object[] row = (Object[]) iter.next();
                    obj.areaName = String.valueOf(row[4]);
                    obj.areaID = String.valueOf(row[3]);
                    obj.categoryName = String.valueOf(row[2]);
                    obj.categoryID = String.valueOf(row[1]);
                    obj.noOfCobjects = ((BigDecimal) row[0]).intValue();
                    resultList.put(obj.areaID+obj.categoryID, obj);
                }


                Query queryGetAllCorrectTaggedStats = session.createSQLQuery("select obj_count, cat_id, category_text, area_id, name_of_area\n" +
                                                                                "from luft_stat$mv\n" +
                                                                                "where correctness = 1");

                List resultsGeotaggedCorrect = queryGetAllCorrectTaggedStats.list();

                for (ListIterator iter = resultsGeotaggedCorrect.listIterator(); iter.hasNext(); ) {
                    Object[] row = (Object[]) iter.next();

                    StatisticsAllInfoPublic fetchedObj = (StatisticsAllInfoPublic) resultList.get(String.valueOf(row[3])+String.valueOf(row[1]));
                    fetchedObj.areaName = String.valueOf(row[4]);
                    fetchedObj.areaID = String.valueOf(row[3]);  //1
                    fetchedObj.categoryName = String.valueOf(row[2]);
                    fetchedObj.categoryID = String.valueOf(row[1]);  //3
                    fetchedObj.noOfCorrectCobjects = ((BigDecimal) row[0]).intValue();

                    fetchedObj.percentage = calculatePercentage(fetchedObj.noOfCorrectCobjects, fetchedObj.noOfCobjects);
                    resultList.put(fetchedObj.areaID+fetchedObj.categoryID, fetchedObj);
                }

            } catch (Exception e) {
                logger.error(e);
                throw e;
            }


            transaction.commit();
            return resultList; //new ArrayList<String>();


        } catch (HibernateException e) {
            logger.error("Error while accessing the database", e);
            throw new Exception("Error while accessing the database", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }

        }


    }

    public String calculatePercentage(BigDecimal occurences, BigDecimal total) {
        if (occurences.intValue() == 0) {
            return "0%";
        } else {
            String output = (occurences.floatValue() / total.floatValue()) * 100.0f + "     ";
            return output.substring(0, 5).trim() + "%";
        }
    }

    public String calculatePercentage(int occurences, int total) {
        if (occurences < 0) {
            return "0%";
        } else {
            float percentage = (((float)occurences / (float)total) * 100.0f);
            String output =  percentage + "     ";
            return output.substring(0, 5).trim() + "%";
        }
    }


    public String calculatePercentage(Long occurences, Long total) {
        if (occurences.intValue() < 0) {
            return "0%";
        } else {
            String output = (occurences.floatValue() / total.floatValue()) * 100.0f + "     ";
            return output.substring(0, 5).trim() + "%";
        }
    }

    /*

    public static void main(String[] args) {

        StatisticsDaoImpl testDao = new StatisticsDaoImpl();
        System.out.println(testDao.calculatePercentage(200l,1002l));

        System.out.println(testDao.calculatePercentage(new BigDecimal(200), new BigDecimal(1002)));
        System.out.println(testDao.calculatePercentage(new BigDecimal(1), new BigDecimal(10)));
        System.out.println(testDao.calculatePercentage(new BigDecimal(100), new BigDecimal(100)));
        System.out.println(testDao.calculatePercentage(new BigDecimal(500), new BigDecimal(1000)));



    }*/
}

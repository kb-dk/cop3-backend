package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.model.StatisticsAllInfoPublic;
import dk.kb.cop3.backend.crud.model.StatisticsForAnEditionPublic;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author: Andreas B. Westh
 * Date: 8/27/12
 * Time: 11:37 AM
 */
public class StatisticsFacade {


    private final Session session;

    public StatisticsFacade(Session session) {
        this.session = session;
    }

    private static final Logger logger = Logger.getLogger(StatisticsFacade.class);

    public StatisticsForAnEditionPublic getStatsForCategoryAndEdition(String eid, String catId) {
        ensureSessionHasActiveTransaction();
        StatisticsForAnEditionPublic response = new StatisticsForAnEditionPublic();
        addEditionStatsToResponse(eid, response);

        if (!catId.equals("")) {
            addCategoryStatisToResponse(eid, catId, response);
        }
        response.setCategoryID(catId);
        return response;
    }

    private void addCategoryStatisToResponse(String eid, String catId, StatisticsForAnEditionPublic response) {
        BigDecimal resultOfSpecialCategoryQuery  = (BigDecimal) session.createSQLQuery(
                "SELECT distinct count(object.id) FROM OBJECT, category_join,category  WHERE category.id= category_join.cid AND category_join.oid = object.id AND category_join.cid LIKE '%" + eid + "%" + catId + "/da%'")
                .list().get(0);
        BigDecimal resultOfSpecialCategoryCorrectPlacedQuery = (BigDecimal) session.createSQLQuery(
                "SELECT count(object.id) FROM OBJECT, category_join,category  WHERE category.id= category_join.cid AND category_join.oid = object.id AND category_join.cid LIKE '%" + eid + "%" + catId + "/da%' AND object.correctness>0.0")
                .list().get(0);
        response.setNoOfCobjectsInCategory(resultOfSpecialCategoryQuery.intValue());
        response.setNoOfCobjectsInCategoryCorrect(resultOfSpecialCategoryCorrectPlacedQuery.intValue());
        String percent = calculatePercentage(resultOfSpecialCategoryCorrectPlacedQuery, resultOfSpecialCategoryQuery);
        response.setNoOfCobjectsPlacedCategoryCorrectPercentage(percent);
    }

    private void addEditionStatsToResponse(String eid, StatisticsForAnEditionPublic response) {
        Long numberOfObjectsInEdition = (Long) session.createQuery("select count(*) from Object copject where copject.edition.id like '%" + eid + "%'").iterate().next();
        Long numberOfCorrectlyPlacedObjectsInEdition = (Long) session.createQuery("select count(*) from Object copject where copject.edition.id like '%" + eid + "%' and copject.correctness > 0.0").iterate().next();
        Long numberOfIncorrectlyPlacedObjectsInEdition = (Long) session.createQuery("select count(*) from Object copject where copject.edition.id like '%" + eid + "%' and copject.correctness < 1.0").iterate().next();
        response.setNoOfCobjects(numberOfObjectsInEdition.intValue());
        response.setNoOfCobjectsPlacedCorrect(numberOfCorrectlyPlacedObjectsInEdition.intValue());
        response.setNoOfCobjects(numberOfIncorrectlyPlacedObjectsInEdition.intValue());
        String percent = calculatePercentage(numberOfCorrectlyPlacedObjectsInEdition, numberOfObjectsInEdition);
        response.setNoOfCobjectsPlacedCorrectPercentage(percent);
        response.setEditionID(eid);
    }

    public Map getAllStatistics() {
        ensureSessionHasActiveTransaction();
        Map resultList = new TreeMap();

        List results = session.createSQLQuery("select sum(obj_count) as obj_count, cat_id, category_text, area_id, name_of_area\n" +
                "from luft_stat_mv\n" +
                "group by cat_id, category_text, area_id, name_of_area").list();

        for (ListIterator iter = results.listIterator(); iter.hasNext(); ) {
            StatisticsAllInfoPublic obj = new StatisticsAllInfoPublic();
            Object[] row = (Object[]) iter.next();
            obj.areaName = String.valueOf(row[4]);
            obj.areaID = String.valueOf(row[3]);
            obj.categoryName = String.valueOf(row[2]);
            obj.categoryID = String.valueOf(row[1]);
            obj.noOfCobjects = ((BigDecimal) row[0]).intValue();
            resultList.put(obj.areaID + obj.categoryID, obj);
        }
        
        List resultsGeotaggedCorrect = session.createSQLQuery("select obj_count, cat_id, category_text, area_id, name_of_area\n" +
                "from luft_stat_mv\n" +
                "where correctness = 1").list();

        for (Object o : resultsGeotaggedCorrect) {
            Object[] row = (Object[]) o;
            StatisticsAllInfoPublic fetchedObj = (StatisticsAllInfoPublic) resultList.get(String.valueOf(row[3]) + String.valueOf(row[1]));
            fetchedObj.areaName = String.valueOf(row[4]);
            fetchedObj.areaID = String.valueOf(row[3]);  //1
            fetchedObj.categoryName = String.valueOf(row[2]);
            fetchedObj.categoryID = String.valueOf(row[1]);  //3
            fetchedObj.noOfCorrectCobjects = ((BigInteger) row[0]).intValue();
            fetchedObj.percentage = calculatePercentage(fetchedObj.noOfCorrectCobjects, fetchedObj.noOfCobjects);
            resultList.put(fetchedObj.areaID + fetchedObj.categoryID, fetchedObj);
        }
        return resultList;
    }

    private String calculatePercentage(BigDecimal occurences, BigDecimal total) {
        if (occurences.intValue() == 0) {
            return "0%";
        } else {
            String output = (occurences.floatValue() / total.floatValue()) * 100.0f + "     ";
            return output.substring(0, 5).trim() + "%";
        }
    }

    private String calculatePercentage(int occurences, int total) {
        if (occurences < 0) {
            return "0%";
        } else {
            float percentage = (((float) occurences / (float) total) * 100.0f);
            String output = percentage + "     ";
            return output.substring(0, 5).trim() + "%";
        }
    }

    private String calculatePercentage(Long occurences, Long total) {
        if (occurences.intValue() < 0) {
            return "0%";
        } else {
            String output = (occurences.floatValue() / total.floatValue()) * 100.0f + "     ";
            return output.substring(0, 5).trim() + "%";
        }
    }

    private void ensureSessionHasActiveTransaction() {
        if (!session.getTransaction().isActive()) {
            throw new IllegalArgumentException("StatisticsFacade: session has no active transaction");
        }
    }
}

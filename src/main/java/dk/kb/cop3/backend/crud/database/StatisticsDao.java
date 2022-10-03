package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.model.StatisticsForAnEditionPublic;

import java.util.Map;

/**
 * @author: Andreas B. Westh
 * Date: 8/27/12
 * Time: 11:16 AM
 */
public interface StatisticsDao {

    public StatisticsForAnEditionPublic getStatistics(String eid, String category) throws Exception;
    public Map getAllStatistics() throws Exception;

}

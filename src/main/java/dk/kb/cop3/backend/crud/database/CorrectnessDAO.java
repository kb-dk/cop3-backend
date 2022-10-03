package dk.kb.cop3.backend.crud.database;

/**
 * @author: Andreas B. Westh
 * Date: 2/13/12
 * Time: 13:54 PM
 */
public interface CorrectnessDAO {
       double getCorrectness(String objectUri) throws Exception;
}

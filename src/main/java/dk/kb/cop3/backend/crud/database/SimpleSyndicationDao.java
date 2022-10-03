package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.model.SimpleSyndicationResponse;

import java.util.List;

/**
 * @author: Andreas B. Westh
 * Date: 8/29/12
 * Time: 18:10 PM
 */
public interface SimpleSyndicationDao {

    public List<SimpleSyndicationResponse> getCobjects(String eid, String cid);

    public List<SimpleSyndicationResponse> getLatestEditedCobjects(String eid, String cid) throws Exception;
}

package dk.kb.cop3.backend.crud.oai.server.catalog;

/**
 * Created by IntelliJ IDEA.
 * User: dgj
 * Date: 31-10-11
 * Time: 14:09
 * To change this template use File | Settings | File Templates.
 */

import ORG.oclc.oai.server.verb.BadArgumentException;
import ORG.oclc.oai.server.verb.OAIInternalServerError;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;

import java.util.List;

public interface I_OaiDbLayer {
    public void init( java.util.Properties properties );
    public int countRecordsInSet(String from, String to, String set) throws OAIInternalServerError ,BadArgumentException;
    public List<OaiRecordData> getRecords(String from, String to, String set, int offset, int limit) throws OAIInternalServerError, BadArgumentException;
    public List<OaiRecordData> getRecords(String from, String to, int offset, int limit) throws OAIInternalServerError, BadArgumentException;
    public OaiRecordData getRecord( String id ) throws OAIInternalServerError;
    public List<Edition> getSets() throws OAIInternalServerError;
    public void close();
}

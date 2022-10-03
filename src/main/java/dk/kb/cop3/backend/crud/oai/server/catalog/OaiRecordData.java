package dk.kb.cop3.backend.crud.oai.server.catalog;

/**
 * Created by IntelliJ IDEA.
 * User: dgj
 * Date: 31-10-11
 * Time: 14:13
 * To change this template use File | Settings | File Templates.
 */
public class OaiRecordData {
    private String id = "";
    private String record = "";
    private String dateStamp = "";
    private String setInfo = "";
    private String protoUrl = "";

    public OaiRecordData() {

    }

    public OaiRecordData( String id,
            String record,
            String dateStamp,
            String setInfo,
            String protoUrl ) {
        this.id = id;
        this.record = record;
        this.dateStamp = dateStamp;
        this.setInfo = setInfo;
        this.protoUrl = protoUrl;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public void setRecord( String record ) {
        this.record = record;
    }

    public void setDateStamp( String dateStamp ) {
        this.dateStamp = dateStamp;
    }

    public void setSetInfo( String setInfo ) {
        this.setInfo = setInfo;
    }

    public void setProtoUrl( String protoUrl ) {
        this.protoUrl = protoUrl;
    }

    public String getId() {
        return id;
    }

    public String getRecord() {
        return record;
    }

    public String getDateStamp() {
        return dateStamp;
    }

    public String getSetInfo() {
        return setInfo;
    }

    public String getProtoUrl() {
        return protoUrl;
    }
}

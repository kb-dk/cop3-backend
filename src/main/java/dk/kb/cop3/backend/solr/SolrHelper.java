package dk.kb.cop3.backend.solr;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.MetadataSource;
import dk.kb.cop3.backend.crud.database.SolrMetadataSource;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.format.MetadataFormulator;
import dk.kb.cop3.backend.crud.format.SolrMetadataFormulator;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.hibernate.Session;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class SolrHelper {
    private static Logger log = Logger.getLogger(SolrHelper.class);

    private static String getSolrXmlDocument(String id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        MetadataSource source = new SolrMetadataSource(session);
        source.setSearchterms("id", id);
        source.execute();
        MetadataFormulator formulator = new SolrMetadataFormulator();
        formulator.setDataSource(source);
        Document dom = formulator.formulate();
        return getStringFromDocument(dom);
    }

    public static boolean updateCobjectInSolr(String objectId)  {
        boolean updateWentOk;
        String solrUrl = CopBackendProperties.getSolrBaseurl();
        HttpSolrClient client= new HttpSolrClient.Builder(solrUrl).build();
        String xml = getSolrXmlDocument(objectId);
        DirectXmlRequest xmlReq = new DirectXmlRequest("/update", xml);
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("softCommit","true");
        xmlReq.setParams(params);
        try {
            UpdateResponse response = xmlReq.process(client);
            if (response.getStatus() == 0) {
                updateWentOk = true;
            } else {
                updateWentOk = false;
                log.error("Unable to update object in solr "+response);
            }
        } catch (SolrServerException | IOException e) {
            log.error("error updating object in solr ",e);
            updateWentOk = false;
        }
        return updateWentOk;
    }

    public static boolean SolrizeEditions() {
        boolean updateWentOK = false;
        String solrUrl = CopBackendProperties.getSolrBaseurl();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Edition> editions;
        try {
            editions = session.createQuery("from Edition").list();
        } catch (Exception e) {
            log.error("Error getting edition from database",e);
            return false;
        }
        HttpSolrClient client = new HttpSolrClient.Builder(solrUrl).build();
        for (Edition edition : editions) {
            SolrInputDocument doc = getSolrDocFromEdition(edition);
            try {
                UpdateRequest update = new UpdateRequest();
                update.add(doc);
                update.setParam("softCommit","true");
                UpdateResponse response = update.process(client);
                if (response.getStatus() != 0) {
                    updateWentOK = false;
                    log.error("Error sending edition to solr "+response);
                }
            } catch (SolrServerException | IOException e) {
                updateWentOK = false;
                log.error("Error sending edition to solr", e);
            }
        }
        return updateWentOK;
    }

    public static SolrInputDocument getSolrDocFromEdition(Edition edition) {
        SolrInputDocument solrDoc = new SolrInputDocument();
        solrDoc.addField("id",edition.getId());
        solrDoc.addField("name_ssi",edition.getName());
        solrDoc.addField("name_en_ssi",edition.getNameEn());
        solrDoc.addField("top_cat_ssi",edition.getId()+"/subject"+edition.getCumulusTopCatagory());
        solrDoc.addField("description_tdsim",edition.getDescription());
        solrDoc.addField("description_tesim",edition.getDescriptionEn());
        solrDoc.addField("collection_da_ssi",edition.getCollectionDa());
        solrDoc.addField("collection_en_ssi",edition.getCollectionEn());
        solrDoc.addField("department_da_ssi",edition.getDepartmentDa());
        solrDoc.addField("department_en_ssi",edition.getDepartmentEn());
        solrDoc.addField("contact_email_ssi",edition.getContactEmail());
        solrDoc.addField("medium_ssi","editions");
        return solrDoc;
    }
    
    private static String getStringFromDocument(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch(TransformerException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

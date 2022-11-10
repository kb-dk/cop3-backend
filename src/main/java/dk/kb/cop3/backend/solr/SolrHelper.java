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
import org.apache.xalan.processor.TransformerFactoryImpl;
import org.hibernate.Session;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class SolrHelper {
    private static final Logger log = Logger.getLogger(SolrHelper.class);

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

    public static boolean solrizeEditions() {
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
                } else {
                    updateWentOK = true;
                }
            } catch (SolrServerException | IOException e) {
                updateWentOK = false;
                log.error("Error sending edition to solr", e);
            }
        }
        return updateWentOK;
    }

    public static boolean updateCategoriesInEditionInSolr(String editionId, String categoryId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Edition edition = session.get(Edition.class,editionId);
        if (edition != null) {
            String solr_url = CopBackendProperties.getSolrBaseurl();
            String topCategoryId = edition.getCumulusTopCatagory();
            HttpSolrClient solr = new HttpSolrClient.Builder(solr_url).build();
            try {
                UpdateRequest updateRequest = new UpdateRequest();
                updateRequest.deleteByQuery("bread_crumb_ssim:"+topCategoryId);
                updateRequest.deleteById(topCategoryId);
                updateRequest.setParam("softCommit","true");
                UpdateResponse response = updateRequest.process(solr);
                if (response.getStatus() != 0) {
                    log.error("Unable to update categories "+response);
                    return false;
                }
                String solrXml = getSolrXMLfromOPML(edition.getOpml(),editionId,categoryId);
                DirectXmlRequest xmlReq = new DirectXmlRequest("/update", solrXml);
                ModifiableSolrParams params = new ModifiableSolrParams();
                params.set("softCommit","true");
                xmlReq.setParams(params);
                response = xmlReq.process(solr);
                if (response.getStatus() != 0) {
                    log.error("Unable to update categories "+response);
                    return false;
                }
                return true;
            } catch (SolrServerException | IOException e) {
                log.error("Unable to update categories in solr",e);
            }
        }
        return false;
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

    private static String getSolrXMLfromOPML(String opml, String editionId, String catId) {
        try {
            TransformerFactory trans_fact = new TransformerFactoryImpl();
            Transformer transformer =
                    trans_fact.newTransformer(new StreamSource(SolrHelper.class.getResourceAsStream("/opml2solr.xsl")));
            transformer.setParameter("start_node_id", catId.replaceFirst("subject", ""));
            transformer.setParameter("mode", "deep");
            String baseUrl = CopBackendProperties.getCopBackendUrl();
            String guiUri = CopBackendProperties.getGuiUri();
            transformer.setParameter("base_uri", baseUrl + "/navigation" + editionId);
            transformer.setParameter("edition_id", editionId);
            transformer.setParameter("html_base_uri", guiUri + editionId);
            transformer.setParameter("rss_base_uri", baseUrl + "/syndication" + editionId);
            DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dfactory.newDocumentBuilder();
            Document opmlResult = dBuilder.newDocument();
            Document sourceOpml = dBuilder.parse(new InputSource(new StringReader(opml)));
            transformer.transform(new DOMSource(sourceOpml), new DOMResult(opmlResult));
            return getStringFromDocument(opmlResult);
        } catch (ParserConfigurationException | IOException | TransformerException | SAXException e) {
            log.error("Error transforming opml for edition "+editionId,e);
        }
        return null;

    }


}

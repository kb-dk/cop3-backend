package dk.kb.cop3.backend.solr;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.MetadataSource;
import dk.kb.cop3.backend.crud.database.SolrMetadataSource;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.format.MetadataFormulator;
import dk.kb.cop3.backend.crud.format.SolrMetadataFormulator;
import dk.kb.cop3.backend.scripts.UpdateIndex;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.xalan.processor.TransformerFactoryImpl;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class CopSolrClient {
    private static final Logger log = LoggerFactory.getLogger(CopSolrClient.class);

    private final Session session;

    public CopSolrClient(Session session) {
        this.session = session;
    }

    private String getSolrXmlDocument(String id) {
        MetadataSource source = new SolrMetadataSource(session);
        source.setSearchterms("id", id);
        source.execute();
        MetadataFormulator formulator = new SolrMetadataFormulator();
        formulator.setDataSource(source);
        Document dom = formulator.formulate();
        return getStringFromDocument(dom);
    }

    public boolean updateCobjectInSolr(String objectId, boolean doCommit)  {
        boolean updateWentOk = false;
        String solrUrl = CopBackendProperties.getSolrBaseurl();
        HttpSolrClient client= new HttpSolrClient.Builder(solrUrl).build();
        String xml = getSolrXmlDocument(objectId);
        DirectXmlRequest xmlReq = new DirectXmlRequest("/update", xml);
        try {
            UpdateResponse response = xmlReq.process(client);
            if (response.getStatus() == 0) {
                updateWentOk = true;
            } else {
                log.error("Unable to update object in solr "+response);
            }
            if (doCommit) {
                client.commit();
            }
            client.close();
        } catch (IOException | SolrServerException | SolrException e) {
            log.error("error updating object in solr: " + objectId + " ",e);
        }
        return updateWentOk;
    }

    public boolean updateEditionsInSolr() {
        boolean updateWentOK = false;
        String solrUrl = CopBackendProperties.getSolrBaseurl();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Edition> editions;
        try {
            editions = session.createQuery("from Edition where visiblePublic = '1'").list();
        } catch (Exception e) {
            log.error("Error getting edition from database",e);
            return false;
        } finally {
            session.close();
        }
        HttpSolrClient client = new HttpSolrClient.Builder(solrUrl).build();
        UpdateRequest update;
        for (Edition edition : editions) {
            SolrInputDocument doc = getSolrDocFromEdition(edition);
            try {
                update = new UpdateRequest();
                update.add(doc);
                UpdateResponse response = update.process(client);
                if (response.getStatus() != 0) {
                    updateWentOK = false;
                    log.error("Error sending edition to solr "+response);
                } else {
                    updateWentOK = true;
                }
            } catch (SolrServerException | IOException | SolrException e) {
                updateWentOK = false;
                log.error("Error sending edition to solr", e);
            }
        }
        try {
            client.commit();
            client.close();
        } catch (SolrServerException | IOException | SolrException e) {
            updateWentOK = false;
            log.error("Error committing editions solr", e);
        }
        return updateWentOK;
    }

    public  boolean updateCategoriesSolrForEdition(String editionId) {
        Edition edition = session.get(Edition.class,editionId);
        if (edition != null) {
            String solr_url = CopBackendProperties.getSolrBaseurl();
            String topCategoryId = edition.getCumulusTopCatagory();
            HttpSolrClient solr = new HttpSolrClient.Builder(solr_url).build();
            try {
                if (removeCurrentCategoriesFromSolr(topCategoryId, solr)) return false;
                String solrXml = getSolrXMLfromOPML(edition.getOpml(),editionId,topCategoryId);
                return addNewCategoriesToSolr(solr, solrXml);
            } catch (SolrException | IOException | SolrServerException e) {
                log.error("Unable to update categories in solr",e);
            }
        }
        return false;
    }

    private static boolean addNewCategoriesToSolr(HttpSolrClient solr, String solrXml) throws SolrServerException, IOException {
        UpdateResponse response;
        DirectXmlRequest xmlReq = new DirectXmlRequest("/update", solrXml);
        response = xmlReq.process(solr);
        solr.commit();
        solr.close();
        if (response.getStatus() != 0) {
            log.error("Unable to update categories "+response);
            return false;
        }
        return true;
    }

    private static boolean removeCurrentCategoriesFromSolr(String topCategoryId, HttpSolrClient solr) throws SolrServerException, IOException {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.deleteByQuery("bread_crumb_ssim:"+ topCategoryId);
        updateRequest.deleteById(topCategoryId);
        UpdateResponse response = updateRequest.process(solr);
        if (response.getStatus() != 0) {
            log.error("Unable to update categories "+response);
            return true;
        }
        return false;
    }

    public  SolrInputDocument getSolrDocFromEdition(Edition edition) {
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
    
    private String getStringFromDocument(Document doc) {
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

    private String getSolrXMLfromOPML(String opml, String editionId, String catId) {
        try {
            TransformerFactory trans_fact = new TransformerFactoryImpl();
            Transformer transformer =
                    trans_fact.newTransformer(new StreamSource(CopSolrClient.class.getResourceAsStream("/opml2solr.xsl")));
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

    public  void deleteCobjectFromSolr(String objectId) {
        String solrUrl = CopBackendProperties.getSolrBaseurl();
        HttpSolrClient client= new HttpSolrClient.Builder(solrUrl).build();
        try {
            client.deleteById(objectId);
            client.commit();
        } catch (SolrServerException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

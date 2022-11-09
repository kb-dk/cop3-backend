package dk.kb.cop3.backend.solr;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.MetadataSource;
import dk.kb.cop3.backend.crud.database.SolrMetadataSource;
import dk.kb.cop3.backend.crud.format.MetadataFormulator;
import dk.kb.cop3.backend.crud.format.SolrMetadataFormulator;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;
import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringWriter;

public class SolrHelper {

//    public static SolrInputDocument copObjectToSolrDoc(String id) throws XPathExpressionException {
//        Document dom = getSolrXmlDocument(id);
//        XPath xPath = XPathFactory.newInstance().newXPath();
//        NodeList docNodes = ((NodeList)xPath.compile("/add/doc").evaluate(dom, XPathConstants.NODESET));
//        if (docNodes.getLength() > 0) {
//            SolrInputDocument solrDoc = new SolrInputDocument();
//            NodeList fieldNodes = docNodes.item(0).getChildNodes();
//            for(int i=0; i < fieldNodes.getLength(); i++) {
//                Node field = fieldNodes.item(i);
//                if ("field".equals(field.getNodeName())) {
//                    String fieldName = field.getAttributes().getNamedItem("name").getNodeValue();
//                    String fieldValue = field.getTextContent();
//                    solrDoc.addField(fieldName, fieldValue);
//                }
//            }
//            return solrDoc;
//        }
//        return null;
//    }

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

    public static void indexCopObject(String objectId) throws XPathExpressionException, SolrServerException, IOException {
        String solrUrl = CopBackendProperties.getSolrBaseurl();
        HttpSolrClient client= new HttpSolrClient.Builder(solrUrl).build();
        String xml = getSolrXmlDocument(objectId);
        DirectXmlRequest xmlReq = new DirectXmlRequest("/update", xml);
        client.request(xmlReq);
    }

    public static void SolrizeEditions() {
        String solrUrl = CopBackendProperties.getSolrBaseurl();



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

package dk.kb.cop3.backend.crud.format;

import dk.kb.cop3.backend.constants.CopBackendProperties;

import dk.kb.cop3.backend.crud.util.TransformErrorListener;
import org.apache.xalan.trace.*;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.serializer.SerializerTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import java.util.TooManyListenersException;

/**
 * Reads a MetadataSource returning mods data, creating SOLR an add document
 *
 * @author Sigfrid Lundberg (slu@kb.dk)
 * @version $Revision$, last modified $Date$ by $Author$
 *          $Id$
 */

public class SolrMetadataFormulator extends MetadataFormulator {

    private String copBaseUrl = CopBackendProperties.getCopBackendUrl();
    private String baseUrl = copBaseUrl + "/syndication";
    private String copIntBaseUrl = CopBackendProperties.getCopBackendInternalBaseurl();
    private String internalBaseUrl = copIntBaseUrl + "/syndication";

    protected Logger logger = LoggerFactory.getLogger(SolrMetadataFormulator.class.getPackage().getName());

    private java.lang.String format = "mods";
    private java.lang.String xslt = "/build_mods.xsl";
    private java.lang.String template = "/template_mods_collection.xml";

    javax.xml.transform.TransformerFactory trans_fact = new org.apache.xalan.processor.TransformerFactoryImpl();

    public SolrMetadataFormulator() {
    }

    public Document formulate() {
        Document solr_doc = (org.w3c.dom.Document) null;

        Document src = this.formulate(this.format, this.template, this.xslt);
        DOMSource dom_source = new DOMSource(src);
        DOMResult dom_result = new DOMResult();

        Transformer mods2solr = null;
        try {
            mods2solr = this.trInit("/mods_solrizr.xsl");
        } catch (TooManyListenersException e) {
            throw new RuntimeException(e);
        }


        mods2solr.setErrorListener(new TransformErrorListener("mods_solrizr.xsl"));
        mods2solr.setParameter("metadata_context", copBaseUrl);
        mods2solr.setParameter("url_prefix", baseUrl);
        mods2solr.setParameter("internal_url_prefix", internalBaseUrl);
        mods2solr.setParameter("raw_mods", this.currentRawMods);
        mods2solr.setParameter("content_context", CopBackendProperties.getGuiUri());
        try {
            mods2solr.transform(dom_source,dom_result);
        } catch (javax.xml.transform.TransformerException trnsFrmPrblm) {
            logger.warn("transformer problem "+trnsFrmPrblm.getMessage());
        }
        solr_doc = (Document) dom_result.getNode();

        return solr_doc;
    }

    private javax.xml.transform.Transformer trInit(java.lang.String xsl) throws TooManyListenersException {

        String xfile = xsl;
        // getResource() returns a file URL
        javax.xml.transform.Transformer transform = null;

        try {
            // this.getClass().getResource(xfile).toString() the URL object as a systemID
            javax.xml.transform.stream.StreamSource source =
                    new javax.xml.transform.stream.StreamSource(this.getClass().getResource(xfile).toString());

            transform =
                    trans_fact.newTransformer(source);
        } catch (javax.xml.transform.TransformerConfigurationException transformerPrblm) {
            logger.debug("problem might be: " + transformerPrblm.getMessage());
            transformerPrblm.printStackTrace();
        }
        if (transform == null) {
            logger.debug("transform for " + xfile + " didn't work as expected");
        }

        // Enable tracing
        if (CopBackendProperties.getTraceTransforms()) {
            TraceListener traceListener = new TraceListener() {
                @Override
                public void trace(TracerEvent ev) {
                    logger.info("==== XSLT Execution Trace ====");
                    logger.info("XSLT Node: " + ev.m_styleNode.getNodeName());
                    logger.info("XML Source Node: " +
                            (ev.m_sourceNode != null ? ev.m_sourceNode.getNodeName() : "N/A"));
                    logger.info("XML Source Node attributes: " +
                            (ev.m_sourceNode != null && ev.m_sourceNode.getAttributes() != null ? ev.m_sourceNode.getAttributes().getLength() : "N/A"));
                    logger.info("Line Number: " + ev.m_styleNode.getLineNumber());
                    logger.info("Column Number: " + ev.m_styleNode.getColumnNumber());
                    logger.info("=============================");
                }

                @Override
                public void selected(SelectionEvent ev) throws TransformerException {
                    logger.info("XSLT Selection Event:");
                    logger.info("Expression: " + ev.m_xpath.getPatternString());
                }

                @Override
                public void generated(GenerateEvent event) {
                    switch (event.m_eventtype) {
                        case SerializerTrace.EVENTTYPE_STARTDOCUMENT:
                            logger.info("Start of document");
                            break;

                        case SerializerTrace.EVENTTYPE_ENDDOCUMENT:
                            logger.info("End of document");
                            break;

                        case SerializerTrace.EVENTTYPE_STARTELEMENT:
                            logger.info("Start Element: <" + event.m_name + ">");
                            break;

                        case SerializerTrace.EVENTTYPE_ENDELEMENT:
                            logger.info("End Element: </" + event.m_name + ">");
                            break;

                        case SerializerTrace.EVENTTYPE_CHARACTERS:
                            if (event.m_characters != null) {
                                logger.info("Text Node: " + new String(event.m_characters, event.m_start, event.m_length));
                            }
                            break;

                        case SerializerTrace.EVENTTYPE_CDATA:
                            if (event.m_characters != null) {
                                logger.info("CDATA: " + new String(event.m_characters, event.m_start, event.m_length));
                            }
                            break;

                        case SerializerTrace.EVENTTYPE_COMMENT:
                            if (event.m_characters != null) {
                                logger.info("Comment: " + new String(event.m_characters, event.m_start, event.m_length));
                            }
                            break;

                        default:
                            logger.info("Unhandled GenerateEvent type: " + event.m_eventtype);
                    }
                }

            };
            ((TransformerImpl) transform).getTraceManager().addTraceListener(traceListener);
        }

        return transform;
    }

    @Override
    protected Element insertElementAt(Document resultSet, Element root) {
        return root;
    }


}

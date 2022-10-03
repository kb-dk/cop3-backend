package dk.kb.cop3.backend.crud.oai.server.crosswalk;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import dk.kb.cop3.backend.crud.oai.server.catalog.OaiRecordData;

/**
 * Contains code that is used by both oai_dc and mods oai providers
 * 
 */
public class MetadataCreator {
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MetadataCreator.class);
    private static TransformerFactory tf = TransformerFactory.newInstance();
    private static DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
    private String xslt_file = "";
    private String url_prefix;

    public MetadataCreator() {
        logger.info("INIT MetadataCreator");

    }

    public java.lang.String transform(Object o,
				      String format) throws  CannotDisseminateFormatException {
        try {
	    logger.debug("instantiating transform...");
	    LocalUriResolver fileResolver = new LocalUriResolver();
	    tf.setURIResolver(fileResolver);	    
            Transformer transformer = 
		tf.newTransformer(new StreamSource(this.getClass().getResourceAsStream(this.xslt_file)));
            if (transformer == null) {
                logger.error("Transformer is null");
                throw new CannotDisseminateFormatException(format);
            }
            Reader reader = new StringReader(((OaiRecordData) o).getRecord());
            dFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
            Document mods = dBuilder.parse(new InputSource(reader));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            transformer.setOutputProperty("omit-xml-declaration","yes");
            // For some reason we need to explicitly tell the transformer to use UTF-8
            transformer.setOutputProperty("encoding","UTF-8");
            transformer.setParameter("url_prefix",url_prefix);
	    logger.debug("Starting to transform...");
            transformer.transform(
                    new DOMSource(mods),
                    new StreamResult(out)
            );

            return out.toString("UTF-8");
        } catch (TransformerConfigurationException ex ) {
            logger.error("Transformer configuraton exception " + ex.getMessage() );
            throw new CannotDisseminateFormatException(format);
        } catch (TransformerException ex) {
            logger.error("Transformer exception              " + ex.getMessage() );
            throw new CannotDisseminateFormatException(format);
        } catch (ParserConfigurationException ex) {
             logger.error("Parser Configure Exception        " + ex.getMessage() );
            throw new CannotDisseminateFormatException(format);
        } catch (SAXException ex) {
            logger.error("XML Parser Exception               " + ex.getMessage() );
            throw new CannotDisseminateFormatException(format);
        } catch (IOException ex) {
            logger.error("XML Parser io Exception            " + ex.getMessage() );
            throw new CannotDisseminateFormatException(format);
        }
    }

    public void setXsltFile (String xsltFile  ) {
	logger.info("xsltFile is " + xsltFile );
	this.xslt_file  = xsltFile;
    }

    public void setUrlPrefix(String urlPrefix ) {
	logger.info("urlPrefix " + urlPrefix );
	this.url_prefix = urlPrefix;
    }

}

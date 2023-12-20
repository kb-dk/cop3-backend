package dk.kb.cop3.backend.crud.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.stream.StreamSource;

public class Reformulator {

    private javax.xml.parsers.DocumentBuilderFactory dfactory =
            javax.xml.parsers.DocumentBuilderFactory.newInstance();

    private static final Logger logger = LoggerFactory.getLogger(Reformulator.class.getPackage().getName());

    private javax.xml.transform.TransformerFactory transFact
            = new org.apache.xalan.processor.TransformerFactoryImpl();

    private static final java.util.Map<String, String> XSL_FILES =
	new java.util.HashMap<String, String>() {
	{
	    put("title",        "/reformulate-title.xsl");
	    put("note",         "/reformulate-note.xsl");
	    put("person",       "/reformulate-person.xsl");
	    put("building",     "/reformulate-hierarchical-geographic.xsl");
	    put("area",         "/reformulate-hierarchical-geographic.xsl");
	    put("city",         "/reformulate-hierarchical-geographic.xsl");
	    put("parish",       "/reformulate-hierarchical-geographic.xsl");
	    put("street",       "/reformulate-hierarchical-geographic.xsl");
	    put("housenumber",  "/reformulate-hierarchical-geographic.xsl");
	    put("zipcode",      "/reformulate-hierarchical-geographic.xsl");
	    put("cadastre",     "/reformulate-hierarchical-geographic.xsl");
	    put("location",     "/reformulate-location.xsl");
	    put("latlng",       "/reformulate-latlng.xsl");
	    put("pdfidentifier","/reformulate-pdfidentifier.xsl");
	    put("orientation",  "/reformulate-orientation.xsl");
	    put("creator",      "/reformulate-creator.xsl");
	    put("dateCreated",  "/reformulate-dateCreated.xsl");
	    put("genre",        "/reformulate-genre.xsl");
        put("identifier",   "/reformulate-identifier.xsl");
	}
    };

    private org.w3c.dom.Document record = null;
    private java.util.Map<String, javax.xml.transform.Transformer> transformers =
            new java.util.HashMap<String, javax.xml.transform.Transformer>();


    public Reformulator(String mods) {
        java.util.Iterator<String> keys = XSL_FILES.keySet().iterator();
        while (keys.hasNext()) {
            transformers.put(keys.next(), null);
        }

        try {
            dfactory.setNamespaceAware(true);
            javax.xml.parsers.DocumentBuilder dBuilder = dfactory.newDocumentBuilder();
            java.io.Reader reader = new java.io.StringReader(mods);
            record = dBuilder.parse(new org.xml.sax.InputSource(reader));
        } catch (org.xml.sax.SAXException flawedMods) {
            logger.error(flawedMods.getMessage());
        } catch (java.io.IOException ioPrblms) {
            logger.error(ioPrblms.getMessage());
        } catch (javax.xml.parsers.ParserConfigurationException parserPrblm) {
            logger.error(parserPrblm.getMessage());
        }

    }

    public void changeField(String field, String value) {
        changeField(field,value,null);
    }

    public void changeField(String field, String value, String displayLabel) {
        //	if(transformers.get(field)==null) {
        this.initializeTransform(field);
        //	}
        javax.xml.transform.dom.DOMSource dom_source = new javax.xml.transform.dom.DOMSource(this.record);
        javax.xml.transform.dom.DOMResult dom_result = new javax.xml.transform.dom.DOMResult();

        try {
            logger.debug("changing field: "+field+" value="+value);
            transformers.get(field).setParameter("variable", field);
            transformers.get(field).setParameter("value", value);
            if (displayLabel != null) transformers.get(field).setParameter("displayLabel", displayLabel);
            transformers.get(field).transform(dom_source, dom_result);
        } catch (javax.xml.transform.TransformerException trnsFrmPrblm) {
            logger.error(trnsFrmPrblm.getMessage());
        }

        this.record = (org.w3c.dom.Document) dom_result.getNode();

    }

    public String commitChanges() {
        return this.serialize(this.record);
    }

    private String serialize(org.w3c.dom.Document doc) {
        java.lang.String str = "";

        try {
            org.w3c.dom.bootstrap.DOMImplementationRegistry registry =
                    org.w3c.dom.bootstrap.DOMImplementationRegistry.newInstance();

            org.w3c.dom.ls.DOMImplementationLS impl =
                    (org.w3c.dom.ls.DOMImplementationLS) registry.getDOMImplementation("LS");

            org.w3c.dom.ls.LSSerializer writer = impl.createLSSerializer();
            str = writer.writeToString(doc);

        } catch (java.lang.ClassNotFoundException classNotFound) {
            str = "";
            //            logger.error(classNotFound.getMessage());
        } catch (java.lang.InstantiationException instantiationPrblm) {
            str = "";
            //            logger.error(instantiationPrblm.getMessage());
        } catch (java.lang.IllegalAccessException accessPrblm) {
            str = "";
            //            logger.error(accessPrblm.getMessage());
        }
        return str;
    }

    void initializeTransform(String field) {
        String xsl = this.XSL_FILES.get(field);
        logger.debug("field is "+field+" sheet is " + xsl);
        try {
            final StreamSource streamSource = new StreamSource(this.getClass().getResourceAsStream(xsl));
            this.transformers.put(field, transFact.newTransformer(streamSource));
        } catch (javax.xml.transform.TransformerConfigurationException transformerPrblm) {
            logger.warn("problem might be: " + transformerPrblm.getMessage());
            transformerPrblm.printStackTrace();
        }
    }


}

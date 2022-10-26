package dk.kb.cop3.backend.crud.format;

import dk.kb.cop3.backend.crud.database.HibernateEditionTool;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;

/**
 * Reads a MetadataSource returning a hibernate bean and creates a suitable xml object
 * @author Sigfrid Lundberg (slu@kb.dk)
 * @version $Revision: 1090 $, last modified $Date: 2011-04-26 15:56:35 +0200 (Tue, 26 Apr 2011) $ by $Author: slu $
 * $Id: ModsMetadataFormulator.java 1090 2011-04-26 13:56:35Z slu $
 */

public class DirectoryFormulator extends MetadataFormulator {

    private java.lang.String      xslt     = "/build_directory_opml.xsl";
    private java.lang.String      template = "/template_directory_opml.xml";
    private java.lang.String      format   = "opml";
    private java.lang.String      language = "da";
    private org.hibernate.Session session  = null;

    private HibernateEditionTool source = null;

    private javax.xml.parsers.DocumentBuilderFactory dfactory =
            javax.xml.parsers.DocumentBuilderFactory.newInstance();

    public DirectoryFormulator() {
    }

    public void setDataSource(HibernateEditionTool source) {
	this.source = source;
	this.source.execute();
    }

    public void setSession(org.hibernate.Session session) {
	this.session = session;
    }

    public void setLanguage(java.lang.String language) {
	this.language = language;
    }

    public java.lang.String getLanguage() {
	return this.language;
    }

    public org.w3c.dom.Document formulate(String format,
					  String template,
					  String xsl) {

	logger.debug("I'm going to use " + xsl + " on " + template);

	org.w3c.dom.Document result = (org.w3c.dom.Document)null;
	javax.xml.parsers.DocumentBuilder dBuilder = null;
        try {
	    dfactory.setNamespaceAware(true);
	    dBuilder = dfactory.newDocumentBuilder();
            java.io.InputStream in = this.getClass().getResourceAsStream(template);
            result = dBuilder.parse(in);
        } catch (javax.xml.parsers.ParserConfigurationException parserPrblm) {
            logger.warn(parserPrblm.getMessage());
        } catch (org.xml.sax.SAXException xmlPrblm) {
            logger.error(xmlPrblm.getMessage());
        } catch (java.io.IOException ioPrblm) {
            logger.error(ioPrblm.getMessage());
        }

	org.w3c.dom.Element root = result.getDocumentElement();
	org.w3c.dom.Element insert_here = null;

	insert_here = result.createElement("body");
	root.appendChild(insert_here);
	javax.xml.transform.Transformer transformer = null;
        try {
            logger.debug("xsl path is :" + this.getClass().getResourceAsStream(xsl));
	    javax.xml.transform.stream.StreamSource streamSource =
		new javax.xml.transform.stream.StreamSource(this.getClass().getResourceAsStream(xsl));
            transformer = trans_fact.newTransformer(streamSource);

            logger.debug("Transformer fra xsl " + xsl + " transformer.toString() " + transformer.toString());

        } catch (javax.xml.transform.TransformerConfigurationException transformerPrblm) {
            logger.warn("problem might be: " + transformerPrblm.getMessage());
            transformerPrblm.printStackTrace();
        }

	java.lang.String opensearch_relevant ="xxx mods atom rss kml";

	if(opensearch_relevant.indexOf(format)>0 ) {
	    /*this.insertOpenSearchStuff(resultSet,
				       insert_here,
				       data_source.getOffset() + "",
				       data_source.getNumberPerPage() + "",
				       hits + "",
				       editionId);*/
	}


	while (this.source.hasMore()) {

	    try {
		logger.debug("In while because more editions");
		Edition edition = this.source.getAnother();
		if(edition == null) {
		    return result;
		}
		java.lang.String subject_uri = edition.getId();
		transformer.clearParameters();
		transformer.setParameter( "text", edition.getName() );
		transformer.setParameter( "html_uri",          subject_uri );
		java.lang.String node_id = subject_uri;
		node_id = node_id.replaceFirst("/[^/]+/[^/]+/","");
		node_id = node_id.replaceAll("/","-");
		transformer.setParameter( "node_id", node_id);

		if(edition.getDescription() != null) {
		    transformer.setParameter( "description",  edition.getDescription() );
		} else {
		    transformer.setParameter( "description",  "" );
		}
	
		java.lang.String subjectId = edition.getCumulusTopCatagory();
		java.lang.String retrieve_subject = subject_uri + "/subject" + subjectId+"/da/";
		logger.debug(".. subject uri " + subject_uri);
		logger.debug(".. subject " + subjectId);
		logger.debug(".. retrieve subject " + retrieve_subject);

		javax.xml.transform.dom.DOMSource source_dom = null;
		source_dom = new javax.xml.transform.dom.DOMSource(result);

		logger.debug(".. about to create a dom_result");
		javax.xml.transform.dom.DOMResult dom_result =
		    new javax.xml.transform.dom.DOMResult(); 
		if(source_dom == null) {
		    logger.warn(".. source_dom is null");
		} else {
		    logger.debug(".. source_dom is OK");
		}
		transformer.transform(source_dom,dom_result);
		result = (org.w3c.dom.Document)dom_result.getNode();
		logger.debug(".. tranforme OK");

	    } catch(javax.xml.transform.TransformerException trnsFrmPrblm) {
		logger.warn(trnsFrmPrblm.getMessage());
	    }

	}

	return result;
    }

    public void setFormat(String format) {
	this.format = format;
    }

    public org.w3c.dom.Document formulate() {
	return this.formulate(this.format,
			      this.template,
			      this.xslt);
    }


}

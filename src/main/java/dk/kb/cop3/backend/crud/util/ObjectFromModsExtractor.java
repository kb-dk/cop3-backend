package dk.kb.cop3.backend.crud.util;

import dk.kb.cop3.backend.constants.Types;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.hibernate.Category;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.database.hibernate.Type;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import org.apache.log4j.Logger;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by IntelliJ IDEA.
 * User: jac
 * Date: 14-04-11
 * Time: 12:46
 * Implemented as a singleton
 */
public class ObjectFromModsExtractor {
    /**
     * The logger called logger
     */
    private static Logger logger = Logger.getLogger(ObjectFromModsExtractor.class);

    private static XPathFactory factory = XPathFactory.newInstance();
    private static XPath xPath = factory.newXPath();

    private static final String CREATOR_XPATH = "/mods/name[@type='personal']/namePart";
    private static final String GEOGRAPHIC_XPATH = "/mods/subject/geographic";
    private static final String PERSON_XPATH = "/mods/subject/name/namePart";
    private static final String EXTENSION_XPATH = "/mods/extension/div";
    private static final String BUILDING_XPATH = "/mods/subject/hierarchicalGeographic/area";
    private static final String MODIFIED_BY_XPATH = "/mods/name[@type='cumulus']/namePart";
    public static final String ID_XPATH = "/mods/recordInfo/recordIdentifier";
    private static final String DATE_NOT_BEFORE_XPATH = "/mods/originInfo/dateCreated";
    private static final String DATE_NOT_AFTER_XPATH = "/mods/originInfo/dateCreated";
    private static final String TYPE_XPATH = "/mods/genre";
    public static final String TITLE_XPATH = "/mods/titleInfo/title";
    private static final String GEO_LAT_LONG_XPATH = "/mods/subject/cartographics/coordinates";


    private static DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder builder = null;

    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private DateFormat dfYearOnly = new SimpleDateFormat("yyyy", Locale.US);


    private static ObjectFromModsExtractor ourInstance = new ObjectFromModsExtractor();

    public static ObjectFromModsExtractor getInstance() {
        return ourInstance;
    }

    public ObjectFromModsExtractor() {
        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error("Exception while parsing the configuration: " + e.getMessage());
        }
    }

    /**
     * Extract relevant information from MODS, and apply
     * to fields. Used at creation of Copjects.
     * <p/>
     *
     * @param copject
     * @param modsString
     * @param session
     * @return
     */
    public Object extractFromMods(Object copject, String modsString, Session session) {
        return extractFromMods(copject, modsString, new BigDecimal("1"), session);
    }


    public Object extractFromMods(Object copject, String modsString, BigDecimal version, Session session) {
        if (copject == null) {
            throw new IllegalArgumentException("copject cannot be null");
        }

        Document modsDocument = parseModsString(modsString);
        Transaction transaction = session.beginTransaction();
        try {
            populateCopjectWithSimpleFields(copject, version, modsDocument);
            setLatLng(copject, modsDocument);
            setDates(copject, modsDocument);
            setTypeAndEdition(copject, session, modsDocument);
            setCategories(copject, session, modsDocument);
            copject.setDeleted('n');
            copject.setLastModified(getCurrentTimestamp());
            copject.setMods(modsString);
        } catch (XPathExpressionException e) {
            logger.error("Error evaluating XPath. Error is: " + e.getMessage());
        } catch (HibernateException e) {
            logger.error("Database error while extracting cobject from mods",e);
        } finally {
            if(transaction.isActive()) {
                transaction.commit();
            }
        }
        return copject;
    }

    private static String getCurrentTimestamp() {
        return "" + new Date().getTime();
    }

    private void populateCopjectWithSimpleFields(Object copject, BigDecimal version, Document modsDocument) throws XPathExpressionException {
        String creator = extract(CREATOR_XPATH, modsDocument);
        String geographic = extract(GEOGRAPHIC_XPATH, modsDocument);
        String id = extract(ID_XPATH, modsDocument);
        String person = extract(PERSON_XPATH, modsDocument);
        String building = extract(BUILDING_XPATH, modsDocument);
        String modifiedBy = extract(MODIFIED_BY_XPATH, modsDocument);
        String title = extract(TITLE_XPATH, modsDocument);

        if (creator != null) {
            copject.setCreator(creator);
        }
        if (title != null && !title.isEmpty()) {
            copject.setTitle(title);
        }
        if (title.isEmpty() && building != null && !building.isEmpty()) {  // no title provided,  BUT building is present,create a new title
            logger.warn("No title provided in MODS record, auto generating a title: " + building);
            copject.setTitle(building);
        }
        if (building != null) {
            copject.setBuilding(building);
        }
        if (geographic != null) {
            copject.setLocation(geographic);
        }
        if (person != null) {
            copject.setPerson(person);
        }
        copject.setId(id);
        copject.setLastModifiedBy(modifiedBy);
        copject.setObjVersion(version);
    }

    private String getEditionString(Object copject) {
        return copject.getId().substring(0, copject.getId().indexOf("/object"));

    }

    private void setDates(Object copject, Document modsDocument) throws XPathExpressionException {
        String extractedDate = extract(DATE_NOT_AFTER_XPATH, modsDocument);
        Date dates[] = extractInDifferentFormats(extractedDate);
        try {
            if (dates[1] != null) {
                copject.setNotAfter(dates[1]);
            }
            if (dates[0] != null) {
                copject.setNotBefore(dates[0]);
            }
        } catch (NullPointerException e) {
            logger.warn("No correct date could be entered for this copject id: " + copject.getId());
        }
    }

    private void setLatLng(Object copject, Document modsDocument) throws XPathExpressionException {
        String latlng = extract(GEO_LAT_LONG_XPATH, modsDocument);
        if(latlng != null && !latlng.isEmpty()){
            String lat = latlng.split(",")[0];
            String lon = latlng.split(",")[1];
            GeometryFactory geoFactory = JTSFactoryFinder.getGeometryFactory();
            copject.setPoint(geoFactory.createPoint(new Coordinate(Double.valueOf(lat),Double.valueOf(lon))));

            if(copject.getCorrectness() == null){
                copject.setCorrectness(new BigDecimal(0)); // maybe we need this.
            }
        }
    }

    private void setTypeAndEdition(Object copject, Session session, Document modsDocument) throws XPathExpressionException {
        String editionString = getEditionString(copject);
        String typeString = extract(TYPE_XPATH, modsDocument);
        Edition edition = HibernateUtil.getEditionById(session, editionString);
        copject.setEdition(edition);
        if (typeString.isEmpty()) {
            typeString = "Unknown";
        }
        Type type = HibernateUtil.getTypeById(session, Types.getTypeByName(typeString));
        copject.setType(type);
    }

    private void setCategories(Object copject, Session session, Document modsDocument) {
        String editionString = getEditionString(copject);
        // Extract Subjects, and create category objects
        NodeList subjectNodes = null;
        try {
            subjectNodes = (NodeList) xPath.evaluate(EXTENSION_XPATH, (java.lang.Object) modsDocument, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }

        removeAllCaterories(copject);
        insertPresentCategories(copject, session, editionString, subjectNodes);
    }

    private void insertPresentCategories(Object copject, Session session, String editionString, NodeList subjectNodes) {
        try {
            for (int j = 0; j < subjectNodes.getLength(); j++) {
                NodeList subjectsList = subjectNodes.item(j).getChildNodes();
                for (int i = 0; i < subjectsList.getLength(); i++) {
                    Node subject = subjectsList.item(i);
                    if (subject.getAttributes() != null) {
                        String name = subject.getTextContent();
                        String subId = subject.getAttributes().getNamedItem("href").getTextContent();
                        if (!subId.contains("subject0") && subId.contains("subject")) {
                            String absoluteSubjectId = editionString + subId.replace("../..", "");
                            logger.debug("Setting category: " + absoluteSubjectId);
                            Category tempCat = HibernateUtil.getCategoryElseCreate(session, absoluteSubjectId, name);
                            copject.getCategories().add(tempCat);
                        }
                    }
                }
            }
        } catch (NullPointerException n) {
            logger.warn("No subject elements. I Guess this is a Theme: " + n.getLocalizedMessage());
        }
    }

    private void removeAllCaterories(Object copject) {
        copject.getCategories().clear();
    }

    public Document parseModsString(String modsString) {
        Document modsDocument = null;
        try {
            InputSource is = new InputSource(new StringReader(modsString));
            modsDocument = builder.parse(is);
        } catch (SAXException e) {
            logger.error("SAXException: " + e.getMessage());
        } catch (IOException e) {
            logger.error("IOException: " + e.getMessage());
        }
        return modsDocument;
    }

    public String getIdFromMods(String modsString) {
        Document modsDocument = null;

        try {
            InputSource is = new InputSource(new StringReader(modsString));
            modsDocument = builder.parse(is);
            String id = extract(ID_XPATH, modsDocument);
            logger.debug("id is " + id);
            return id;
        } catch (SAXException e) {
            logger.error("SAXException: " + e.getMessage());
        } catch (IOException e) {
            logger.error("IOException: " + e.getMessage());
        } catch (XPathExpressionException e) {
            logger.error("XPathExpressionException: " + e.getMessage());
        }
        return null;
    }

    public String extract(String xPath, Document document) throws XPathExpressionException {
        String result = ObjectFromModsExtractor.xPath.evaluate(xPath, document);
        if (result != null) {
            result = result.trim();
        }
        return result;
    }

    /**
     * @param extractedDate input string   YYYY, YYYY-YYYY, YYYY/YYYY or YYYY-MM-dd
     * @return an Date[] with two elements    new Date[]{dateNotBefore, dateNotAfter};
     */
    public Date[] extractInDifferentFormats(String extractedDate) {
        Date dateNotAfter = null;
        Date dateNotBefore = null;
        try {
            if (extractedDate != null || !extractedDate.isEmpty()) {
                if (extractedDate.trim().length() == 4) { // Only year
                    dateNotAfter = dfYearOnly.parse(extractedDate);
                    dateNotBefore = dfYearOnly.parse(extractedDate);
                    return new Date[]{dateNotBefore, dateNotAfter};
                } else if (extractedDate.trim().length() == 9 && extractedDate.contains("-")) { // YYYY-YYYY format
                    dateNotBefore = dfYearOnly.parse(extractedDate.substring(0, 4));
                    dateNotAfter = dfYearOnly.parse(extractedDate.substring(5, 9));
                    return new Date[]{dateNotBefore, dateNotAfter};
                } else if (extractedDate.trim().length() == 9 && extractedDate.contains("/")) { // YYYY/YYYY format
                    dateNotBefore = dfYearOnly.parse(extractedDate.substring(0, 4));
                    dateNotAfter = dfYearOnly.parse(extractedDate.substring(5, 9));
                    return new Date[]{dateNotBefore, dateNotAfter};
                } else if (extractedDate.trim().length() == 10) { // YYYY-MM-dd format
                    dateNotAfter = df.parse(extractedDate);
                    dateNotBefore = df.parse(extractedDate);
                    return new Date[]{dateNotBefore, dateNotAfter};
                } else if(extractedDate.trim().length() == 21 && extractedDate.contains("/")) { // YYYY-MM-DD/YYYY-MM-DD format
                    dateNotBefore = df.parse(extractedDate.split("/")[0]);
                    dateNotAfter = df.parse(extractedDate.split("/")[1]);
                    return new Date[]{dateNotBefore, dateNotAfter};
                } else {
                    logger.warn("No correct formatted date format supplied. Formats accepted yyyy, yyyy-yyyy, yyyy/yyyy and yyyy-MM-dd. Received: " + extractedDate);
                }
            }
        } catch (ParseException e) {
            logger.warn("Unparsable date: " + e.getLocalizedMessage());
        } catch (NullPointerException n) {
            logger.warn("Unparsable date, threw 'null': " + n.getLocalizedMessage());
        }
        return null;
    }

}

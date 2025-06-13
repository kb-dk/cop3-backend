package dk.kb.cop3.backend.crud.util;

import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.hibernate.Category;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.TimeZone;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.xml.xpath.XPathExpressionException;

import static org.junit.jupiter.api.Assertions.*;

public class ObjectFromModsExtractorTest {
    private static String mods = null;
    private static final String LUFTFOTO_MODS_FILE = "src/test/resources/testdata/luftfoto_object182167.mods.xml";
    private static final String BOOK_MODS_FILE = "src/test/resources/testdata/boghis_object4724.mods.xml";

    ObjectFromModsExtractor objectFromModsExtractor = ObjectFromModsExtractor.getInstance();

    @BeforeAll
    public static void initTests() throws IOException {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Copenhagen"));
        CopBackendProperties.initialize(new FileInputStream("src/test/resources/cop_config.xml"));
    }

    @Test
    public void testExtractFromArealModsOK() throws IOException {
        String mods = Files.readString(Path.of(LUFTFOTO_MODS_FILE), StandardCharsets.UTF_8);
        dk.kb.cop3.backend.crud.database.hibernate.Object copject = new Object();
        Session ses = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = ses.beginTransaction();
        copject = objectFromModsExtractor.extractFromMods(copject, mods, ses);
        transaction.commit();
        assertEquals("Overgård - 1988", copject.getTitle());
        assertEquals("/images/luftfo/2011/maj/luftfoto/object182167", copject.getId());
        assertEquals("Iris og Børge Jensen, gårdejere",copject.getPerson());
        assertEquals("Langsted",copject.getBuilding());
        assertEquals("cumulus:blo",copject.getLastModifiedBy());
        assertEquals(55.275795018274586,copject.getPoint().getCoordinates()[0].getX(),0);
        assertEquals( 10.177794479921577,copject.getPoint().getCoordinates()[0].getY(),0);
        assertEquals(BigDecimal.ZERO,copject.getCorrectness());
        assertEquals("Fri Jan 01 00:00:00 CET 1988", copject.getNotBefore().toString());
        assertEquals("Fri Jan 01 00:00:00 CET 1988", copject.getNotAfter().toString());
        assertEquals("Skråfoto",copject.getType().getTypeText());
        assertEquals("/images/luftfo/2011/maj/luftfoto",copject.getEdition().getId());
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/images/luftfo/2011/maj/luftfoto/subject203/en/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/images/luftfo/2011/maj/luftfoto/subject203/da/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/images/luftfo/2011/maj/luftfoto/subject234/en/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/images/luftfo/2011/maj/luftfoto/subject234/da/".equals(((Category)set).getId())));
        ses.close();
    }

    @Test
    public void testExtractFromBookModsOK() throws IOException {
        String mods = Files.readString(Path.of(BOOK_MODS_FILE), StandardCharsets.UTF_8);
        dk.kb.cop3.backend.crud.database.hibernate.Object copject = new Object();
        Session ses = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = ses.beginTransaction();
        copject = objectFromModsExtractor.extractFromMods(copject, mods, ses);
        transaction.commit();
        assertEquals("Torres Naharro, Bartolomé de", copject.getCreator());
        assertEquals("Propalladia", copject.getTitle());
        assertEquals("/books/boghis/2017/dec/tryk/object4724", copject.getId());
        assertEquals("cumulus:blo",copject.getLastModifiedBy());
        assertEquals(BigDecimal.ZERO,copject.getCorrectness());
        assertEquals("Thu Jan 01 00:00:00 CET 1517", copject.getNotBefore().toString());
        assertEquals("Thu Jan 01 00:00:00 CET 1517", copject.getNotAfter().toString());
        assertEquals("/books/boghis/2017/dec/tryk",copject.getEdition().getId());
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/books/boghis/2017/dec/tryk/subject1310/da/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/books/boghis/2017/dec/tryk/subject1514/en/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/books/boghis/2017/dec/tryk/subject1310/en/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/books/boghis/2017/dec/tryk/subject1212/en/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/books/boghis/2017/dec/tryk/subject1612/en/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/books/boghis/2017/dec/tryk/subject1510/da/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/books/boghis/2017/dec/tryk/subject1511/en/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/books/boghis/2017/dec/tryk/subject1315/da/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/books/boghis/2017/dec/tryk/subject1211/en/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/books/boghis/2017/dec/tryk/subject1612/da/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/books/boghis/2017/dec/tryk/subject1514/da/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/books/boghis/2017/dec/tryk/subject1315/en/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/books/boghis/2017/dec/tryk/subject1211/da/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/books/boghis/2017/dec/tryk/subject1511/da/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/books/boghis/2017/dec/tryk/subject1208/en/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/books/boghis/2017/dec/tryk/subject1216/da/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/books/boghis/2017/dec/tryk/subject1216/en/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/books/boghis/2017/dec/tryk/subject1208/da/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/books/boghis/2017/dec/tryk/subject1212/da/".equals(((Category)set).getId())));
        assertTrue(copject.getCategories().stream().anyMatch(set -> "/books/boghis/2017/dec/tryk/subject1510/en/".equals(((Category)set).getId())));
        ses.close();
    }


    /**
     * TODO tests doesn't work atm. need to fix when we mavenize
     */
    @Test
    public void testExtractInDifferentFormats(){
        Date[] dates = objectFromModsExtractor.extractInDifferentFormats("1950");
        assertEquals("Sun Jan 01 00:00:00 CET 1950", dates[0].toString());
        assertEquals("Sun Jan 01 00:00:00 CET 1950", dates[1].toString());

        dates =  objectFromModsExtractor.extractInDifferentFormats("1950-1952");
        assertEquals("Sun Jan 01 00:00:00 CET 1950", dates[0].toString());
        assertEquals("Tue Jan 01 00:00:00 CET 1952", dates[1].toString());

        dates =  objectFromModsExtractor.extractInDifferentFormats("1950/1951");
        assertEquals("Sun Jan 01 00:00:00 CET 1950", dates[0].toString());
        assertEquals("Mon Jan 01 00:00:00 CET 1951", dates[1].toString());

        dates =  objectFromModsExtractor.extractInDifferentFormats("1945-12-03");
        assertEquals("Mon Dec 03 00:00:00 CET 1945", dates[0].toString());
        assertEquals("Mon Dec 03 00:00:00 CET 1945", dates[1].toString());

        dates =  objectFromModsExtractor.extractInDifferentFormats("1945-12-03/1950-01-01");
        assertEquals("Mon Dec 03 00:00:00 CET 1945", dates[0].toString());
        assertEquals("Sun Jan 01 00:00:00 CET 1950", dates[1].toString());

        dates =  objectFromModsExtractor.extractInDifferentFormats("1945-12-03/1950-01-01:4561"); // unsupported format
        assertNull(dates);
    }

    @Test
    public void testExtractFromModsModsNull() {
        Throwable error = assertThrows(RuntimeException.class, () ->
                objectFromModsExtractor.extractFromMods(new Object(), null, null));
        assertEquals( "java.lang.NullPointerException", error.toString());
    }

//    @Test
//    public void testExtractFromModsCopjectNull() {
//        try{
//            objectFromModsExtractor.extractFromMods(null, mods, null);
//            fail();
//        } catch (IllegalArgumentException exception){
//            assertTrue(true);
//        }
//    }
    @Test
    public void testExtractFromModsCopjectNull() {
        Throwable error = assertThrows(RuntimeException.class, () -> objectFromModsExtractor.extractFromMods(null, mods, null));
        assertEquals("java.lang.IllegalArgumentException: copject cannot be null", error.toString());
        assertEquals("copject cannot be null", error.getMessage());
    }

    @Test
    public void testExtractBuilding() throws IOException, XPathExpressionException {
        String mods = Files.readString(Path.of(LUFTFOTO_MODS_FILE), StandardCharsets.UTF_8);
        String BUILDING_XPATH = "/mods/subject/hierarchicalGeographic/area[@areaType='building']";
        String building = objectFromModsExtractor.extract(BUILDING_XPATH,objectFromModsExtractor.parseModsString(mods));
        System.out.println(building);
    }
}

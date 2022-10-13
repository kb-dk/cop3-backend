package dk.kb.cop3.backend.crud.util;

import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by IntelliJ IDEA.
 * User: jac
 * Date: 14-04-11
 * Time: 13:07
 * To change this template use File | Settings | File Templates.
 */
public class BeanUtilsTest {

    /**
     * The logger called myLogger
     */
    private static Logger myLogger = Logger.getLogger(BeanUtilsTest.class);

    private static String mods = null;
    private static String themeMods = null;

    private static final String MODS_FILE = "testdata/cumulus-export/Luftfoto_OM/205/master_records/L0717_04.tif-mods.xml";
    private static final String THEME_MODS_FILE = "testdata/tema/luftfoto-tema-mods.xml";

    BeanUtils beanUtils = BeanUtils.getInstance();

    @BeforeClass
    public static void initTests() throws ParserConfigurationException, IOException {
        mods = FileUtils.readFileToString(new File(MODS_FILE), "UTF-8");
        themeMods = FileUtils.readFileToString(new File(THEME_MODS_FILE), "UTF-8");
    }

    @Test
    public void testExtractFromModsOK() {
        dk.kb.cop3.backend.crud.database.hibernate.Object copject = new Object();
        Session ses = HibernateUtil.getSessionFactory().getCurrentSession();
        ses.getTransaction().begin();
        copject = beanUtils.extractFromMods(copject, mods, ses);

        //ses.getTransaction().commit(); //Use only if you wan't to write to the DB

        assertEquals("Sylvest Jensen", copject.getCreator());
        // assertEquals("Danmark, Fyn, Gudbjerg Mark (1939)", copject.getTitle());
        assertEquals("/images/luftfo/2011/maj/luftfoto/object62138", copject.getId());
        assertEquals("Sun Jan 01 00:00:00 CET 1939", copject.getNotBefore().toString());
        assertEquals("Sun Jan 01 00:00:00 CET 1939", copject.getNotAfter().toString());
        ses.close();
    }


    /**
     * TODO tests doesn't work atm. need to fix when we mavenize
     */
    @Test
    public void testExtractInDifferentFormats(){
        Date dates[] = beanUtils.extractInDifferentFormats("1950");
        assertEquals("Sun Jan 01 00:00:00 CET 1950", dates[0].toString());
        assertEquals("Sun Jan 01 00:00:00 CET 1950", dates[1].toString());

        dates =  beanUtils.extractInDifferentFormats("1950-1952");
        assertEquals("Sun Jan 01 00:00:00 CET 1950", dates[0].toString());
        assertEquals("Tue Jan 01 00:00:00 CET 1952", dates[1].toString());

        dates =  beanUtils.extractInDifferentFormats("1950/1951");
        assertEquals("Sun Jan 01 00:00:00 CET 1950", dates[0].toString());
        assertEquals("Mon Jan 01 00:00:00 CET 1951", dates[1].toString());

        dates =  beanUtils.extractInDifferentFormats("1945-12-03");
        assertEquals("Mon Dec 03 00:00:00 CET 1945", dates[0].toString());
        assertEquals("Mon Dec 03 00:00:00 CET 1945", dates[1].toString());

        dates =  beanUtils.extractInDifferentFormats("1945-12-03/1950-01-01");
        assertEquals("Mon Dec 03 00:00:00 CET 1945", dates[0].toString());
        assertEquals("Sun Jan 01 00:00:00 CET 1950", dates[1].toString());

        dates =  beanUtils.extractInDifferentFormats("1945-12-03/1950-01-01:4561"); // unsupported format
        assertNull(dates);
    }

    @Test
    public void testExtractFromModsThemeOK() {
        dk.kb.cop3.backend.crud.database.hibernate.Object theme = new Object();
        Session ses = HibernateUtil.getSessionFactory().getCurrentSession();
        ses.getTransaction().begin();
        theme = beanUtils.extractFromMods(theme, themeMods, ses);

        assertEquals("Jacob Larsen (jac@kb.dk)", theme.getCreator());
        assertEquals("Smukke steder på Fyn og Langeland", theme.getTitle());
        assertEquals("/images/luftfo/2011/maj/luftfoto/object99999", theme.getId());
        assertNull(theme.getNotBefore());
        assertNull(theme.getNotAfter());
        ses.close();
    }

    @Test(expected = NullPointerException.class)
    public void testExtractFromModsModsNull() {
        beanUtils.extractFromMods(new Object(), null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtractFromModsCopjectNull() {
        beanUtils.extractFromMods(null, mods, null);
    }
}

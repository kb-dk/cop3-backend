package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.constants.Types;
import dk.kb.cop3.backend.crud.database.hibernate.*;
import dk.kb.cop3.backend.crud.util.ObjectFromModsExtractor;
import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class HibernateToDBTest {

    private static Session session;
    private static final String LODFOTO = "Lodfoto";
    private static HibernateMetadataSource mds;
     private static HibernateMetadataWriter mdw;
    private static final String HIBERNATE_TEST_CATEGORY = "Hibernate-test-category";
    private static final String KATEGORI_TEKST = "slet denne test";
       private static final String MODS_FILE = "testdata/cumulus-export/Luftfoto_OM/205/master_records/L0717_04.tif-mods.xml";

    private static String clob2string(Clob clob) throws Exception {
        StringBuffer sb = new StringBuffer();
        String s;
        BufferedReader br = new BufferedReader(clob.getCharacterStream());
        while ((s = br.readLine()) != null)
            sb.append(s);
        return sb.toString();
    }


    private static void testGet(Session session) throws Exception {
/*		
		List result = session.createSQLQuery("select mods from OBJECT").list();
		Iterator<org.hibernate.lob.SerializableClob> iter = result.iterator();

		while (iter.hasNext()) {
			org.hibernate.lob.SerializableClob mods = iter.next();
			StringBuffer sb = new StringBuffer();
       			String s;
			BufferedReader br = new BufferedReader(mods.getCharacterStream());
			while ((s=br.readLine())!=null)
        			sb.append(s);
			System.out.println(sb.toString());
		}
*/
        List result2 = session.createSQLQuery("SELECT {obj.*} FROM OBJECT {obj}")
                .addEntity("obj", dk.kb.cop3.backend.crud.database.hibernate.Object.class)
                .list();
        Iterator<java.lang.Object> iter2 = result2.iterator();
        dk.kb.cop3.backend.crud.database.hibernate.Object o = null;
        while (iter2.hasNext()) {
            o = (dk.kb.cop3.backend.crud.database.hibernate.Object) iter2.next();
            //System.out.println(o.getMods());
         //   double[] p = o.getPoint().getPoint();
            //System.out.println("("+p[0]+","+p[1]+")");
        }
        if (o != null) {
            dk.kb.cop3.backend.crud.database.hibernate.Object o2 = new dk.kb.cop3.backend.crud.database.hibernate.Object("test1234", o.getType(), o.getEdition(), o.getMods(), "last monday", '0', "someUser", new BigDecimal(1), new BigDecimal(0.5), new BigDecimal(1));

            //dk.kb.cop3.backend.crud.database.hibernate.Object("test1234",o.getType(),o.getEdition(),o.getMods(),new Date(),'0',"someuser",new BigDecimal(1));


            Transaction tx = session.beginTransaction();
            o2.getCategories().add(session.load(Category.class, new java.math.BigDecimal(1)));
            String id = (String) session.save(o2);
            //System.out.println("saved " + id);
            tx.commit();
        }

    }

    @BeforeClass
    public static void before() {
        Configuration cfg = new Configuration().configure("hibernate.cfg.xml");


        SessionFactory sessions = cfg.buildSessionFactory();
        session = sessions.openSession();
        mds = new HibernateMetadataSource(session);

    }

    @AfterClass
    public static void after() {
        //session.getTransaction().rollback();
         session.close();
    }


    @Test
    public void findEnTypeMatcherKonstantenMedDataFraDB() {

        int konstantID = Types.getTypeByName(LODFOTO);

        mds.setType("" + konstantID);

        Type type = mds.getType();
        assertEquals(type.getTypeText(), LODFOTO);
    }


       @Test
    public void opretEnKategori() {
        Category nyCat = new Category(HIBERNATE_TEST_CATEGORY, KATEGORI_TEKST);
        session.save(nyCat);
        mds.setCategory(HIBERNATE_TEST_CATEGORY);


        assertEquals(nyCat.getCategoryText(), mds.getCategory().getCategoryText());
    }
    @Test
    public void findEnKategori() {

        mds.setCategory(HIBERNATE_TEST_CATEGORY);

        Category cat = mds.getCategory();
        assertEquals(cat.getCategoryText(), KATEGORI_TEKST);
    }

    @Test
    public void findEnEdition() {
        mds.setEdition("/images/luftfo/2011/maj/luftfoto");
        Edition ed = mds.getEdition();
        assertEquals(ed.getName(), "Luftfoto");
    }


    @Test
       public void opretMagicCopject() {
        dk.kb.cop3.backend.crud.database.hibernate.Object copject = new dk.kb.cop3.backend.crud.database.hibernate.Object();
        ObjectFromModsExtractor objectFromModsExtractor = ObjectFromModsExtractor.getInstance();

        String mods = null;
        try {
            mods = FileUtils.readFileToString(new File(MODS_FILE), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        copject = objectFromModsExtractor.extractFromMods(copject, mods, session);

           assertEquals(copject.getId(), "/images/luftfo/2011/maj/luftfoto/object62138");
       }

    @Test
    public void opdaterGeoKoordinatForCopject() {
        MetadataWriter mdw = new HibernateMetadataWriter(session);
        // /images/luftfo/2011/maj/luftfoto/object74174
        String output = mdw.updateGeo("/images/luftfo/2011/maj/luftfoto/object74174", 60.90d, 750.60d, "test user","", 0.0);
        //String output = mdw.updateGeo("/images/luftfo/2011/maj/luftfoto/object62138", 60.90d, 750.60d);
        //System.out.println("output = " + output);
        assertEquals(Long.parseLong(output)>0, true);
    }

    @Test
    public void findAnEditionList() {	
	HibernateEditionSource eds = new HibernateEditionSource(session);
	eds.execute();
	if(eds.hasMore()) {
	    Edition ed = eds.getAnother();
	    //System.out.println("ID="+ed.getId());
	}
    }


}

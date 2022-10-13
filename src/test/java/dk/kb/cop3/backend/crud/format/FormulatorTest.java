package dk.kb.cop3.backend.crud.format;

import org.junit.Test;
import org.w3c.dom.Document;

/**
 * jUnit tests of formulators, using a fake metadatasource
 *
 * @Author Sigfrid Lundberg
 * $Rev$ last modified $LastChangedDate$ by $Author$
 * $Id$
 */
public class FormulatorTest {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(FormulatorTest.class);

    /*
      @Test
      public void testKmlFormulator() {

      dk.kb.cop3.backend.crud.database.MetadataSource source = null;

      if(true) {
      org.hibernate.Session session = 
      dk.kb.cop3.backend.crud.database.HibernateUtil.getSessionFactory().getCurrentSession();

      session.beginTransaction();

      source = new dk.kb.cop3.backend.crud.database.HibernateMetadataSource(session);

      } else {

      source = new dk.kb.cop3.backend.crud.database.TestMetadataSource();

      }


      String bb = "10.6518,55.1366,10.6520,55.1368";
      source.setNumberPerPage(5);
      source.setOffset(3);
      source.setBoundingBox(bb);

      MetadataFormulator formulator = new KmlMetadataFormulator();

      formulator.setDataSource(source);
      formulator.setOutPutStream(System.out);
      Document dom = formulator.formulate();
      System.out.println(formulator.serialize(dom));

      }


    @Test
    public void testKmlFormulator() {

	org.hibernate.Session session = 
	    dk.kb.cop3.backend.crud.database.HibernateUtil.getSessionFactory().getCurrentSession();
	session.beginTransaction();

	dk.kb.cop3.backend.crud.database.MetadataSource source 
	    = new dk.kb.cop3.backend.crud.database.HibernateMetadataSource(session);

	String bb = "10.6518,55.1366,10.6520,55.1368";
	source.setNumberPerPage(5);
	source.setOffset(3);
	source.setBoundingBox(bb);

	source.execute();

        MetadataFormulator formulator = new KmlMetadataFormulator();

        formulator.setDataSource(source);
        formulator.setOutPutStream(System.out);
        Document dom = formulator.formulate();
	System.out.println(formulator.serialize(dom));

    }

    @Test
    public void testRssFormulator() {

	String id = "/images/luftfo/2011/maj/luftfoto/object62173";

	org.hibernate.Session session = 
	    dk.kb.cop3.backend.crud.database.HibernateUtil.getSessionFactory().getCurrentSession();
	session.beginTransaction();

	dk.kb.cop3.backend.crud.database.MetadataSource source 
	    = new dk.kb.cop3.backend.crud.database.HibernateMetadataSource(session);
	source.setSearchterms("id",id);
	source.execute();

        MetadataFormulator formulator = new RssMetadataFormulator();

        formulator.setDataSource(source);
        formulator.setOutPutStream(System.out);
        Document dom = formulator.formulate();
	System.out.println(formulator.serialize(dom));

    }
    */
    @Test
    public void testSolrFormulator() {

    String id = "/images/luftfo/2011/maj/luftfoto/object203821";

    org.hibernate.Session session = 
	dk.kb.cop3.backend.crud.database.HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    dk.kb.cop3.backend.crud.database.MetadataSource source 
	= new dk.kb.cop3.backend.crud.database.HibernateMetadataSource(session);
    source.setSearchterms("id",id);
    source.execute();

    MetadataFormulator formulator = new SolrMetadataFormulator();

    formulator.setDataSource(source);
    formulator.setOutPutStream(System.out);
    Document dom = formulator.formulate();
    System.out.println(formulator.serialize(dom));

    if (session != null && session.isConnected()){
	logger.debug("Closing Hibernate session as we're still connected");
	session.cancelQuery();
	session.close();
    }

}


/*@Test
  public void testAtomFormulator() {
  dk.kb.cop3.backend.crud.database.MetadataSource source =
  new dk.kb.cop3.backend.crud.database.TestMetadataSource();

  MetadataFormulator formulator = new AtomMetadataFormulator();

  formulator.setDataSource(source);
  formulator.setOutPutStream(System.out);
  Document dom = formulator.formulate();
  System.out.println(formulator.serialize(dom));
  }

@Test
    public void testContentFormulator() {

    String id = "/images/luftfo/2011/maj/luftfoto/object62173";
    org.hibernate.Session session = 
	dk.kb.cop3.backend.crud.database.HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    dk.kb.cop3.backend.crud.database.MetadataSource source 
	= new dk.kb.cop3.backend.crud.database.HibernateMetadataSource(session);
    source.setSearchterms("id",id);
    source.execute();

    MetadataFormulator formulator = new ContentMetadataFormulator();	

    formulator.setDataSource(source);
    formulator.setOutPutStream(System.out);
    Document dom = formulator.formulate();
    System.out.println(formulator.serialize(dom));
}

@Test
public void testEditionFormulator() {

    org.hibernate.Session session = 
	dk.kb.cop3.backend.crud.database.HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();


    dk.kb.cop3.backend.crud.database.HibernateEditionSource source 
	= new dk.kb.cop3.backend.crud.database.HibernateEditionSource(session);

    EditionMetadataFormulator formulator = new EditionMetadataFormulator();
    formulator.setSession(session);
    formulator.setDataSource(source);
    formulator.setOutPutStream(System.out);
    Document dom = formulator.formulate();
    System.out.println(formulator.serialize(dom));
}
*/
}

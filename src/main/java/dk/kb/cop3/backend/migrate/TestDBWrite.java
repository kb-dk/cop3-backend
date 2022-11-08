package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.crud.database.hibernate.Type;
import dk.kb.cop3.backend.crud.database.type.Point;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class TestDBWrite {

    public static void main(String[] args) {
        SessionFactory sessfac = new Configuration().configure("hibernate.cfg.xml")
                .setProperty(org.hibernate.cfg.Environment.HBM2DDL_AUTO,"update")
                .buildSessionFactory();
        Session session = sessfac.openSession();
        Transaction trans = session.beginTransaction();
        Type type = new Type(BigDecimal.TEN,"TestType");
        session.save(type);
        Edition edition = new Edition();
        edition.setId("edition1");
        edition.setName("Test edition");
        edition.setNameEn("Test edition en");
        edition.setUrlName("http://www.kb.dk");
        edition.setUrlMatrialType("material 1");
        edition.setUrlPubYear(new BigDecimal(2022));
        edition.setUrlPubMonth("Oct");
        edition.setUrlCollection("rugbrød");
        edition.setCumulusCatalog("images");
        edition.setCumulusTopCatagory("1");
        edition.setNormalisationrule("rule 1");

        edition.setVisiblePublic('j');
        session.save(edition);
        Object object = new Object();
        GeometryFactory geoFactory = JTSFactoryFinder.getGeometryFactory();
        object.setPoint(geoFactory.createPoint(new Coordinate(10,20)));
        object.setId("test1234");
        object.setType(type);
        object.setEdition(edition);
        object.setMods("<mods></mods>");
        object.setDeleted('n');
        object.setLastModified("1231231231");
        object.setLastModifiedBy("me");
        object.setObjVersion(new BigDecimal(1));
        object.setInterestingess(new BigDecimal(0));
        object.setTitle("title");
        object.setCorrectness(new BigDecimal(0));
        object.setLikes(BigInteger.valueOf(0));
        object.setBuilding("building");
        object.setCreator("creator");
        object.setLocation("location");
        object.setNotAfter(new Date());
        object.setNotBefore(new Date());
        object.setPerson("persion");
        object.setBookmark(BigInteger.valueOf(0));
        object.setRandomNumber(BigDecimal.ZERO);
        session.save(object);
        trans.commit();


     //   Object retreivedObject = (Object) session.load(Object.class,"test1234");
     //   System.out.println(retreivedObject.getPoint());
    }

}

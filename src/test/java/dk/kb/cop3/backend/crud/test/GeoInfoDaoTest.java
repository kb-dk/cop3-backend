package dk.kb.cop3.backend.crud.test;

/**
 * kb.dk
 *
 * @author jatr
 *         Date: 10/10/13
 *         Time: 09:28
 */

import dk.kb.cop3.backend.constants.DSFLAreas;
import dk.kb.cop3.backend.crud.database.GeoInfoDao;
import dk.kb.cop3.backend.crud.database.GeoInfoDaoImpl;
import dk.kb.cop3.backend.crud.exception.AreaNotFoundException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test class for the GeoInfoDao
 */
public class GeoInfoDaoTest {

    @Test
    public void getAreaDetailsForPointInFyn() {
        GeoInfoDao geoInfoDao = new GeoInfoDaoImpl();
        try {
            Object[] fynArea = geoInfoDao.getAreaDetails(55.395833, 10.388611);//co-ordinates for Odense
            Assert.assertEquals(fynArea[1], DSFLAreas.Fyn);
        } catch (AreaNotFoundException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getAreaDetailsForPointInBornholm() {
        GeoInfoDao geoInfoDao = new GeoInfoDaoImpl();
        try {
            Object[] bornholmArea = geoInfoDao.getAreaDetails(55.098611, 14.701389);//co-ordinates for Rønne
            Assert.assertEquals(bornholmArea[1], DSFLAreas.Bornholm);
        } catch (AreaNotFoundException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getAreaDetailsForPointInHovedstaden() {
        GeoInfoDao geoInfoDao = new GeoInfoDaoImpl();
        try {
            Object[] fynArea = geoInfoDao.getAreaDetails(55.676111, 12.568333);//co-ordinates for København
            Assert.assertEquals(fynArea[1], DSFLAreas.Hovedstaden);
        } catch (AreaNotFoundException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getAreaDetailsForPointInKattegat() {
        GeoInfoDao geoInfoDao = new GeoInfoDaoImpl();
        try {
            Object[] kattegatArea = geoInfoDao.getAreaDetails(57.266667, 11.1);//co-ordinates for Læsø
            Assert.assertEquals(kattegatArea[1], DSFLAreas.Kattegat);
        } catch (AreaNotFoundException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getAreaDetailsForPointInLollandFalster() {
        GeoInfoDao geoInfoDao = new GeoInfoDaoImpl();
        try {
            Object[] lollandFalsterArea = geoInfoDao.getAreaDetails(54.8, 11.966667);//co-ordinates for Falster
            Assert.assertEquals(lollandFalsterArea[1], DSFLAreas.LollandFalster);
        } catch (AreaNotFoundException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getAreaDetailsForPointInMidtjylland() {
        GeoInfoDao geoInfoDao = new GeoInfoDaoImpl();
        try {
            Object[] midtjyllandArea = geoInfoDao.getAreaDetails(56.15, 10.216667);//co-ordinates for Århus
            Assert.assertEquals(midtjyllandArea[1], DSFLAreas.Midtjylland);
        } catch (AreaNotFoundException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getAreaDetailsForPointInNordjylland() {
        GeoInfoDao geoInfoDao = new GeoInfoDaoImpl();
        try {
            Object[] nordjyllandArea = geoInfoDao.getAreaDetails(57.05, 9.916667);//co-ordinates for Aalborg
            Assert.assertEquals(nordjyllandArea[1], DSFLAreas.Nordjylland);
        } catch (AreaNotFoundException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getAreaDetailsForPointInSjælland() {
        GeoInfoDao geoInfoDao = new GeoInfoDaoImpl();
        try {
            Object[] sjællandArea = geoInfoDao.getAreaDetails(55.65, 12.083333);//co-ordinates for Roskilde
            Assert.assertEquals(sjællandArea[1], DSFLAreas.Sjælland);
        } catch (AreaNotFoundException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getAreaDetailsForPointInSønderjylland() {
        GeoInfoDao geoInfoDao = new GeoInfoDaoImpl();
        try {
            Object[] sønderjyllandArea = geoInfoDao.getAreaDetails(54.91382, 9.79225);//co-ordinates for Sønderborg
            Assert.assertEquals(sønderjyllandArea[1], DSFLAreas.Sønderjylland);
        } catch (AreaNotFoundException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test(expected = AreaNotFoundException.class)
    public void getAreaDetailsThatDoesNotExist() throws AreaNotFoundException {
        GeoInfoDao geoInfoDao = new GeoInfoDaoImpl();
        geoInfoDao.getAreaDetails(50.65, 12.083333);//co-ordinates for somewhere not in Denmark
    }
}

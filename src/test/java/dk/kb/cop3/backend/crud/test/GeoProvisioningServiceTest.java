package dk.kb.cop3.backend.crud.test;

import dk.kb.cop3.backend.constants.DSFLAreas;
import dk.kb.cop3.backend.crud.services.GeoProvisioningService;
import dk.kb.cop3.backend.crud.util.TestUtil;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Unit test class for the GeoInfoDao
 */
public class GeoProvisioningServiceTest {

    Session session;

    @BeforeEach
    public void initTest() throws FileNotFoundException {
        session = TestUtil.openDatabaseSession();
        session.beginTransaction();
    }

    @Test
    public void getAreaDetailsForPointInFyn() {
        GeoProvisioningService geoService = new GeoProvisioningService(session);
        DSFLAreas area = geoService.getAreaNotDanmark(55.395833, 10.388611);//co-ordinates for Odense
        assertEquals(DSFLAreas.Fyn, area);
        List<DSFLAreas> areas = geoService.getAreasContainingPoint(55.395833, 10.388611);
        assertTrue(areas.contains(DSFLAreas.Danmark));
        assertTrue(areas.contains(DSFLAreas.Fyn));
    }

    @Test
    public void getAreaDetailsForPointInBornholm() {
        GeoProvisioningService geoService = new GeoProvisioningService(session);
        DSFLAreas area = geoService.getAreaNotDanmark(55.098611, 14.701389);//co-ordinates for Odense
        assertEquals(DSFLAreas.Bornholm, area);
        List<DSFLAreas> areas = geoService.getAreasContainingPoint(55.098611, 14.701389);
        assertTrue(areas.contains(DSFLAreas.Danmark));
        assertTrue(areas.contains(DSFLAreas.Bornholm));
    }

    @Test
    public void getAreaDetailsForPointInHovedstaden() {
        GeoProvisioningService geoService = new GeoProvisioningService(session);
        DSFLAreas area = geoService.getAreaNotDanmark(55.676111, 12.568333);
        assertEquals(DSFLAreas.Hovedstaden, area);
        List<DSFLAreas> areas = geoService.getAreasContainingPoint(55.676111, 12.568333);
        assertTrue(areas.contains(DSFLAreas.Danmark));
        assertTrue(areas.contains(DSFLAreas.Hovedstaden));
    }

    @Test
    public void getAreaDetailsForPointInKattegat() {
        GeoProvisioningService geoService = new GeoProvisioningService(session);
        DSFLAreas area = geoService.getAreaNotDanmark(57.266667, 11.1);//co-ordinates for Odense
        assertEquals(DSFLAreas.Kattegat, area);
        List<DSFLAreas> areas = geoService.getAreasContainingPoint(57.266667, 11.1);
        assertTrue(areas.contains(DSFLAreas.Danmark));
        assertTrue(areas.contains(DSFLAreas.Kattegat));
    }

    @Test
    public void getAreaDetailsForPointInLollandFalster() {
        GeoProvisioningService geoService = new GeoProvisioningService(session);
        DSFLAreas area = geoService.getAreaNotDanmark(54.8, 11.966667);//co-ordinates for Odense
        assertEquals(DSFLAreas.LollandFalster, area);
        List<DSFLAreas> areas = geoService.getAreasContainingPoint(54.8, 11.966667);
        assertTrue(areas.contains(DSFLAreas.Danmark));
        assertTrue(areas.contains(DSFLAreas.LollandFalster));
    }

    @Test
    public void getAreaDetailsForPointInMidtjylland() {
        GeoProvisioningService geoService = new GeoProvisioningService(session);
        DSFLAreas area = geoService.getAreaNotDanmark(56.15, 10.216667);//co-ordinates for Odense
        assertEquals(DSFLAreas.Midtjylland, area);
        List<DSFLAreas> areas = geoService.getAreasContainingPoint(56.15, 10.216667);
        assertTrue(areas.contains(DSFLAreas.Danmark));
        assertTrue(areas.contains(DSFLAreas.Midtjylland));
    }

    @Test
    public void getAreaDetailsForPointInNordjylland() {
        GeoProvisioningService geoService = new GeoProvisioningService(session);
        DSFLAreas area = geoService.getAreaNotDanmark(57.05, 9.916667);//co-ordinates for Odense
        assertEquals(DSFLAreas.Nordjylland, area);
        List<DSFLAreas> areas = geoService.getAreasContainingPoint(57.05, 9.916667);
        assertTrue(areas.contains(DSFLAreas.Danmark));
        assertTrue(areas.contains(DSFLAreas.Nordjylland));
    }

    @Test
    public void getAreaDetailsForPointInSjælland() {
        GeoProvisioningService geoService = new GeoProvisioningService(session);
        DSFLAreas area = geoService.getAreaNotDanmark(55.65, 12.083333);//co-ordinates for Odense
        assertEquals(DSFLAreas.Sjælland, area);
        List<DSFLAreas> areas = geoService.getAreasContainingPoint(55.65, 12.083333);
        assertTrue(areas.contains(DSFLAreas.Danmark));
        assertTrue(areas.contains(DSFLAreas.Sjælland));
    }

    @Test
    public void getAreaDetailsForPointInSydjylland() {
        GeoProvisioningService geoService = new GeoProvisioningService(session);
        DSFLAreas area = geoService.getAreaNotDanmark(54.91382, 9.79225);//co-ordinates for Odense
        assertEquals(DSFLAreas.Sydjylland, area);
        List<DSFLAreas> areas = geoService.getAreasContainingPoint(54.91382, 9.79225);
        assertTrue(areas.contains(DSFLAreas.Danmark));
        assertTrue(areas.contains(DSFLAreas.Sydjylland));
    }

    @Test
    public void getAreaDetailsThatDoesNotExist() {
        GeoProvisioningService geoService = new GeoProvisioningService(session);
        List<DSFLAreas> areas = geoService.getAreasContainingPoint(0.0, 0.0);
        assertTrue(areas.isEmpty());
    }
}

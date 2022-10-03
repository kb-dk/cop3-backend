package dk.kb.cop3.backend.crud.services;

import dk.kb.cop3.backend.constants.Areas;
import dk.kb.cop3.backend.constants.DSFLAreas;
import dk.kb.cop3.backend.crud.database.GeoInfoDao;
import dk.kb.cop3.backend.crud.exception.AreaNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * kb.dk
 *
 * @author jatr
 *         Date: 09/10/13
 *         Time: 16:50
 */
public class GeoProvisioningService {

    Logger LOGGER = Logger.getLogger(GeoProvisioningService.class);

    private static final ApplicationContext COPII_CONTEXT = new ClassPathXmlApplicationContext("copII-dao-context.xml");

    public GeoProvisioningService() {

    }

    public DSFLAreas getArea(double lat, double lng) throws AreaNotFoundException {
        LOGGER.debug("lat: "+ lat);
        LOGGER.debug("lng: "+ lng);
        GeoInfoDao geoInfoDao = (GeoInfoDao) COPII_CONTEXT.getBean("geoInfoDao");
        String area =  geoInfoDao.getAreaDetails(lat, lng)[1].toString();
        DSFLAreas enumArea = Areas.getAreaEnumByName(area);
        return enumArea;
    }

    public Object[] getAreaDetailsForPoint(double lat, double lng) throws AreaNotFoundException {
        LOGGER.debug("lat: "+ lat);
        LOGGER.debug("lng: " + lng);
        GeoInfoDao geoInfoDao = (GeoInfoDao) COPII_CONTEXT.getBean("geoInfoDao");
        return geoInfoDao.getAreaDetails(lat, lng);
    }
}

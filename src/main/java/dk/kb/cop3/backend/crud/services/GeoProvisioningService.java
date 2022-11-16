package dk.kb.cop3.backend.crud.services;

import dk.kb.cop3.backend.constants.Areas;
import dk.kb.cop3.backend.constants.DSFLAreas;
import dk.kb.cop3.backend.crud.database.GeoInfoDaoImpl;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.exception.AreaNotFoundException;
import org.apache.log4j.Logger;

/**
 * kb.dk
 *
 * @author jatr
 *         Date: 09/10/13
 *         Time: 16:50
 */
public class GeoProvisioningService {

    Logger LOGGER = Logger.getLogger(GeoProvisioningService.class);

    public DSFLAreas getArea(double lat, double lng) throws AreaNotFoundException {
        GeoInfoDaoImpl geoInfoDao = new GeoInfoDaoImpl();
        geoInfoDao.setSessionFactory(HibernateUtil.getSessionFactory());
        String area =  geoInfoDao.getAreaDetails(lat, lng)[1].toString();
        DSFLAreas enumArea = Areas.getAreaEnumByName(area);
        return enumArea;
    }

    public Object[] getAreaDetailsForPoint(double lat, double lng) throws AreaNotFoundException {
        LOGGER.debug("lat: "+ lat);
        LOGGER.debug("lng: " + lng);
        GeoInfoDaoImpl geoInfoDao = new GeoInfoDaoImpl();
        geoInfoDao.setSessionFactory(HibernateUtil.getSessionFactory());
        return geoInfoDao.getAreaDetails(lat, lng);
    }
}

package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.exception.AreaNotFoundException;

/**
 * kb.dk
 *
 * @author jatr
 *         Date: 09/10/13
 *         Time: 17:04
 */
public interface GeoInfoDao {

    public Object[] getAreaDetails(double lat, double lng) throws AreaNotFoundException;
}

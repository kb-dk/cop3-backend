package dk.kb.cop3.backend.crud.services;

import dk.kb.cop3.backend.constants.Areas;
import dk.kb.cop3.backend.constants.DSFLAreas;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * kb.dk
 *
 * @author jatr
 *         Date: 09/10/13
 *         Time: 16:50
 */
public class GeoProvisioningService {

    private static final Logger logger = LoggerFactory.getLogger(GeoProvisioningService.class);

    private Session session;

    public GeoProvisioningService(Session session) {
        this.session = session;
    }

    public DSFLAreas getAreaNotDanmark(double lat, double lng)  {
        Optional<DSFLAreas> areaOptional = getAreasContainingPoint(lat, lng).stream().filter(area -> area != DSFLAreas.Danmark).findFirst();
        if (areaOptional.isPresent()) {
            return areaOptional.get();
        }
        return null;
    }

    public  List<DSFLAreas> getAreasContainingPoint(double lat, double lng) {
        if (!session.getTransaction().isActive()) {
            throw new IllegalArgumentException("Geoprovisioningservice: session has no active transaction");
        }
        List<DSFLAreas> areas = (List<DSFLAreas>) session.createSQLQuery("select area_id, name_of_area from areas_in_dk where "
                        + "st_within(st_geomfromtext('POINT("
                        + Double.toString(lat) + " "
                        + Double.toString(lng)
                        + ")',0),ST_GeomFromEWKT(polygon_col))")
                .list().stream().map(row -> {
                    return Areas.getAreaEnumByName(((Object[])row)[1].toString());
                }).collect(Collectors.toList());
        return areas;
    }
}

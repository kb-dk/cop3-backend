package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.constants.DSFLAreas;
import dk.kb.cop3.backend.crud.database.HibernateUtil;
import dk.kb.cop3.backend.crud.exception.AreaNotFoundException;
import dk.kb.cop3.backend.crud.services.GeoProvisioningService;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.*;
import java.util.Arrays;
import java.util.List;

@Path("/geo-services")
public class GeoService {
    static final Logger LOGGER = LoggerFactory.getLogger(GeoService.class);

    /**
     *
     * POST http://localhost:8080/cop/geo-services/get-area
     * Content-Type: application/x-www-form-urlencoded
     *
     * lat=55.6761&lng=12.5683
     *
     * Response:
     * [ 3, Hovedstaden ]
     *
     */
    @POST
    @Path("/get-area")
    @Produces("application/json")
    public String post(@FormParam("lat") double latitude, @FormParam("lng") double longitude) {
        return getArea(latitude, longitude);
    }

    /**
     *
     * GET http://localhost:8088/cop/geo-services/area?lat=55.6761&lng=12.5683
     * Accept: application/json
     *
     * Response:
     * [ 3, Hovedstaden ]
     *
     */
    @GET
    @Path("/area")
    @Produces("application/json")
    public String get(@QueryParam("lat") double latitude, @QueryParam("lng") double longitude) {
        return getArea(latitude, longitude);
    }

    private String getArea(@FormParam("lat") double latitude, @FormParam("lng") double longitude) {
        LOGGER.debug("GEOSERVICE POST GET lat:" + latitude + " longitude " + longitude );
        Session session = HibernateUtil.getSessionFactory().openSession();
        GeoProvisioningService geoProvisioningService = new GeoProvisioningService(session);
        DSFLAreas area = geoProvisioningService.getAreaNotDanmark(latitude,longitude);
        return area.toString();
    }
}

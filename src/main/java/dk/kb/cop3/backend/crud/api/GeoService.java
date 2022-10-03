package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.crud.exception.AreaNotFoundException;
import dk.kb.cop3.backend.crud.services.GeoProvisioningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import java.util.Arrays;

/**
 * kb.dk
 *
 * @author jatr
 *         Date: 14/10/13
 *         Time: 13:33
 */
@Path("/geo-services")
public class GeoService {

    static final Logger LOGGER = LoggerFactory.getLogger(GeoService.class);

    @POST
    @Path("/get-area")
    @Produces("application/json")
    public String post(@FormParam("lat") double latitude, @FormParam("lng") double longitude) {
        LOGGER.debug("GEOSERVICE POST GET lat:" + latitude + " longitude " + longitude );

        GeoProvisioningService geoProvisioningService = new GeoProvisioningService();
        Object[] areaDetails;
        try {
            areaDetails = geoProvisioningService.getAreaDetailsForPoint(latitude, longitude);
        } catch (AreaNotFoundException e) {
            LOGGER.error(e.getMessage());
            return e.getMessage();
        }

        return Arrays.toString(areaDetails);
    }

    @GET
    @Path("/area")
    @Produces("application/json")
    public String get(@QueryParam("lat") double latitude, @QueryParam("lng") double longitude) {
        LOGGER.debug("GEOSERVICE GET lat:" + latitude + " longitude " + longitude );
        GeoProvisioningService geoProvisioningService = new GeoProvisioningService();
        Object[] areaDetails;
        try {
            areaDetails = geoProvisioningService.getAreaDetailsForPoint(latitude, longitude);
        } catch (AreaNotFoundException e) {
            LOGGER.error(e.getMessage());
            return e.getMessage();
        }

        return Arrays.toString(areaDetails);
    }
}

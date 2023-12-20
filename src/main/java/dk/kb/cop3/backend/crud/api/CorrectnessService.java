package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.crud.database.CorrectnessDAO;
import dk.kb.cop3.backend.crud.database.CorrectnessDAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/correctness")
public class CorrectnessService {

    private static final Logger logger = LoggerFactory.getLogger(CorrectnessService.class);


    @GET
    @Path("/{medium}/{collection}/{year}/{month}/{edition}/{id}")
    @Produces("application/xml")
    public Response getCorrectnessValue(@PathParam("medium") String medium,
                                 @PathParam("collection") String collection,
                                 @PathParam("year") int year,
                                 @PathParam("month") String month,
                                 @PathParam("edition") String edition,
                                 @PathParam("id") String id) {

        logger.debug("Request received");

        String objectUri = convertToObjectUri(medium, collection, year, month, edition, id);

        try {
            CorrectnessDAO correctDAO = new CorrectnessDAOImpl();
            double correctnessValue = correctDAO.getCorrectness(objectUri);
            logger.debug("Correctness value: " + correctnessValue);
            String correctnessResponseXml = createLikeResponseXml(correctnessValue);
            return Response.ok(correctnessResponseXml).build();
        } catch (Exception e) {
            logger.error("Could not get correctness", e);
            return Response.serverError().build();
        }

    }

    private String convertToObjectUri(String medium, String collection, int year, String month, String edition, String id) {
        return String.format("/%s/%s/%d/%s/%s/%s", medium, collection, year, month, edition, id);
    }

    private String createLikeResponseXml(double correctnessValue) {
        String correctnessResponse = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<CorrectResponse>" +
                "   <CorrectDecimal>" +
                correctnessValue +
                "   </CorrectDecimal>" +
                "</CorrectResponse>";
        return correctnessResponse;
    }

}

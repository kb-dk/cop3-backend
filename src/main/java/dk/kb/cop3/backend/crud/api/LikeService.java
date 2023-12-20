package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.crud.database.LikeDAO;
import dk.kb.cop3.backend.crud.database.LikeDAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/like")
public class LikeService {

    private static final Logger logger = LoggerFactory.getLogger(LikeService.class);

    @POST
    @Path("/{medium}/{collection}/{year}/{month}/{edition}/{id}")
    @Produces("application/xml")
    public Response addLike(@PathParam("medium") String medium,
                            @PathParam("collection") String collection,
                            @PathParam("year") int year,
                            @PathParam("month") String month,
                            @PathParam("edition") String edition,
                            @PathParam("id") String id) {

        logger.debug("Request received");

        String objectUri = convertToObjectUri(medium, collection, year, month, edition, id);

        try {
            LikeDAO likeDAO = new LikeDAOImpl();
            int likes = likeDAO.increaseNumberOfLikesWithOne(objectUri);
            logger.debug("Number of likes: " + likes);
            String likeResponseXml = createLikeResponseXml(likes);
            return Response.ok(likeResponseXml).build();
        } catch (Exception e) {
            logger.error("Could not +1 the number of likes", e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/{medium}/{collection}/{year}/{month}/{edition}/{id}")
    @Produces("application/xml")
    public Response getLikeCount(@PathParam("medium") String medium,
                                 @PathParam("collection") String collection,
                                 @PathParam("year") int year,
                                 @PathParam("month") String month,
                                 @PathParam("edition") String edition,
                                 @PathParam("id") String id) {

        logger.debug("Request received");

        String objectUri = convertToObjectUri(medium, collection, year, month, edition, id);

        try {
            LikeDAO likeDAO = new LikeDAOImpl();
            int likes = likeDAO.getNumberOfLikes(objectUri);
            logger.debug("Number of likes: " + likes);
            String likeResponseXml = createLikeResponseXml(likes);
            return Response.ok(likeResponseXml).build();
        } catch (Exception e) {
            logger.error("Could not get the number of likes", e);
            return Response.serverError().build();
        }

    }

    private String convertToObjectUri(String medium, String collection, int year, String month, String edition, String id) {
        return String.format("/%s/%s/%d/%s/%s/%s", medium, collection, year, month, edition, id);
    }

    private String createLikeResponseXml(int likeCount) {
        String likeResponse = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<LikeResponse>" +
                "   <LikeCount>" +
                "       %d" +
                "   </LikeCount>" +
                "</LikeResponse>";
        return String.format(likeResponse, likeCount);
    }

}

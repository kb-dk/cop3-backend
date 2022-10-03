package dk.kb.cop3.backend.crud.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.kb.cop3.backend.crud.database.StatisticsDao;
import dk.kb.cop3.backend.crud.database.StatisticsDaoImpl;
import dk.kb.cop3.backend.crud.model.StatisticsForAnEditionPublic;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * @author: Andreas B. Westh
 * Date: 8/23/12
 * Time: 13:07 PM
 */

@Path("/statistics")
public class StatisticsService {

    static final Logger LOGGER = Logger.getLogger(StatisticsService.class);



       @GET
       @Path("/info/")
       @Produces("text/plain")
       public Response getNumberOfElementsInEdition() {
           LOGGER.info("Statistics info service COP-02 - Backend");
           return Response.ok("sample request: http://host:port/cop/statistics/correct/?eid=luftfo&cid=subject205  \n Danish chars: æøåÆØÅ.").build(); // Unsupported type
       }


    /**
     * Returns statistics object in json format for an edition and an optionel category
     * http://localhost:8080/cop/statistics/correct/?eid=luftfo&cid=123
     * http://localhost:8080/cop/statistics/correct/?eid=luftfo&cid=subject205
     * @param editionId   i.e. luftfo
     * @param categoryId   i.e. subject205 (thats sylvest jensen)
     * @return    json formatted statistics
     * {
       "editionID": "luftfo",
       "categoryID": "subject205",
       "noOfCobjects": 53487,
       "noOfCobjectsPlacedCorrect": 4,
       "noOfCobjectsPlacedIncorrect": 0,
       "noOfCobjectsPlacedCorrectPercentage": "0.0%",
       "noOfCobjectsInCategory": 29353,
       "noOfCobjectsInCategoryCorrect": 3,
       "noOfCobjectsPlacedCategoryCorrectPercentage": "0.0%"
     }
     */
    @GET
    @Path("/correct")
    @Produces("application/json")
    public Response getStatistics(@DefaultValue("luftfo") @QueryParam("eid") String editionId,
                                @DefaultValue("") @QueryParam("cid") String categoryId) {
        StatisticsForAnEditionPublic response = new StatisticsForAnEditionPublic();

        LOGGER.info("get statistics: " + editionId);
        StatisticsDao dao = new StatisticsDaoImpl();
        try {
             response = dao.getStatistics(editionId,categoryId);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //Encode the response as JSON
        Gson gson = new GsonBuilder().create();

        //Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
        String responseJson = gson.toJson(response);
        final Response myResponse = Response.status(Response.Status.OK)
                .entity(responseJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8") //"; charset=ISO-8859-1"
                .build();
            return myResponse;

    }


    /**
     * Returns statistics object in json format for ALL edition and groups by area.
     * http://localhost:8080/cop/statistics/all/
     *
     * @return    json formatted statistics
     */
    @GET
    @Path("/all")
    @Produces("application/json")
    public Response getAllStatistics() {
        Map response = null;

        LOGGER.info("get All statistics: " );
        StatisticsDao dao = new StatisticsDaoImpl();
        try {
             response = dao.getAllStatistics();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //Encode the response as JSON
        Gson gson = new GsonBuilder().create();

        //Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
        String responseJson = gson.toJson(response.values());
        final Response myResponse = Response.status(Response.Status.OK)
                .entity(responseJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8") //"; charset=ISO-8859-1"
                .build();
            return myResponse;

    }
}

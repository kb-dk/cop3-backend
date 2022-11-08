package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.crud.database.*;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.crud.update.Reformulator;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

// The class binds to /syndication
@Path("/update")
public class UpdateService {
    private static Logger logger = Logger.getLogger(UpdateService.class);
    /**
     * Post-service. Receives updated fields or geoPoint
     * POST http://localhost:8080/cop/update/uri
     *
     * @return
     */
    @POST
    @Path("/{medium}/{collection}/{year}/{month}/{edition}/{id}")
    public Response post(@PathParam("medium") String medium,
                         @PathParam("collection") String collection,
                         @PathParam("year") int year,
                         @PathParam("month") String month,
                         @PathParam("edition") String edition,
                         @PathParam("id") String id,
                         @FormParam("user") String user,
                         @FormParam("lastmodified") String lastModified,
                         @FormParam("lat") double lat,
                         @FormParam("lng") double lng,
                         @FormParam("correctness") double correctness,
                         @FormParam("title") String title,
                         @FormParam("person") String person,
                         @FormParam("building") String building,
			             @FormParam("parish") String parish,
			             @FormParam("street") String street,
                         @FormParam("housenumber") String housenumber,
                         @FormParam("zipcode") String zipcode,
                         @FormParam("cadastre") String cadastre,
                         @FormParam("area") String area,
                         @FormParam("city") String city,
                         @FormParam("location") String location,
                         @FormParam("note") String note,
                         @FormParam("pdfidentifier") String pdfidentifier,
                         @FormParam("orientation") String orientation,
                         @FormParam("creator") String creator,
                         @FormParam("dateCreated") String dateCreated,
                         @FormParam("genre") String genre,
                         @FormParam("iiifIdentifier") String iiifIdentifier,
                         @FormParam("imageIdentifier") String imageIdentifier,
                         @FormParam("thumbnailIdentifier") String thumbnailIdentifier
    ) {

        String uri = "/" + medium + "/" + collection + "/" + year + "/" + month + "/" + edition + "/" + id;
        logInputParams(user, lastModified, lat, lng, correctness, title, person, building, parish, street, housenumber, zipcode, cadastre, area, city, location, note, orientation, iiifIdentifier, imageIdentifier, thumbnailIdentifier, uri);

        if ((user == null) || "".equals(user)) {
            logger.warn("update service no user given");
            return Response.status(400).build();
        }
        String current_mods = getCurrentMods(uri);
        if (current_mods == null){
            return  Response.status(404).build();
        }

        try {
            if (lastModified == null) {
                if ((lat != 0.0) && (lng != 0.0)) {
                    return updateCoordinatesAndGetHttpReturnCode(user, lat, lng, correctness, uri);
                }
            } else {
                boolean doModsUpdate = false;
                Reformulator reformulator = new Reformulator(current_mods);
                doModsUpdate = updateChangedFields(lat, lng, title, person, building, parish, street, housenumber, zipcode, cadastre, area, city, location, note, pdfidentifier, orientation, creator, dateCreated, genre, iiifIdentifier, imageIdentifier, thumbnailIdentifier, doModsUpdate, reformulator);

                if (doModsUpdate) {
                    String new_mods = reformulator.commitChanges();
                    if (new_mods == null || "".equals(new_mods)){
                        return Response.serverError().build();
                    }
                    Session ses = HibernateUtil.getSessionFactory().getCurrentSession();
                    MetadataWriter mdw = new HibernateMetadataWriter(ses);
                    String result = mdw.updateFromMods(uri, new_mods, lastModified, user);
                    return getHttpResponseCodeAndUpdateSolr(uri, result);
                }
            }
        } catch (HibernateException ex) {
            logger.error("hibernate error in updateservice", ex);
        }
        return Response.serverError().build();//If we end here an (Hibernate-) Exception has occurred
    }

    private Response updateCoordinatesAndGetHttpReturnCode(String user, double lat, double lng, double correctness, String uri) {
        Session ses = HibernateUtil.getSessionFactory().getCurrentSession();
        MetadataWriter mdw = new HibernateMetadataWriter(ses);
        String result = mdw.updateGeo(uri, lat, lng, user, null, correctness);
        return getHttpResponseCodeAndUpdateSolr(uri, result);
    }

    private Response getHttpResponseCodeAndUpdateSolr(String uri, String result) {
        if (result == null || result.equals("")) {
            return Response.notModified("not modified").build();
        } else if (result.equals("out-of-date")){
            return Response.notModified("out-of-date").build();
        } else {
            sendToSolr(uri);
            return Response.ok("Updated").build();
        }
    }


    private boolean updateChangedFields(double lat, double lng, String title, String person, String building, String parish, String street, String housenumber, String zipcode, String cadastre, String area, String city, String location, String note, String pdfidentifier, String orientation, String creator, String dateCreated, String genre, String iiifIdentifier, String imageIdentifier, String thumbnailIdentifier, boolean doModsUpdate, Reformulator reformulator) {
        if (orientation != null && !"".equals(orientation)) {
            reformulator.changeField("orientation", orientation);
            doModsUpdate = true;
        }
        if (title != null && !"".equals(title)) {
            reformulator.changeField("title", title);
            doModsUpdate = true;
        }
        if (person != null && !"".equals(person)) {
            reformulator.changeField("person", person);
            doModsUpdate = true;
        }

        if (building != null && !"".equals(building)) {
            reformulator.changeField("building", building);
            doModsUpdate = true;
        }
        if (city != null && !"".equals(city)) {
            reformulator.changeField("city", city);
            doModsUpdate = true;
        }
        if (parish != null && !"".equals(parish)) {
            reformulator.changeField("parish", parish);
            doModsUpdate = true;
        }
        if (street != null && !"".equals(street)) {
            reformulator.changeField("street", street);
            doModsUpdate = true;
        }
        if (housenumber != null && !"".equals(housenumber)) {
            reformulator.changeField("housenumber", housenumber);
            doModsUpdate = true;
        }
        if (zipcode != null && !"".equals(zipcode)) {
            reformulator.changeField("zipcode", zipcode);
            doModsUpdate = true;
        }
        if (cadastre != null && !"".equals(cadastre)) {
            reformulator.changeField("cadastre", cadastre);
            doModsUpdate = true;
        }
        if (area != null && !"".equals(area)) {
            reformulator.changeField("area", area);
            doModsUpdate = true;
        }
        if (location != null && !"".equals(location)) {
            reformulator.changeField("location", location);
            doModsUpdate = true;
        }
        if (pdfidentifier != null && !"".equals(pdfidentifier)) {
            reformulator.changeField("pdfidentifier", pdfidentifier);
            doModsUpdate = true;
        }
        if (creator != null && !"".equals(creator)) {
            reformulator.changeField("creator", creator);
            doModsUpdate = true;
        }
        if (dateCreated != null && !"".equals(dateCreated)) {
            reformulator.changeField("dateCreated", dateCreated);
            doModsUpdate = true;
        }
        if (genre != null && !"".equals(genre)) {
            reformulator.changeField("genre", genre);
            doModsUpdate = true;
        }

        if (note != null && !"".equals(note)) {
            reformulator.changeField("note", note);
            doModsUpdate = true;
        }
        if (iiifIdentifier != null && !"".equals(iiifIdentifier)) {
            reformulator.changeField("identifier", iiifIdentifier,"iiif");
            doModsUpdate = true;
        }

        if (imageIdentifier != null && !"".equals(imageIdentifier)) {
            reformulator.changeField("identifier", imageIdentifier,"image");
            doModsUpdate = true;
        }

        if (thumbnailIdentifier != null && !"".equals(thumbnailIdentifier)) {
            reformulator.changeField("identifier", thumbnailIdentifier,"thumbnail");
            doModsUpdate = true;
        }
        return doModsUpdate;
    }

    private void logInputParams(String user, String lastModified, double lat, double lng, double correctness, String title, String person, String building, String parish, String street, String housenumber, String zipcode, String cadastre, String area, String city, String location, String note, String orientation, String iiifIdentifier, String imageIdentifier, String thumbnailIdentifier, String uri) {
        logger.debug("updating object " + uri);
        logger.debug("user: "+ user);
        logger.debug("lastModified: "+ lastModified);
        logger.debug("new coordinates lat,lng =" + lat + "," + lng);
        logger.debug("coordinates correctness =" + correctness);
        logger.debug("title: " + title + " note:" + note);
        logger.debug("building: "+ building +" "+"person: "+ person);
        logger.debug("parish: "+ parish +" "+"street: "+ street);
        logger.debug("housenumber: "+ housenumber +" "+"zipcode: "+ zipcode);
        logger.debug("cadastre: "+ cadastre +" "+"area: "+ area);
        logger.debug("city: " + city + " location: "+ location);
        logger.debug("orientation: "+ orientation);
        logger.debug("iiifIdentifier: "+ iiifIdentifier);
        logger.debug("imageIdentifier:"+ imageIdentifier);
        logger.debug("thumbnailIdentifier:"+ thumbnailIdentifier);
    }


    private void sendToSolr(String uri) {
        //TODO call solrizr for uri
    }

    private String getCurrentMods(String uri) {
        Session ses = HibernateUtil.getSessionFactory().getCurrentSession();
        ses.beginTransaction();
        MetadataSource mds = new SolrMetadataSource(ses);
        mds.setSearchterms("id", uri);
        mds.execute();
        if (!mds.hasMore())   {
            logger.debug("no object found with id: "+uri);
            return null;
        }
        Object cObject = mds.getAnother();
        ses.getTransaction().commit();
        return cObject.getMods();
    }

}

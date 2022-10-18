package dk.kb.cop3.backend.crud.api;

import dk.kb.cop3.backend.constants.ConfigurableConstants;
import dk.kb.cop3.backend.crud.database.*;
import dk.kb.cop3.backend.crud.update.Reformulator;
import dk.kb.cop3.backend.crud.util.JMSProducer;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import javax.jms.JMSException;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.HashSet;

// The class binds to /syndication
@Path("/update")
public class UpdateService {
    private static Logger logger = Logger.getLogger(UpdateService.class);

    private ConfigurableConstants consts = ConfigurableConstants.getInstance();

    //
    private static final HashSet<String> EDITABLE_FIELDS =
            new HashSet<String>() {
                {
                    add("title");
                    add("note");
                }
            };


    /**
     * Post-service. Receives geo coordinates and saves them
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
        String cacheId = uri.replaceAll("/", ":");
        String responseLastModFromGeoUpd = "";
        logger.debug("updating object " + uri);
        logger.debug("user: "+user);
        logger.debug("lastModified: "+lastModified);
        logger.debug("new coordinates lat,lng =" + lat + "," + lng);
        logger.debug("coordinates correctness =" + correctness);
        logger.debug("title: " + title + " note:" + note);
        logger.debug("building: "+building+" "+"person: "+person);
        logger.debug("parish: "+parish+" "+"street: "+street);
        logger.debug("housenumber: "+housenumber+" "+"zipcode: "+zipcode);
        logger.debug("cadastre: "+cadastre+" "+"area: "+area);
        logger.debug("city: " + city + " location: "+location);
        logger.debug("orientation: "+orientation);
        logger.debug("iiifIdentifier: "+iiifIdentifier);
        logger.debug("imageIdentifier:"+imageIdentifier);
        logger.debug("thumbnailIdentifier:"+thumbnailIdentifier);


        // Do the housekeeping update the geo position of the object
        if ((user == null) || "".equals(user)) {
            // no user given
            logger.warn("update service no user given");
            return Response.status(400).build();
        }

        try {
            if ((lat != 0.0) && (lng != 0.0)) {
                // Update coordinates
                logger.debug("changing coordinates");
                Session ses = HibernateUtil.getSessionFactory().getCurrentSession();
                MetadataWriter mdw = new HibernateMetadataWriter(ses);

                responseLastModFromGeoUpd = mdw.updateGeo(uri, lat, lng, user,lastModified, correctness);
                if ("out-of-date".equals(responseLastModFromGeoUpd))
                    return Response.notModified("out-of-date").build();
                if (responseLastModFromGeoUpd == null || responseLastModFromGeoUpd.equalsIgnoreCase("") || responseLastModFromGeoUpd.length() <= 1)
                    return Response.notModified().build();
            }


            // Perform  changes to mods
            boolean doModsUpdate = false;
            String current_mods = getCurrentMods(uri);
            logger.debug("current mods is "+current_mods);
            Reformulator reformulator = new Reformulator(current_mods);

            if (current_mods == null)
                return Response.status(404).build();


            if (orientation != null && !"".equals(orientation)) {
                // update orientation info
                reformulator.changeField("orientation", orientation);
                doModsUpdate = true;
            }

            if (title != null && !"".equals(title)) {
                // update title
                reformulator.changeField("title", title);
                doModsUpdate = true;
            }
            if (person != null && !"".equals(person)) {
                // update person (subject->name->namepart)
                reformulator.changeField("person", person);
                doModsUpdate = true;
            }
            if (building != null && !"".equals(building)) {
                // update building subject->hieracicalGeographic->area
                reformulator.changeField("building", building);
                doModsUpdate = true;
            }
	    if (city != null && !"".equals(city)) {
                // update building subject->hieracicalGeographic->city
                reformulator.changeField("city", city);
                doModsUpdate = true;
            }
	    if (parish != null && !"".equals(parish)) {
		// update building subject->hieracicalGeographic->area@areaType=parish
		reformulator.changeField("parish", parish);
		doModsUpdate = true;
	    }
	    if (street != null && !"".equals(street)) {
		// update building subject->hieracicalGeographic->cityPart@cityPartType=street
		reformulator.changeField("street", street);
		doModsUpdate = true;
	    }
	    if (housenumber != null && !"".equals(housenumber)) {
		// update building subject->hieracicalGeographic->cityPart@cityPartType=housenumber
		reformulator.changeField("housenumber", housenumber);
		doModsUpdate = true;
	    }
	    if (zipcode != null && !"".equals(zipcode)) {
		// update building subject->hieracicalGeographic->cityPart@cityPartType=zipcode
		reformulator.changeField("zipcode", zipcode);
		doModsUpdate = true;
	    }
	    if (cadastre != null && !"".equals(cadastre)) {
		// update building subject->hieracicalGeographic->cityPart@cityPartType=cadastre
		reformulator.changeField("cadastre", cadastre);
		doModsUpdate = true;
	    }
	    if (area != null && !"".equals(area)) {
		// update building subject->hieracicalGeographic->cityPart@cityPartType=area
		reformulator.changeField("area", area);
		doModsUpdate = true;
	    }
            if (location != null && !"".equals(location)) {
                // update location subject->geographica
                reformulator.changeField("location", location);
                doModsUpdate = true;
            }
            if (pdfidentifier != null && !"".equals(pdfidentifier)) {
                // update mods => pdfIdentifier
                reformulator.changeField("pdfidentifier", pdfidentifier);
                doModsUpdate = true;
            }
            if (creator != null && !"".equals(creator)) {
                // update mods => pdfIdentifier
                logger.debug("updating creator "+creator);
                reformulator.changeField("creator", creator);
                doModsUpdate = true;
            }
            if (dateCreated != null && !"".equals(dateCreated)) {
                // update mods => pdfIdentifier
                reformulator.changeField("dateCreated", dateCreated);
                doModsUpdate = true;
            }

            if (genre != null && !"".equals(genre)) {
                // update mods => pdfIdentifier
                reformulator.changeField("genre", genre);
                doModsUpdate = true;
            }

            if (lat != 0.0 && lng != 0.0) {
                // update latlng  subject->geographica
                /*            <md:subject xmlns:java="http://xml.apache.org/xalan/java" xmlns:mix="http://www.loc.gov/mix/v10">
                        <md:cartographics>
                            <md:coordinates> */
                reformulator.changeField("latlng", String.valueOf(lat) + ", " + String.valueOf(lng));
                doModsUpdate = true;
            }
            if (note != null && !"".equals(note)) {
                // update title
                reformulator.changeField("note", note);
                doModsUpdate = true;
            }
            if (iiifIdentifier != null && !"".equals(iiifIdentifier)) {
                reformulator.changeField("identifier",iiifIdentifier,"iiif");
                doModsUpdate = true;
            }

            if (imageIdentifier != null && !"".equals(imageIdentifier)) {
                reformulator.changeField("identifier",imageIdentifier,"image");
                doModsUpdate = true;
            }

            if (thumbnailIdentifier != null && !"".equals(thumbnailIdentifier)) {
                reformulator.changeField("identifier",thumbnailIdentifier,"thumbnail");
                doModsUpdate = true;
            }

            if (doModsUpdate) {
                String new_mods = reformulator.commitChanges();
                logger.debug("new mods "+new_mods);
                logger.debug("changing mods " + new_mods);
                if (new_mods == null || "".equals(new_mods))
                    return Response.serverError().build();

                if (lastModified != null) {
                    logger.debug("Trying to UPDATE object " + uri + ", at time: " + lastModified);
                    Session ses = HibernateUtil.getSessionFactory().getCurrentSession();
                    MetadataWriter mdw = new HibernateMetadataWriter(ses);
                    String result;

                    // We did a geoupdate first?
                    if(responseLastModFromGeoUpd  != null && !responseLastModFromGeoUpd .equals("")){
                        result = mdw.updateFromMods(uri, new_mods, responseLastModFromGeoUpd, user);

                    }else{  // we didnt do a geoupdate first.
                        result = mdw.updateFromMods(uri, new_mods, lastModified, user);
                    }
                    if (result == null || result.equals(""))
                        return Response.notModified("not modified").build();
                    if (result.equals("out-of-date"))
                        return Response.notModified("out-of-date").build();
                } else {
                    logger.warn("no lastmodified provided ");
                    return Response.notModified("No lastmodified provided").build();
                }
            }
            try {
                logger.debug("sending message to queue");
                JMSProducer producer = new JMSProducer(
                        this.consts.getConstants().getProperty("cop2.solrizr.queue.host"),
                        this.consts.getConstants().getProperty("cop2.solrizr.queue.update"));
                producer.sendMessage(uri);
                producer.shutDownPRoducer();
                if ("true".equals(this.consts.getConstants().getProperty("cop2.solrizr.queue.copy_messages"))) {
                    producer = new JMSProducer(
                            this.consts.getConstants().getProperty("cop2.solrizr.queue.host"),
                            this.consts.getConstants().getProperty("cop2.solrizr.queue.update")+".copy");
                    producer.sendMessage(uri);
                    producer.shutDownPRoducer();
                }
                logger.debug("message send shutting down");
                logger.debug("done");
            } catch (JMSException ex) {
                logger.error("Unable to connect to solrizr queue "+ex.getMessage());
            }
            return Response.ok("Updated").build();
        } catch (HibernateException ex) {
            logger.error("hibernate error in updateservice", ex);
            return Response.serverError().build();
        }
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

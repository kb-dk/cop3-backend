package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.database.hibernate.AuditTrail;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.crud.util.ObjectFromModsExtractor;
import org.apache.log4j.Logger;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

import java.math.BigDecimal;
import java.util.Date;


public class HibernateMetadataWriter implements MetadataWriter {


    private static Logger logger = Logger.getLogger(HibernateMetadataWriter.class);

    private org.hibernate.Session hibSession;

    public HibernateMetadataWriter(org.hibernate.Session hibSession) {
        this.hibSession = hibSession;
    }

    /*
         creates a new object in the database
     */
    public String create(Object cobject) {
        logger.trace(" saving cobject " + cobject.toString());
        try {
            long startTime = 0;
            if (logger.isDebugEnabled()) {
                startTime = System.nanoTime();
            }
            hibSession.save(cobject);
            if (logger.isDebugEnabled()) {
                logger.debug("Saving object to Oracle took "+((System.nanoTime()-startTime)/10000000)+" seconds");
            }
            return cobject.getLastModified();
        } catch (HibernateException ex) {
            logger.error("cannot create object: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * metode til at opdatere et eksisterende cobject. Forudsætter at 'klienten' har den seneste version af cobjectet som kan identificeres via last modified dato feltet.
     *
     * @param cobject      cobjectet.
     * @param lastmodified tidsstempel.Den dato der vidergives skal matche
     */
    @Override
    public String updateCobject(Object cobject, String lastmodified) {

        try {
            Object existingCobject = (Object) hibSession.get(Object.class, cobject.getId());
            logger.debug("found cobject to be updated " + existingCobject.getId() + ", " + existingCobject.getLastModified() + ", title:" + existingCobject.getTitle());
            if (existingCobject.getLastModified().equals(lastmodified)) {  // the security check... DO we have the latest and greatest version of the copject?
                logger.debug(" Correct last modified date provided. Updating... " + cobject.toString());

                cobject.setLastModified("" + new Date().getTime()); // set the new lastmodified to just now.

                cobject.setObjVersion(existingCobject.getObjVersion().add(new BigDecimal("1")));
                existingCobject = null;
                long startTime = 0;
                if (logger.isDebugEnabled()) {
                    startTime = System.nanoTime();
                }
                hibSession.evict(existingCobject);
                hibSession.merge(cobject); // saving
                if (logger.isDebugEnabled()) {
                    logger.debug("Saving object to Oracle took "+((System.nanoTime()-startTime)/10000000)+" seconds");
                }
                return cobject.getLastModified();

            } else {

                logger.error(" Updating cobject " + cobject.getId() + " failed! Out-of-date. Copject have been updated since retrievel.\n " +
                        "Provided " + lastmodified
                        + " but found " + existingCobject.getLastModified() + " in db");
                return "";
            }

        } catch (HibernateException ex) {
            logger.error("cannot create object: " + ex.getMessage());
            throw ex;
            //TODO: throw Exception
        }
    }


    /*
         updates mods for the object given by cobject
     */
    public void updateMods(Object cobject) {
        try {
            logger.debug("updateMods " + cobject);
            long startTime = 0;
            if (logger.isDebugEnabled()) {
                startTime = System.nanoTime();
            }
            hibSession.update(cobject);
            if (logger.isDebugEnabled()) {
                logger.debug("updating object to Oracle took "+((System.nanoTime()-startTime)/10000000)+" seconds");
            }
        } catch (HibernateException ex) {
            logger.error("cannot create object: " + ex.getMessage());
            //TODO: throw Exception
        }

    }

    @Override
    public String createFromMods(String id, String mods, String user) {
        return createOrUpdateFromMods(id, mods, null, user, true, false);
    }

    @Override
    public String updateFromMods(String id, String mods, String lastModified, String user) {
        return createOrUpdateFromMods(id, mods, lastModified, user, false, true);
    }

    @Override
    public String createOrUpdateFromMods(String id, String mods, String lastModified, String user) {
        return createOrUpdateFromMods(id, mods, lastModified, user, true, true);
    }

    /**
     * Updates or Creates a new Cobject
     *
     * @param id           id of the object to be updated/created
     * @param mods         mods of the object to be updated/created
     * @param lastModified to check if it is the right verions (only relevant for updating)
     * @param user         id of the user doing the update/create
     * @param create       if true a new object is created if none exists
     * @parame update       if true we update the object if it exists
     */

    private String createOrUpdateFromMods(String id, String mods, String lastModified, String user, boolean create, boolean update) {
        try {
            logger.debug("Create or update object id:" + id + 
			 " create:" + create + 
			 " update:" + update + 
			 " lastmodified " + lastModified + 
			 " mods:" + mods);

            hibSession.beginTransaction();
            SQLQuery sqlQuery = hibSession.createSQLQuery("alter session set optimizer_mode=first_rows");
            sqlQuery.executeUpdate();
            logger.debug("id " + id);
            ObjectFromModsExtractor bu = ObjectFromModsExtractor.getInstance();
            if (!id.equals(bu.getIdFromMods(mods))) {
                logger.debug("id does not correspond to mods");
                logger.debug(id + " != " + bu.getIdFromMods(mods));
                return null;
            }
            long startTime = 0;
            if (logger.isDebugEnabled()) {
                startTime = System.nanoTime();
            }
            Object existingCobject = (Object)
                    hibSession.get(Object.class, id);
            if (logger.isDebugEnabled()) {
                logger.debug("Got old object from mods took "+((System.nanoTime()-startTime)/10000000)+" seconds");
            }
            if (existingCobject == null) {
                // create new
                logger.debug("no object found with id " + id);
                if (create) {
                    logger.debug("  ... creating it");
                    Object newCopject =
                            new Object();
                    if (logger.isDebugEnabled()) {
                        startTime = System.nanoTime();
                    }
                    newCopject = bu.extractFromMods(newCopject, mods, hibSession);
                    if (logger.isDebugEnabled()) {
                        logger.debug("extracting new cobject from mods "+((System.nanoTime()-startTime)/10000000)+" seconds");
                    }
                    newCopject.setLastModified("" + new Date().getTime()); // set the new lastmodified to just now.
                    newCopject.setObjVersion(new BigDecimal("0"));
                    newCopject.setLastModifiedBy(user);
                    if (logger.isDebugEnabled()) {
                        startTime = System.nanoTime();
                    }
                    hibSession.save(existingCobject);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Saving object to Oracle took "+((System.nanoTime()-startTime)/10000000)+" seconds");
                    }
                    return newCopject.getLastModified();
                } else {
                    return null;
                }
            } else {
                logger.debug("found existing object ");
                if (update) {
                    logger.debug(" ... doing update");
                    if (existingCobject.getLastModified().equals(lastModified)) {
                        logger.debug(" correct lastModified provided");
                        //Create audittrail bean
                        AuditTrail audit = new AuditTrail(existingCobject.getId() + "-" + existingCobject.getObjVersion(),
                                existingCobject.getId(),
                                existingCobject.getEdition().getId(),
                                existingCobject.getMods(),
                                existingCobject.getLastModified(),
                                existingCobject.getDeleted(),
                                existingCobject.getLastModifiedBy(),
                                existingCobject.getObjVersion(),
                                null);
                        logger.debug(audit.toString());
                        if (logger.isDebugEnabled()) {
                            startTime = System.nanoTime();
                        }
                        existingCobject = bu.extractFromMods(existingCobject, 
							     mods, 
							     existingCobject.getObjVersion().add(new BigDecimal("1")), 
							     hibSession);
                        if (logger.isDebugEnabled()) {
                            logger.debug("Extracting new cobject from mods took "+((System.nanoTime()-startTime)/10000000)+" seconds");
                        }
                        existingCobject.setInterestingess(existingCobject.getInterestingess().add(new BigDecimal("1")));
                        existingCobject.setLastModified("" + new Date().getTime()); // set the new lastmodified to just now.
                        existingCobject.setLastModifiedBy(user);
                        logger.debug("audit version " + audit.getObjVersion() + ", new version " + existingCobject.getObjVersion());
                        if (logger.isDebugEnabled()) {
                            startTime = System.nanoTime();
                        }
                        hibSession.update(existingCobject);
                        hibSession.save(audit);
                        if (logger.isDebugEnabled()) {
                            logger.debug("Updating object to Oracle took "+((System.nanoTime()-startTime)/10000000)+" seconds");
                        }
                        return existingCobject.getLastModified();
                    }
                    logger.error(" Updating cobject " + id + " failed! Out-of-date. Copject have been updated since retrievel.\n " +
                            "Provided " + lastModified
                            + " but found " + existingCobject.getLastModified() + " in db");
                    return "out-of-date";
                }
                return null;
            }
        } catch (HibernateException ex) {
            hibSession.getTransaction().rollback();
            logger.error(" Opdatering af cobjectets metadata kunne ikke udføres " + ex.getMessage());
            throw ex;
        } finally {
            logger.debug("createOrUpdateFromMods: hibSession.getTransaction().isActive() = " + hibSession.getTransaction().isActive());
            if (hibSession.getTransaction().isActive())
                hibSession.getTransaction().commit();
            logger.debug("transaction committed");
        }
    }

    @Override
    public String updateGeo(String id, double lat, double lon, String user, String lastModified, double correctness) {
        logger.debug(" opdaterer geo koordinat " + id + " forårsaget af bruger " + user + " lastmodified:" + lastModified + "nye coordinater (" + lat + "," + lon + ") correctness:" + correctness);
        hibSession.beginTransaction();
        SQLQuery sqlQuery = hibSession.createSQLQuery("alter session set optimizer_mode=first_rows");
        sqlQuery.executeUpdate();
        try {
            long startTime = 0;
            if (logger.isDebugEnabled()) {
                startTime = System.nanoTime();
            }
            Object existingCobject = (Object)
                    hibSession.get(Object.class, id);
            if (logger.isDebugEnabled()) {
                logger.debug("Fetching object from Oracle took "+((System.nanoTime()-startTime)/10000000)+" seconds");
            }
            // For now we allow for lastmodified to be null (not provided)
            if (lastModified == null || existingCobject.getLastModified().equals(lastModified)) {
                logger.debug(" Correct last modified date provided. Updating... ");
                AuditTrail audit = new AuditTrail(existingCobject.getId() + "-" + existingCobject.getObjVersion(),
                        existingCobject.getId(),
                        existingCobject.getEdition().getId(),
                        null,
                        existingCobject.getLastModified(),
                        existingCobject.getDeleted(),
                        existingCobject.getLastModifiedBy(),
                        existingCobject.getObjVersion(),
                        existingCobject.getPoint());


                existingCobject.setLastModified("" + new Date().getTime()); // set the new lastmodified to just now.
                existingCobject.setObjVersion(existingCobject.getObjVersion().add(new BigDecimal("1")));
                existingCobject.setInterestingess(existingCobject.getInterestingess().add(new BigDecimal("1")));
                GeometryFactory geoFactory = JTSFactoryFinder.getGeometryFactory();
                existingCobject.setPoint(geoFactory.createPoint(new Coordinate(Double.valueOf(lat),Double.valueOf(lon))));                existingCobject.setLastModifiedBy(user);
                existingCobject.setCorrectness(new BigDecimal(correctness));
                logger.debug("audit version " + audit.getObjVersion() + ", new version " + existingCobject.getObjVersion());
                logger.debug(audit.toString());
                if (logger.isDebugEnabled()) {
                    startTime = System.nanoTime();
                }
                hibSession.update(existingCobject);
                hibSession.save(audit);
                if (logger.isDebugEnabled()) {
                    logger.debug("Saving object GEO to Oracle took "+((System.nanoTime()-startTime)/10000000)+" seconds");
                }
                return existingCobject.getLastModified();
            } else {
                logger.error(" Updating cobject " + id + " failed! Out-of-date. Copject have been updated since retrievel.\n " +
                        "Provided " + lastModified +
                        " but found " + existingCobject.getLastModified() + " in db");
                return "out-of-date";
            }
        } catch (HibernateException ex) {
            logger.error(" Opdatering af cobjectets geokoordinater kunne ikke udføres " + ex.getMessage());
            hibSession.getTransaction().rollback();
            return null;
        } finally {
            logger.debug("updateGeo: hibSession.getTransaction().isActive() = " + hibSession.getTransaction().isActive());
            if (hibSession.getTransaction().isActive())
                hibSession.getTransaction().commit();
            logger.debug("transaction committed");
        }
    }

}

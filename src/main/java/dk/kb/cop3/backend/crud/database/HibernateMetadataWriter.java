package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.database.hibernate.AuditTrail;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.crud.util.ObjectFromModsExtractor;
import org.apache.log4j.Logger;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.hibernate.HibernateException;

import org.hibernate.Transaction;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import javax.persistence.PersistenceException;
import java.math.BigDecimal;
import java.util.Date;


public class HibernateMetadataWriter implements MetadataWriter {
    private static Logger logger = Logger.getLogger(HibernateMetadataWriter.class);
    private org.hibernate.Session hibSession;
    public HibernateMetadataWriter(org.hibernate.Session hibSession) {
        this.hibSession = hibSession;
    }

    public String create(Object cobject) {
        Transaction transaction = hibSession.beginTransaction();
        try {
            hibSession.save(cobject);
            return cobject.getLastModified();
        } catch (Exception ex) {
            logger.error("cannot create object: " + ex.getMessage());
            transaction.rollback();
            throw ex;
        } finally {
            if (transaction.isActive()) {
                transaction.commit();
            }
        }
    }

    public String createFromMods(String mods) {
        ObjectFromModsExtractor objectFromModsExtractor = ObjectFromModsExtractor.getInstance();
        Transaction trans = hibSession.beginTransaction();
        Object newObjectFromMods = objectFromModsExtractor.extractFromMods(new Object(), mods, hibSession);
        newObjectFromMods.setId("/images/luftfo/2011/maj/luftfoto/objectXXXXXX");
        newObjectFromMods.setTitle("TEST-FOTO");
        try {
            final boolean objectAllreadyExists = hibSession.get(Object.class, newObjectFromMods.getId()) != null;
            if (objectAllreadyExists) {
                // object allready exists
                trans.commit();
                return "conflict";
            }else{
                hibSession.save(newObjectFromMods);
                trans.commit();
                return newObjectFromMods.getId();
            }
        } catch (Exception ex) {
            logger.error("Error creating new cobject",ex);
            if (trans != null && trans.isActive()) {
                trans.rollback();
            }
            return "error";
        }
    }

    /**
     * metode til at opdatere et eksisterende cobject. Forudsætter at 'klienten' har den seneste version af cobjectet som kan identificeres via last modified dato feltet.
     *
     * @param cobject      cobjectet.
     * @param lastmodified tidsstempel.Den dato der vidergives skal matche
     */
    @Override
    @Deprecated //Kaldes tilsyneladende ALDRIG i virkeligheden, da den ikke virkede. Den virker nu...
    public String updateCobject(Object cobject, String lastmodified) {
        try {
            Object existingCobject = hibSession.get(Object.class, cobject.getId());
            final boolean updateIsOk = existingCobject.getLastModified().equals(lastmodified);
            if (updateIsOk) {
                cobject.setLastModified("" + new Date().getTime());
                cobject.setObjVersion(existingCobject.getObjVersion().add(new BigDecimal("1")));
                hibSession.update(cobject);
                return cobject.getLastModified();
            } else {
                logger.error(" Updating cobject " + cobject.getId() + " failed! Out-of-date. Copject have been updated since retrievel.\n " +
                        "Provided " + lastmodified + " but found " + existingCobject.getLastModified() + " in db");
                return "";
            }
        } catch (Exception ex) {
            logger.error("cannot create object: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public String updateFromMods(String id, String mods, String lastModified, String user) {
        ObjectFromModsExtractor bu = ObjectFromModsExtractor.getInstance();
        if (!id.equals(bu.getIdFromMods(mods))) {
            return "ids-do-not-match";
        }
        Transaction transaction = hibSession.beginTransaction();
        try {
            Object existingCobject = hibSession.get(Object.class, id);
            if (existingCobject != null) {
                if (existingCobject.getLastModified().equals(lastModified)) {
                    AuditTrail auditTrail = createAuditTrail(existingCobject, false);
                    Object modifiedCobject = getModifiedObject(mods, user, bu, existingCobject);
                    try {
                        hibSession.update(modifiedCobject);
                        hibSession.save(auditTrail);
                        transaction.commit();
                    } catch (Exception e) {
                        logger.error("Unable to update cobject", e);
                        transaction.rollback();
                        return "error";
                    }
                    return modifiedCobject.getLastModified();
                } else {
                    logger.error(" Updating cobject " + id + " failed! Out-of-date. Copject have been updated since retrievel.\n " +
                            "Provided " + lastModified + " but found " + existingCobject.getLastModified() + " in db");
                    return "out-of-date";
                }
            } else {
                return "id-not-found";
            }
        } finally {
            if (transaction.isActive()) {
                transaction.commit();
            }
        }
    }

    private Object getModifiedObject(String mods, String user, ObjectFromModsExtractor bu, Object existingCobject) {
        Object modifiedCobject = bu.extractFromMods(existingCobject,
                mods,
                existingCobject.getObjVersion().add(new BigDecimal("1")),
                hibSession);
        modifiedCobject.setInterestingess(existingCobject.getInterestingess().add(new BigDecimal("1")));
        modifiedCobject.setLastModified("" + new Date().getTime()); // set the new lastmodified to just now.
        modifiedCobject.setLastModifiedBy(user);
        return modifiedCobject;
    }

    @Override
    public String updateGeo(String id, double lat, double lon, String user, String lastModified, double correctness) {
        Transaction trans = hibSession.beginTransaction();
/*
        NativeQuery sqlQuery = hibSession.createSQLQuery("alter session set optimizer_mode=first_rows");
        sqlQuery.executeUpdate();
*/
        try {
            Object existingCobject = (Object)hibSession.get(Object.class, id);
            // For now we allow for lastmodified to be null (not provided)
            final boolean updateIsOk = lastModified == null || existingCobject.getLastModified().equals(lastModified);
            if (updateIsOk) {
                AuditTrail auditTrail = createAuditTrail(existingCobject, true);
                updateExistingCobjectWitNNewGeopoint(lat, lon, user, correctness, existingCobject);
                hibSession.update(existingCobject);
                hibSession.save(auditTrail);
                return existingCobject.getLastModified();
            } else {
                logger.error(" Updating cobject " + id + " failed! Out-of-date. Copject have been updated since retrievel.\n " +
                        "Provided " + lastModified +  " but found " + existingCobject.getLastModified() + " in db");
                return "out-of-date";
            }
        } catch (Exception ex) {
            logger.error(" Opdatering af cobjectets geokoordinater kunne ikke udføres " + ex.getMessage());
            trans.rollback();
            return "error";
        } finally {
            if (trans.isActive()){
                trans.commit();
            }
        }
    }

    private AuditTrail createAuditTrail(Object existingCobject, boolean onlyUpdateGeopoint) {
        final String mods = onlyUpdateGeopoint? null : existingCobject.getMods();
        final Geometry point = onlyUpdateGeopoint? existingCobject.getPoint(): null;
        AuditTrail auditTrail = new AuditTrail(existingCobject.getId() + "-" + existingCobject.getObjVersion(),
                existingCobject.getId(),
                existingCobject.getEdition().getId(),
                mods,
                existingCobject.getLastModified(),
                existingCobject.getDeleted(),
                existingCobject.getLastModifiedBy(),
                existingCobject.getObjVersion(),
                point);
        return auditTrail;
    }

    private void updateExistingCobjectWitNNewGeopoint(double lat, double lon, String user, double correctness, Object existingCobject) {
        existingCobject.setLastModified("" + new Date().getTime()); // set the new lastmodified to just now.
        existingCobject.setObjVersion(existingCobject.getObjVersion().add(new BigDecimal("1")));
        existingCobject.setInterestingess(existingCobject.getInterestingess().add(new BigDecimal("1")));
        GeometryFactory geoFactory = JTSFactoryFinder.getGeometryFactory();
        existingCobject.setPoint(geoFactory.createPoint(new Coordinate(Double.valueOf(lat),Double.valueOf(lon))));
        existingCobject.setLastModifiedBy(user);
        existingCobject.setCorrectness(new BigDecimal(correctness));
    }
}

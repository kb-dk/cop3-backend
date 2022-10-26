package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.database.hibernate.AuditTrail;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import dk.kb.cop3.backend.crud.util.ObjectFromModsExtractor;
import org.apache.log4j.Logger;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.hibernate.HibernateException;

import org.hibernate.Transaction;
import org.hibernate.engine.transaction.internal.TransactionImpl;
import org.hibernate.query.NativeQuery;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import java.math.BigDecimal;
import java.util.Date;


public class HibernateMetadataWriter implements MetadataWriter {
    private static Logger logger = Logger.getLogger(HibernateMetadataWriter.class);
    private org.hibernate.Session hibSession;
    public HibernateMetadataWriter(org.hibernate.Session hibSession) {
        this.hibSession = hibSession;
    }

    public String create(Object cobject) {
        try {
            Transaction transaction = hibSession.beginTransaction();
            hibSession.save(cobject);
            transaction.commit();
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
        } catch (HibernateException ex) {
            logger.error("cannot create object: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public String updateFromMods(String id, String mods, String lastModified, String user) {
        ObjectFromModsExtractor bu = ObjectFromModsExtractor.getInstance();
        if (!id.equals(bu.getIdFromMods(mods))) {
            return null;
        }
        try {
            hibSession.beginTransaction();
            Object existingCobject = (Object) hibSession.get(Object.class, id);
            if (existingCobject != null) {
                    if (existingCobject.getLastModified().equals(lastModified)) {
                        AuditTrail auditTrail = createAuditTrail(existingCobject, false);
                        existingCobject = bu.extractFromMods(existingCobject,
							     mods,
							     existingCobject.getObjVersion().add(new BigDecimal("1")),
							     hibSession);
                        existingCobject.setInterestingess(existingCobject.getInterestingess().add(new BigDecimal("1")));
                        existingCobject.setLastModified("" + new Date().getTime()); // set the new lastmodified to just now.
                        existingCobject.setLastModifiedBy(user);
                        hibSession.update(existingCobject);
                        hibSession.save(auditTrail);
                        return existingCobject.getLastModified();
                    }else {
                        logger.error(" Updating cobject " + id + " failed! Out-of-date. Copject have been updated since retrievel.\n " +
                                "Provided " + lastModified + " but found " + existingCobject.getLastModified() + " in db");
                        return "out-of-date";
                    }
            }else {
                return null;
            }
        } catch (HibernateException ex) {
            hibSession.getTransaction().rollback();
            logger.error(" Opdatering af cobjectets metadata kunne ikke udføres " + ex.getMessage());
            throw ex;
        } finally {
            if (hibSession.getTransaction().isActive()){
                hibSession.getTransaction().commit();
            }
        }
    }

    @Override
    public String updateGeo(String id, double lat, double lon, String user, String lastModified, double correctness) {
        hibSession.beginTransaction();
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
        } catch (HibernateException ex) {
            logger.error(" Opdatering af cobjectets geokoordinater kunne ikke udføres " + ex.getMessage());
            hibSession.getTransaction().rollback();
            return null;
        } finally {
            if (hibSession.getTransaction().isActive()){
                hibSession.getTransaction().commit();
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

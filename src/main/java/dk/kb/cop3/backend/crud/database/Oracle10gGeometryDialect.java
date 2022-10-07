package dk.kb.cop3.backend.crud.database;
 
import org.hibernate.dialect.Oracle10gDialect;
 
 
public class Oracle10gGeometryDialect extends Oracle10gDialect {
    public Oracle10gGeometryDialect() {
        super();
        registerColumnType(java.sql.Types.OTHER, "MDSYS.SDO_GEOMETRY");
	    registerHibernateType(java.sql.Types.OTHER, "dk.kb.cop2.backend.crud.database.type.JGeometryType");
    }
}


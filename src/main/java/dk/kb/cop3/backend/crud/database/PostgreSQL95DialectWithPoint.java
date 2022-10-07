package dk.kb.cop3.backend.crud.database;

import org.hibernate.dialect.PostgreSQL95Dialect;

public class PostgreSQL95DialectWithPoint extends PostgreSQL95Dialect {
    public PostgreSQL95DialectWithPoint() {
        super();
        registerColumnType(java.sql.Types.OTHER, "Point");
        registerHibernateType(java.sql.Types.OTHER, "dk.kb.cop3.backend.crud.database.type.Point");

    }
}

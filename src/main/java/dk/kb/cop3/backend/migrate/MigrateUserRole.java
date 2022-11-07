package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.migrate.hibernate.UserRoleOracle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class MigrateUserRole {
    public static void main(String[] args) {
        MigrationUtils.initializeMigration();
        Session oraSession = MigrationUtils.getOracleSession();
        SessionFactory psqlSessfac = MigrationUtils.getPostgresSessionFactory();

        List<UserRoleOracle> userRolesOracle = oraSession.createQuery( "from dk.kb.cop3.backend.migrate.hibernate.UserRoleOracle").list();
        userRolesOracle.stream()
                .map(oraUserRole -> ObjectConverter.convertUserRole(oraUserRole))
                .forEach(userRole -> {
                    Session psqkSession = psqlSessfac.openSession();
                    Transaction transaction = psqkSession.beginTransaction();
                    psqkSession.save(userRole);
                    transaction.commit();
                    psqkSession.close();
                });
    }

}

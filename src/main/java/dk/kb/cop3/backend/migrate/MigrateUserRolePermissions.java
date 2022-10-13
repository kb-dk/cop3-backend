package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.migrate.hibernate.UserOracle;
import dk.kb.cop3.backend.migrate.hibernate.UserRolePermissionsOracle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class MigrateUserRolePermissions {
    public static void main(String[] args) {
        Session oraSession = MigrationUtils.getOracleSession();

        SessionFactory psqlSessfac = new Configuration().configure("hibernate.cfg.xml")
                .buildSessionFactory();

        List<UserRolePermissionsOracle> userRolePermissionsOracle = oraSession.createQuery( "from dk.kb.cop3.backend.migrate.hibernate.UserRolePermissionsOracle").list();
        userRolePermissionsOracle.stream()
                .map(oraUserRolePermissions -> ObjectConverter.convertUserRolePermissions(oraUserRolePermissions))
                .forEach(userRolePermissions -> {
                    Session psqkSession = psqlSessfac.openSession();
                    Transaction transaction = psqkSession.beginTransaction();
                    psqkSession.save(userRolePermissions);
                    transaction.commit();
                    psqkSession.close();
                });
    }

}

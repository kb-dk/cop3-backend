package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.migrate.hibernate.UserOracle;
import dk.kb.cop3.backend.migrate.hibernate.UserPermissionsOracle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class MigrateUserPermissions {
    public static void main(String[] args) {
        Session oraSession = MigrationUtils.getOracleSession();

        SessionFactory psqlSessfac = new Configuration().configure("hibernate.cfg.xml")
                .buildSessionFactory();

        List<UserPermissionsOracle> userPermissionsOracle = oraSession.createQuery( "from dk.kb.cop3.backend.migrate.hibernate.UserPermissionsOracle").list();
        userPermissionsOracle.stream()
                .map(oraUserPermission -> ObjectConverter.convertUserPermission(oraUserPermission))
                .forEach(userPermission -> {
                    Session psqkSession = psqlSessfac.openSession();
                    Transaction transaction = psqkSession.beginTransaction();
                    psqkSession.save(userPermission);
                    transaction.commit();
                    psqkSession.close();
                });
    }



    }

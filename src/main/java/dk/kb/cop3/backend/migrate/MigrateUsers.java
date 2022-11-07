package dk.kb.cop3.backend.migrate;

import dk.kb.cop3.backend.migrate.hibernate.UserOracle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import javax.persistence.Query;
import java.util.List;

public class MigrateUsers {

    public static void main(String[] args) {
        MigrationUtils.initializeMigration();
        Session oraSession = MigrationUtils.getOracleSession();
        SessionFactory psqlSessfac = MigrationUtils.getPostgresSessionFactory();

        List<UserOracle> usersOracle = oraSession.createQuery( "from dk.kb.cop3.backend.migrate.hibernate.UserOracle").list();
        usersOracle.stream()
                .map(oraUser -> ObjectConverter.convertUser(oraUser))
                .forEach(user -> {
                    Session psqkSession = psqlSessfac.openSession();
                    Transaction transaction = psqkSession.beginTransaction();
                    psqkSession.save(user);
                    transaction.commit();
                    psqkSession.close();
                });
    }
}

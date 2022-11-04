package dk.kb.cop3.backend.migrate;

public class MigrateDB {
    public static void main(String[] args) {
        CreateDB.main(args);
        MigrateTags.main(args);
        MigrateEditions.main(args);
        MigrateType.main(args);
        MigrateXLink.main(args);
        MigrateCategories.main(args);
        MigrateUserRole.main(args);
        MigrateUsers.main(args);
        MigrateAreasInDk.main(args);
        MigrateObjects.main(args);
        MigrateComments.main(args);
    }
}

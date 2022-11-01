package dk.kb.cop3.backend.crud.database;


import dk.kb.cop3.backend.crud.database.hibernate.Object;

public interface MetadataWriter {


    /*
         creates a new object in the database
     */
    public String create(Object cobject);

    public String createFromMods(String mods);

    /*
         updates mods for the object given by cobject
     */
    public String updateCobject(Object cobject, String lastmodified);

    public String updateFromMods(String id, String mods, String lastModified,String user); //only  update existing objects


    /**
     * update geo position
     *
     *
     * @param id  for cobjectet
     * @param lat latitude
     * @param lon longitude
     * @param user user
     * @param correctness
     * @return
     */

    public String   updateGeo(String id, double lat, double lon, String user, String lastmodified, double correctness);


}

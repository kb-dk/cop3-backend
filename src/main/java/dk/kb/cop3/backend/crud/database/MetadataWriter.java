package dk.kb.cop3.backend.crud.database;


import dk.kb.cop3.backend.crud.database.hibernate.Object;

public interface MetadataWriter {


    /*
         creates a new object in the database
     */
    public String create(Object cobject);

    /*
         updates mods for the object given by cobject
     */
    public String updateCobject(Object cobject, String lastmodified);


    /*
         updates mods for the object given by cobject
     */
    public void updateMods(Object cobject);

    public String createFromMods(String id, String mods, String user); // only create object with id, if it does not exist
    public String updateFromMods(String id, String mods, String lastModified,String user); //only  update existing objects
    public String createOrUpdateFromMods(String id, String mods, String lastModified, String user); // update existing object and create of not exist



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

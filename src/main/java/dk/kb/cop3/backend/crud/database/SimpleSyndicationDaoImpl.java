package dk.kb.cop3.backend.crud.database;

import dk.kb.cop3.backend.crud.database.hibernate.Category;
import dk.kb.cop3.backend.crud.model.SimpleSyndicationResponse;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Andreas B. Westh
 * Date: 8/29/12
 * Time: 20:02 PM
 */
public class SimpleSyndicationDaoImpl implements SimpleSyndicationDao {

    static final Logger logger = Logger.getLogger(SimpleSyndicationDaoImpl.class);

    @Override
    public List<SimpleSyndicationResponse> getCobjects(String eid, String cid) {


        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<SimpleSyndicationResponse> getLatestEditedCobjects(String eid, String cid) throws Exception {
        Session session = null;
          Transaction transaction = null;
          try {
              session = HibernateUtil.getSessionFactory().openSession();
              transaction = session.beginTransaction();

              List<SimpleSyndicationResponse> outputList = new ArrayList<SimpleSyndicationResponse>();

              List cobjects = null;
              if(!cid.equals("")){

                  cobjects = session.createCriteria(
                              Object.class)
                          .add(Restrictions.like("edition.id", "%" +eid+"%"))
                          .createAlias("categories", "cats").add(Restrictions.eq("cats.id", "%" +cid+"%"))
                           .addOrder(Property.forName("lastModified").desc())
                           .setMaxResults(50)
                                  .list();
              }else{
                  cobjects = session.createCriteria(
                              Object.class)
                          .add(Restrictions.like("edition.id", "%" +eid+"%"))
                           .addOrder(Property.forName("lastModified").desc())
                           .setMaxResults(50)
                                  .list();
              }

              transaction.commit();
              if (cobjects != null) {
                  logger.info("cobjects found: " + cobjects.size());
                  for(int j = 0; j < cobjects.size(); j++){
                      SimpleSyndicationResponse simpleCpjct = new SimpleSyndicationResponse();
                      Object tmpCpjt= (Object)cobjects.get(j);
                      simpleCpjct.setId(tmpCpjt.getId());
                      simpleCpjct.setAuthor(tmpCpjt.getLastModifiedBy());
                      simpleCpjct.setTitle(tmpCpjt.getTitle());
                      simpleCpjct.setLastmodified(tmpCpjt.getLastModified());
                      if(!tmpCpjt.getCategories().isEmpty()){
                         try{
                          for (java.lang.Object tmpCat : tmpCpjt.getCategories()) {
                              Category tmpCatHib = (Category) tmpCat;
                              logger.debug(tmpCat.toString());
                              if(tmpCatHib != null && !tmpCatHib.getId().equals("") && !tmpCatHib.getCategoryText().equals("")){
                                simpleCpjct.getCategories().put(""+tmpCatHib.getId(), ""+tmpCatHib.getCategoryText() );
                              }
                          }
                         }catch(Exception e){
                             logger.error("categories " );
                             throw e;
                         }

                          }
                      outputList.add(simpleCpjct);
                      }
                  logger.debug("outputList.size: " + outputList.size());

                  }

              else {
                  logger.error("Could not find a copject with objectUri: ");
                  throw new Exception("Copject not found");
              }
              return outputList;


          } catch (HibernateException e) {
              logger.error("Error while accessing the database", e);
              throw new Exception("Error while accessing the database", e);
          } finally {
              if (session != null && session.isOpen()) {
                  session.close();
              }
          }
    }
}

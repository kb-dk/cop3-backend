package dk.kb.cop3.backend.crud.database;

/**
 * User: abwe
 * Date: 4/15/11
 * Time: 2:41 PM
 */

import dk.kb.cop3.backend.crud.database.hibernate.Category;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.database.hibernate.Type;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


public class HibernateUtilWithoutTomcat  {

    private static final SessionFactory sessionFactory = buildSessionFactory();
    private static Logger myLogger = Logger.getLogger(HibernateUtilWithoutTomcat.class);

    private static SessionFactory buildSessionFactory() {
//          myLogger.debug("Setting up a hibernateUtil for Test SessionFactory!");
        try {
            System.out.println("BuildSessionFactory for test purposes");
            // Create the SessionFactory from hibernate.cfg.xml
            SessionFactory x = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
            return x;

        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            System.exit(1);
            throw new ExceptionInInitializerError(ex);
        }

    }

    public static SessionFactory getSessionFactory() {
        myLogger.debug("get session");
        return sessionFactory;

    }

    @Deprecated
    public static Type getType(Session ses, String text) {
        //Type x = (Type) ses.load(Type.class, new BigDecimal("1"));
        List types = ses.createCriteria(Type.class)
                .add(Restrictions.like("typeText", text + "%"))
                .list();

        if (types == null) {
            myLogger.error("No types found in database related to " + text);
            return null;
        } else if (types.size() == 0) {
            myLogger.error("No types with " + text + " found in database ");
            return null;
        } else if (types.size() != 1) {
            myLogger.error("to many types with " + text + " found in database ");
            return null;
        } else {
            myLogger.debug("Found " + (Type) types.get(0));
            return (Type) types.get(0);
        }

    }

    @Deprecated
    public static Edition getEditionbyUrlname(Session ses, String searchStringtext) {
        //Type x = (Type) ses.load(Type.class, new BigDecimal("1"));
        List editions = ses.createCriteria(Edition.class)
                .add(Restrictions.like("urlName", searchStringtext + "%"))
                .list();

        if (editions == null) {
            myLogger.error("No types found in database related to " + searchStringtext);
            return null;
        } else if (editions.size() == 0) {
            myLogger.error("No edition with " + searchStringtext + " found in database ");
            return null;
        } else if (editions.size() > 1) {
            myLogger.error("to many edition with " + searchStringtext + " found in database ");
            return null;
        } else {
            myLogger.debug("Found " + (Edition) editions.get(0));
            return (Edition) editions.get(0);
        }
    }


    @Deprecated
    public static Object getCobject(Session ses, String id) {
        Object cobject = (Object) ses.load(Object.class, id);
        myLogger.debug(cobject);
        return cobject;
    }

    @Deprecated
    public static Object getCobjectLastModified(Session ses, String id, Date timestamp) {
        Object cobject = (Object) ses.createCriteria(Object.class, id)
                .add(Restrictions.eq("lastModified", timestamp)).uniqueResult();
        myLogger.debug(cobject);
        return cobject;
    }

    public static Category getCategory(Session ses, String searchStringtext) {

        List categories = ses.createCriteria(Category.class)
                .add(Restrictions.like("urlName", searchStringtext + "%"))
                .list();

        if (categories == null) {
            myLogger.error("No types found in database related to " + searchStringtext);
            return null;
        } else if (categories.size() == 0) {
            myLogger.error("No category with " + searchStringtext + " found in database ");
            return null;
        } else if (categories.size() > 1) {
            myLogger.error("too many categories with " + searchStringtext + " found in database ");
            return null;
        } else {
            myLogger.debug("Found " + (Category) categories.get(0));
            return (Category) categories.get(0);
        }
    }


    public static Edition getEditionById(Session ses, String id) {
        Edition ed = (Edition) ses.load(Edition.class, id);

        myLogger.debug("Found " + id + " edition " + ed.getName());
        return ed;
    }

    public static Type getTypeById(Session ses, int id) {
        Type t = (Type) ses.load(Type.class, new BigDecimal(id));

        myLogger.debug("Found " + id + " type " + t);
        return t;
    }

    public static Category getCategoryById(Session ses, String id) {
        Category cat = (Category) ses.load(Category.class, id);

        myLogger.debug("Found " + id + " category " + cat);
        return cat;
    }



    public static Category getCategoryElseCreate(Session ses, String id, String categoryText) {
        myLogger.debug(" ID: " + id + " categoryText: " + categoryText);

        java.lang.Object o = null;

        try {
            //o = ses.load(Category.class, id);
            o = ses.get(Category.class, id);
        } catch (ObjectNotFoundException one) {
            myLogger.info("ObjectNotFoundException ");
            o = null;
        } catch (HibernateException e) {
            myLogger.debug("HibernateException " + e);
            o = null;
        } catch (Exception e) {
            myLogger.debug("Exception " + e);
            o = null;
        }
        if (o == null || !(o instanceof Category)) {
            Category newCategory = new Category();
            newCategory.setId(id);
            newCategory.setCategoryText(categoryText);
            ses.save(newCategory);
            /* ses.getTransaction().commit();
            ses.getTransaction().begin();*/
            myLogger.info("Create new category " + newCategory);
            return newCategory;
        } else {
            Category cat = (Category) o;
            myLogger.debug("Found " + id + " category " + cat);
            return cat;
        }

    }






}

package dk.kb.cop3.backend.crud.database;


import dk.kb.cop3.backend.constants.CopBackendProperties;
import dk.kb.cop3.backend.crud.database.hibernate.Category;
import dk.kb.cop3.backend.crud.database.hibernate.Edition;
import dk.kb.cop3.backend.crud.database.hibernate.Type;
import dk.kb.cop3.backend.crud.database.hibernate.Object;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


public class HibernateUtil {


    private static SessionFactory sessionFactory = buildSessionFactory();
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml")
                    .setProperty(Environment.URL, CopBackendProperties.getDatabaseUrl())
                    .setProperty(Environment.USER,CopBackendProperties.getDatabaseUser())
                    .setProperty(Environment.PASS,CopBackendProperties.getDatabasePassword());
            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            ex.printStackTrace();
        }
      return null;
    }


    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory();
        }
        return sessionFactory;

    }

    @Deprecated
    public static Type getType(Session ses, String text) {
        List types = ses.createCriteria(Type.class)
                .add(Restrictions.like("typeText", text + "%"))
                .list();

        if (types == null) {
            logger.error("No types found in database related to " + text);
            return null;
        } else if (types.size() == 0) {
            logger.error("No types with " + text + " found in database ");
            return null;
        } else if (types.size() != 1) {
            logger.error("to many types with " + text + " found in database ");
            return null;
        } else {
            return (Type) types.get(0);
        }

    }

    @Deprecated
    public static Edition getEditionbyUrlname(Session ses, String searchStringtext) {
        List editions = ses.createCriteria(Edition.class)
                .add(Restrictions.like("urlName", searchStringtext + "%"))
                .list();

        if (editions == null) {
            logger.error("No types found in database related to " + searchStringtext);
            return null;
        } else if (editions.size() == 0) {
            logger.error("No edition with " + searchStringtext + " found in database ");
            return null;
        } else if (editions.size() > 1) {
            logger.error("to many edition with " + searchStringtext + " found in database ");
            return null;
        } else {
            return (Edition) editions.get(0);
        }
    }


    @Deprecated
    public static Object getCobject(Session ses, String id) {
        Object cobject =  ses.get(Object.class, id);
        logger.debug("cobject "+cobject);
        return cobject;
    }

    @Deprecated
    public static Object getCobjectLastModified(Session ses, String id, Date timestamp) {
        Object cobject = (Object) ses.createCriteria(Object.class, id)
                .add(Restrictions.eq("lastModified", timestamp)).uniqueResult();
        logger.debug("coject "+cobject);
        return cobject;
    }

    public static Category getCategory(Session ses, String searchStringtext) {

        List categories = ses.createCriteria(Category.class)
                .add(Restrictions.like("urlName", searchStringtext + "%"))
                .list();

        if (categories == null) {
            logger.error("No types found in database related to " + searchStringtext);
            return null;
        } else if (categories.size() == 0) {
            logger.error("No category with " + searchStringtext + " found in database ");
            return null;
        } else if (categories.size() > 1) {
            logger.error("too many categories with " + searchStringtext + " found in database ");
            return null;
        } else {
            return (Category) categories.get(0);
        }
    }


    public static Edition getEditionById(Session ses, String id) {
        Edition ed = ses.get(Edition.class, id);
        return ed;
    }

    public static Type getTypeById(Session ses, int id) {
        Type t = ses.get(Type.class, new BigDecimal(id));
        return t;
    }


    public static Category getCategoryElseCreate(Session ses, String id, String categoryText) {
        java.lang.Object o;
        try {
            o = ses.get(Category.class, id);
        } catch (ObjectNotFoundException one) {
            logger.info("ObjectNotFoundException ");
            o = null;
        } catch (HibernateException e) {
            logger.debug("HibernateException " + e);
            o = null;
        } catch (Exception e) {
            logger.debug("Exception " + e);
            o = null;
        }
        if (o == null || !(o instanceof Category)) {
            Category newCategory = new Category();
            newCategory.setId(id);
            newCategory.setCategoryText(categoryText);
            ses.save(newCategory);
            return newCategory;
        } else {
            Category cat = (Category) o;
            return cat;
        }
    }
}

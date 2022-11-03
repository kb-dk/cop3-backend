package dk.kb.cop3.backend.commonutils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/**
 * Created by IntelliJ IDEA.
 * User: jac
 * Date: 14-04-11
 * Time: 14:30
 * To change this template use File | Settings | File Templates.
 */
public class DomUtils {

    public static String doc2String(Node doc){

        DOMImplementationRegistry registry = null;
        try {
            registry = DOMImplementationRegistry.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        DOMImplementationLS impl =
            (DOMImplementationLS)registry.getDOMImplementation("LS");
        LSSerializer writer = impl.createLSSerializer();
        return  writer.writeToString(doc);

    }

}

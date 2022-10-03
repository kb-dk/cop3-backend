package dk.kb.cop3.backend.crud.server;

//Log imports

import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


// Jetty imports
// Jersey imports
// sql imports

public class Jetty {
    private static Logger logger = Logger.getLogger(Jetty.class);

    // Configuration of the jetty web server
    final private static int HTTP_PORT = 8080;
    final private static String CONTEXT = "/*";


    public Jetty() {
    }

    public Server serverInitializedOK(int port) throws Exception {

        Server server = new Server(port);

        ServletHolder servletHolder = new ServletHolder(ServletContainer.class);

        servletHolder.setInitParameter("com.sun.jersey.config.property.resourceConfigClass",
                "com.sun.jersey.api.core.PackagesResourceConfig");
        servletHolder.setInitParameter("com.sun.jersey.config.property.packages",
                "dk.kb.cop2.backend.crud.api");

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(servletHolder, CONTEXT);
        server.setHandler(context);
        return server;
    }

    public static void main(String[] args) throws Exception {
        Jetty jetty = new Jetty();
        int myPort = HTTP_PORT;
        try{
            myPort = Integer.parseInt(args[0]);
        } catch (ArrayIndexOutOfBoundsException index){
            logger.debug("can't read custom portNumner from cmd line. Error is " + index.getMessage());
        } catch (NumberFormatException number){
            logger.debug(args[0] + " is not a Number. Using default portnumber. Error is " + number.getMessage());
        }
        Server server = jetty.serverInitializedOK(myPort);
        server.start();
        server.join();
    }
}

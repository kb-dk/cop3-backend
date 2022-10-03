package dk.kb.cop3.backend.crud.util;

import dk.kb.cop3.backend.constants.ConfigurableConstants;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.jms.*;
import java.util.Properties;

/**
 * Created by dgj on 09-05-2017.
 */
public class RetryFailed {

    private static ConfigurableConstants consts = ConfigurableConstants.getInstance();
    Logger logger = configureLog4j();


    public static void main(String args[]) {

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(consts.getConstants().getProperty("cop2.solrizr.queue.host"));
        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        JMSProducer producer = null;

        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(consts.getConstants().getProperty("cop2.solrizr.queue.update")+".failed");
            consumer = session.createConsumer(destination);

            Message message = consumer.receive(1000);
            while(message != null) {
                String id = null;
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    id = textMessage.getText();
                } else {
                    id = message.toString();
                }
                System.err.println("Received failed message: " + id);
                if (id != null && !"".equals(id)) {
                    String[] tokens = id.split("|");
                    if (tokens.length > 1) {
                        System.out.println("GET tokens[0]");
                    }
                }
                message = consumer.receive(1000);
            }

        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }

    }

    private static Logger configureLog4j() {
        String level = "info";
        if (System.getProperty("loglevel") != null ) level = System.getProperty("loglevel");
        String file = "retryfailed.log";
        if (System.getProperty("logfile") != null) file = System.getProperty("logfile");
        Properties props = new Properties();
        props.put("log4j.rootLogger", level+", FILE");
        props.put("log4j.appender.FILE", "org.apache.log4j.DailyRollingFileAppender");
        props.put("log4j.appender.FILE.File",file);
        props.put("log4j.appender.FILE.ImmediateFlush","true");
        props.put("log4j.appender.FILE.Threshold",level);
        props.put("log4j.appender.FILE.Append","true");
        props.put("log4j.appender.FILE.layout", "org.apache.log4j.PatternLayout");
        props.put("log4j.appender.FILE.layout.conversionPattern","[%d{yyyy-MM-dd HH.mm:ss}] %-5p %C{1} %M: %m %n");
        PropertyConfigurator.configure(props);
        Logger logger = Logger.getLogger(SolrizeEdition.class);
        return logger;
    }

}

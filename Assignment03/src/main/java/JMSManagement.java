import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;


public class JMSManagement {
    /**
     * Queue trip data
     */
    static private Queue tripData;

    static {
        try {
            tripData = getSession().createQueue("tripdata");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * Topic distributor
     */
    static private Topic distributor;

    static {
        try {
            distributor = getSession().createTopic("distributor");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public JMSManagement(){
    }

    public static Session getSession(){
        // JMS + ActiveMQ initialisierung
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

        Context naming = null;
        ConnectionFactory connectionFactory = null;
        try {
            naming = new InitialContext(props);
            connectionFactory = (ConnectionFactory) naming.lookup("ConnectionFactory");
        } catch (NamingException e) {
            e.printStackTrace();
        }

        Connection connection = null;
        Session session = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            e.printStackTrace();
        }

        return session;

        // producer.close(); session.close();
        // connection.stop(); connection.close();
    }

    public static Connection getConnection(){
        // JMS + ActiveMQ initialisierung
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

        Context naming = null;
        ConnectionFactory connectionFactory = null;
        try {
            naming = new InitialContext(props);
            connectionFactory = (ConnectionFactory) naming.lookup("ConnectionFactory");
        } catch (NamingException e) {
            e.printStackTrace();
        }

        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
        } catch (JMSException e) {
            e.printStackTrace();
        }

        return connection;
    }

    public static Queue getQueueTripData(){
        return tripData;
    }

    public static Topic getTopicDistributor(){
        return distributor;
    }
}

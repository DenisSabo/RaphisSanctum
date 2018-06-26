import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;


public class JMSManagement {
    // Queues and topics will be created here, so there is no need to create them in each class

    /**
     * Queue trip data
     */
    static private Queue tripData;

    static {
        try {
            tripData = getSession().createQueue("tripdata");
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (NamingException e) {
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
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public JMSManagement() throws NamingException, JMSException {
    }

    public static Session getSession() throws NamingException, JMSException {
        // JMS + ActiveMQ initialisierung
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

        Context naming = new InitialContext(props);
        ConnectionFactory connectionFactory = (ConnectionFactory) naming.lookup("ConnectionFactory");

        Connection connection = connectionFactory.createConnection();

        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        return session;

        // producer.close(); session.close();
        // connection.stop(); connection.close();
    }

    public static Connection getConnection() throws JMSException, NamingException {
        // JMS + ActiveMQ initialisierung
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

        Context naming = new InitialContext(props);
        ConnectionFactory connectionFactory = (ConnectionFactory) naming.lookup("ConnectionFactory");

        Connection connection =  connectionFactory.createConnection();

        return connection;
    }

    public static Queue getQueueTripData(){
        return tripData;
    }

    public static Topic getTopicDistributor(){
        return distributor;
    }
}

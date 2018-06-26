import com.google.gson.Gson;

import javax.jms.*;
import javax.naming.NamingException;


/**
 * intermediate layer between telematics unit and topic "distributor", so some messages can be filtered
 */
public class Filter implements MessageListener{
    private Session session;
    private Queue tripData; private Topic distributor;
    MessageConsumer consumer; MessageProducer producer;

    public void initialize() throws JMSException, NamingException{
        session = JMSManagement.getSession();

        // will read messages out of queue trip data
        tripData = session.createQueue("tripdata");
        consumer = session.createConsumer(tripData);

        // Message listener for queue trip data
        consumer.setMessageListener(this);

        // will write the messages that have been read, into a topic
        distributor = session.createTopic("distributor");
        producer = session.createProducer(distributor);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    }

    public static void main(String[] args) {

    }

    @Override
    public void onMessage(Message message) {
        TextMessage jsonTelematicsMessage = (TextMessage)message;

        // TODO check if message is an alarm message or may be invalid
        TelematicMessage telMessage = null;
        try {
            telMessage = TelematicMessage.deserialize(jsonTelematicsMessage.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }

        // TODO if alarm write into alarm queue

        // else write into topic "distributor"
        String json = telMessage.toJson();
        try {
            // writes message into topic
            TextMessage textMessage = session.createTextMessage(json);
            producer.send(textMessage);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

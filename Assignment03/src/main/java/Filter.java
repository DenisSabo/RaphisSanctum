import javax.jms.*;


/**
 * intermediate layer between telematics unit and topic "distributor", so some messages can be filtered
 */
public class Filter implements MessageListener{

    // JMS
    private static final Session session = JMSManagement.getSession();
    private static final Queue tripData = JMSManagement.getQueueTripData();
    private static final Topic distributor = JMSManagement.getTopicDistributor();
    private static MessageConsumer consumer; private static MessageProducer producer;


    public void initialize(){
        try{
            // will read messages out of queue trip data
            consumer = session.createConsumer(tripData);

            // Message listener for queue trip data
            consumer.setMessageListener(this);

            producer = session.createProducer(distributor);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        } catch(JMSException ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Filter filter = new Filter();
        filter.initialize();
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
        String json = TelematicMessage.serialize(telMessage);
        try {
            // writes message into topic
            TextMessage textMessage = session.createTextMessage(json);
            producer.send(textMessage);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

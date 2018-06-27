import javax.jms.*;
import java.util.Calendar;


/**
 * intermediate layer between telematics unit and topic "distributor", so some messages can be filtered
 */
public class Filter implements MessageListener{

    // JMS
    private static final Session session = JMSManagement.getSession();
    private static final Queue tripData = JMSManagement.getQueueTripData();
    private static final Topic distributor = JMSManagement.getTopicDistributor();
    private static final Queue alarms = JMSManagement.getQueueAlarms();
    private static MessageConsumer consumer;
    private static MessageProducer producerForDistributor;
    private static MessageProducer producerForAlarms;

    /**
     * Always call initialize on instance of this class otherwise messages wont be handled
     */
    public void initialize(){
        try{
            // will read messages out of queue trip data
            consumer = session.createConsumer(tripData);

            // Message listener for queue trip data
            consumer.setMessageListener(this);

            // normal messages will be written into the topic "distributor"
            producerForDistributor = session.createProducer(distributor);
            producerForDistributor.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // alarm messages will be written into queue for alarms
            producerForAlarms = session.createProducer(alarms);
            producerForAlarms.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        } catch(JMSException ex){
            ex.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Filter filter = new Filter();
        filter.initialize(); // message handling
    }

    @Override
    public void onMessage(Message message) {

        TextMessage messageOrAlarm = (TextMessage)message;

        // Can be message or alarm
        TelematicMessage telMessage = null;
        TelematicAlarm telAlarm = null;

        // first try to deserialize as alarm message
        try{
            telAlarm = TelematicAlarm.deserialize(messageOrAlarm.getText());
        }
        catch(JMSException ex){
            ex.printStackTrace();
        }
        if(telAlarm.alarmReason == null){
            // message was actually a TelematicsMessage, because the reason is required
            telMessage = telAlarm;
            telAlarm = null;
        }

        // message was an alarm
        if(telAlarm != null){
            System.out.println("Found an alarm in Queue trip data. Find me on: http://localhost:8161/admin/browse.jsp?JMSDestination=alarms");
            // Write message as json into alarm queue
            String json = TelematicAlarm.serialize(telAlarm);
            try{
                TextMessage textMessage = session.createTextMessage(json);
                producerForAlarms.send(textMessage);

            } catch(JMSException ex){
                ex.printStackTrace();
            }
        }
        // message was an TelematicsMessage
        else if(telMessage != null){
            System.out.println("Writing message into topic distributor.");
            // write into topic "distributor"
            String json = TelematicMessage.serialize(telMessage);
            try {
                // writes message into topic
                TextMessage textMessage = session.createTextMessage(json);
                producerForDistributor.send(textMessage);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        else{ // message was something else or falsy
            System.err.println("Was not able to deserialize message: " + messageOrAlarm.toString());
        }
    }
}

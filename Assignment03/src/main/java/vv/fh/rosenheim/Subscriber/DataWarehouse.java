package vv.fh.rosenheim.Subscriber;

import vv.fh.rosenheim.HelpingClasses.PrettyPrinter;
import vv.fh.rosenheim.HelpingClasses.JMSManagement;
import vv.fh.rosenheim.Telematics.Producer.Produces.TelematicMessage;
import vv.fh.rosenheim.Transport.DrivenDistance;
import com.sun.xml.internal.ws.encoding.soap.DeserializationException;

import javax.jms.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataWarehouse implements MessageListener, Runnable{

    /**
     * This structure will save the driven distance of a telematics in one hour (of a specific date)
     */
    TreeMap<UUID, TreeMap<Calendar, DrivenDistance[]>> dataStructure = new TreeMap<>();

    // JMS
    private static final Connection connection = JMSManagement.getConnection();
    private static final Topic distributor = JMSManagement.getTopicDistributor();
    private static Session session;
    private static MessageConsumer durableTopicConsumer;
    // Other
    private static final Integer printWareHouseAfterXSeconds = 15;


    public void initialize(){
        try{
            // identifiable client for durable subscriber
            Class DataWarehouseClass = this.getClass();
            connection.setClientID(DataWarehouseClass.getName());
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // Durable so data in data-structure will be complete
            durableTopicConsumer = session.createDurableSubscriber(distributor, this.getClass().getName());

            durableTopicConsumer.setMessageListener(this);
        } catch(JMSException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Own thread for printing data in Warehouse
     */
    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(printWareHouseAfterXSeconds * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            printDataWareHouse();
        }
    }


    public static void main(String[] args) {
        DataWarehouse warehouse = new DataWarehouse();
        warehouse.initialize(); // JMS

        // plus thread which will print data in warehouse
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(warehouse);
    }




    /**
     * Message listener, which will add messages to pre-structure first. Then if there are all messages
     * for one hour -> Save into real data-structure and clean entries from pre-structure
     */
    @Override
    public void onMessage(Message message) {
        // get text message which was written into the topic by the Filter.java class
        TextMessage filteredTelematicsMessagesJson = (TextMessage)message;

        // deserialize
        TelematicMessage deserializedMessage = null;
        try {
            deserializedMessage = TelematicMessage.deserialize(filteredTelematicsMessagesJson.getText());
        } catch (JMSException e) {
            e.printStackTrace();
            return;
        }
        if(deserializedMessage == null) throw new DeserializationException("Unable to deserialize Telematics Message");
        addDrivenDistance(deserializedMessage.getTelematicsId(), deserializedMessage.getTimestampAsCalender(),
                deserializedMessage.getTimestampAsCalender().get(Calendar.HOUR_OF_DAY), deserializedMessage.getDrivenDistanceMeters());

    }

    /**
     * Adds driven distances to a specific hour of a telematics on date
     * For example: Truck drove 50.000 meters from 17.00 - 17.50 at 27.06.2018
     * The id of his telematics -> Date: 27.06.2018 -> DrivenDistances[18] = 50.000 meters
     */
    private void addDrivenDistance(UUID telematicsId, Calendar timestampAsCalendar, int hour, Long drivenDistance) {
        // must be 0 or else data-structure will have own node (key) in TreeMap for each Message,
        // because 27.06.2018 18:00:00 is different from 27.06.2018 18:00:01, but we want
        // only one node for a specific date -> Time does not matter at this point, since hour is passed anyways
        timestampAsCalendar.set(Calendar.HOUR_OF_DAY, 00);
        timestampAsCalendar.set(Calendar.MINUTE, 00);
        timestampAsCalendar.set(Calendar.SECOND, 00);
        timestampAsCalendar.set(Calendar.MILLISECOND, 000);

        if(!dataStructure.containsKey(telematicsId)){
            // completly new id
            DrivenDistance[] arr = new DrivenDistance[24];
            arr[hour] = new DrivenDistance(drivenDistance);
            TreeMap<Calendar, DrivenDistance[]> map = new TreeMap<>();
            map.put(timestampAsCalendar, arr);
            // complete new entry is necessary
            dataStructure.put(telematicsId, map);
        }
        else if(!dataStructure.get(telematicsId).containsKey(timestampAsCalendar)){
            // new date
            DrivenDistance[] arr = new DrivenDistance[24];
            arr[hour] = new DrivenDistance(drivenDistance);
            dataStructure.get(telematicsId).put(timestampAsCalendar, arr);
        }
        else if(dataStructure.get(telematicsId).get(timestampAsCalendar)[hour] == null){
            // new hour
            dataStructure.get(telematicsId).get(timestampAsCalendar)[hour] = new DrivenDistance(drivenDistance);
        }
        else{
            // same hour -> add new driven distance to hour
            dataStructure.get(telematicsId).get(timestampAsCalendar)[hour].addDrivenDistance(drivenDistance);
        }

    }


    private double sumDrivenDistancesOfMessages(List<TelematicMessage> messages){
        double sum = 0;
        for(TelematicMessage message : messages){
            sum += message.getDrivenDistanceMeters();
        }
        return sum;
    }


    /**
     * Obtains data about each units driven distances at a all dates for which data exists, and prints
     * the driven distances of each hour in a pretty table
     */
    public void printDataWareHouse() {

        // Table containing the information about driven distances per hour that can be printed by PrettyPrinter
        String[][] prettyDistancesPerHourTable = new String[2][25];

        // First column contains "titles" of table
        prettyDistancesPerHourTable[0][0] = "Hour of Day";
        prettyDistancesPerHourTable[1][0] = "Driven distance";

        // Initialize first row with the 24 hours of the day
        for(int i = 1; i < 25; i++){
            prettyDistancesPerHourTable[0][i] = Integer.toString(i);
        }

        Set<UUID> allIds = dataStructure.keySet();
        // Iterate through all ids
        for(UUID id : allIds){
            System.out.println("Unit: " + id);

            Set<Calendar> allDatesOfId = dataStructure.get(id).keySet();
            //Iterate through all dates of currently iterated ID
            for(Calendar date : allDatesOfId){
                System.out.println("On date: " + date.getTime());
                // Get table with distances for current unit and current date
                DrivenDistance[] distancesForDate = dataStructure.get(id).get(date);
                for(int i = 1; i < 25; i++){
                    if(distancesForDate[i - 1] == null){ // If no entry for hour exists -> Placeholder
                        prettyDistancesPerHourTable[1][i] = "-";
                    }
                    else{
                        // Get value and put it into table that will be printed
                        prettyDistancesPerHourTable[1][i] =  Double.toString(distancesForDate[i - 1].getDistanceInMeters());
                    }
                }
                // pretty print
                final PrettyPrinter printer = new PrettyPrinter(System.out);
                printer.print(prettyDistancesPerHourTable);

            }
        }
    }
}

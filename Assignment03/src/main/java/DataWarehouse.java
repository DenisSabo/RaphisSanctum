import Telematics.DrivenDistance;

import javax.jms.*;
import javax.naming.NamingException;
import java.lang.IllegalStateException;
import java.util.*;


/**
 * Saves driven distance of a telematics unit in an hour of a specific day (date)
 */
public class DataWarehouse implements MessageListener{

    /**
     * This structure will cache messages until there are enough messages for computing driven distance
     * of a telematics unit for an complete hour
     */
    public class PreStructure{
        // Date will be saved too, so the possibility of bugs is reduced
        TreeMap<UUID, HashMap<Date, LinkedList<TelematicMessage>>> cache;

        /**
         * Will return a list of messages IF all messages for one hour of a specific date is in cache already
         * For example: A telematics unit sends messages in a 5 minute interval. The cache has 5 messages of
         * a Unit with an id 1234. The first message has following timestamp: 26.06.2018 19:00.00.000 and the
         * last one: 19:25.00.000 -> This means there are still more messages to be collected for this hour,
         * so list wont be returned
         * @param id of telematics
         * @param date which is meant
         * @returns List with telematics messages, if condition is fulfilled
         */
        public Optional<List<TelematicMessage>> getMessagesOfFullHour(UUID id, Date date){
            // list that will be returned, if there are enough messages for one hour
            LinkedList<TelematicMessage> list = null;

            // get messages for one specific id and date
            LinkedList<TelematicMessage> messages = cache.get(id).get(date);
            Iterator iterator = messages.iterator();

            // get the hour of first message element
            int hour = messages.get(0).getTimestampAsCalender().get(Calendar.HOUR_OF_DAY);

            // will be set to true, if there is an entry in messages, with a date, that is not the same as "hour"
            boolean hourIsFull = false;

            // iterate through all messages
            while(iterator.hasNext()){
                TelematicMessage nextMessage = (TelematicMessage) iterator.next();

                // add message to list, so it may be returned if full
                list.add(nextMessage);

                // get hour of date of current message
                int currentHour = nextMessage.getTimestampAsCalender().get(Calendar.HOUR_OF_DAY);

                // set flag to indicate, that another hour-interval started in messages (means one hour is passed)
                if(currentHour != hour) hourIsFull = true;
            }

            // check if flag for full hour is set
            if(hourIsFull){
                // delete entries for the full hour -> pre-structure should be kept clean, since it is not
                // the real data structure of the data warehouse
                list.forEach(telematicMessage -> {
                    cache.get(id).get(date).remove(telematicMessage);
                });

                // return map
                return Optional.of(list);
            }
            else{
                // hour not fully passed -> driven distance for specific hour cannot be computed fully
                return Optional.empty();
            }
        }

        /**
         * Adds new message to pre-structure.
         */
        public void addMessage(TelematicMessage message){
            // check if id of message is new
            if(!cache.containsKey(message.telematicsId)){
                // new id -> build complete structure
                // list in cache
                LinkedList<TelematicMessage> newList = new LinkedList<>();
                newList.add(message);
                // map in cache
                HashMap<Date, LinkedList<TelematicMessage>> map = new HashMap<>();
                map.put(message.getTimestampAsDate(), newList);
                // put map into cache
                cache.put(message.telematicsId, map);
            }
            else if(!cache.get(message.telematicsId).containsKey(message.getTimestampAsDate())){
                // new date -> new entry for id
                // build structure
                LinkedList<TelematicMessage> messages = new LinkedList<>();
                messages.add(message);

                cache.get(message.telematicsId).put(message.getTimestampAsDate(), messages);
            }
            else{ // just add new message
                cache.get(message.telematicsId).get(message.getTimestampAsDate()).add(message);
            }
        }
    }

    /**
     * This structure will save the driven distance of a telematics in one hour (of a specific date)
     */
    TreeMap<UUID, TreeMap<Date, DrivenDistance[]>> dataStructure = new TreeMap<>();

    PreStructure preStructure = new PreStructure();


    // JMS + MQ Gedöns
    private Connection connection; private Session session;
    Topic distributor; MessageConsumer durableTopicConsumer;

    public void initialize() throws JMSException, NamingException {
        connection = JMSManagement.getConnection();

        // identifiable client for durable subscriber
        connection.setClientID(this.getClass().getName());
        connection.start();

        session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);

        // Data warehouse will read messages from topic "distributor"
        distributor = JMSManagement.getTopicDistributor();

        // Durable so data in data-structure will be complete
        durableTopicConsumer = session.createDurableSubscriber(distributor, this.getClass().getName());

        durableTopicConsumer.setMessageListener(this);
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

        // add message to pre-structure
        preStructure.addMessage(deserializedMessage);

        // check if there is enough data to be converted as entry for the real data-structure
        Optional<List<TelematicMessage>> messagesForRealStructure =
        preStructure.getMessagesOfFullHour(deserializedMessage.telematicsId, deserializedMessage.getTimestampAsDate());

        if(messagesForRealStructure.isPresent()){
            // convert pre-structure's data to an entry in real data-structure
            try {
                addEntryIntoDataStructure(messagesForRealStructure.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Create new entry in data-structure
     * @param messagesForAFullHour Only pass messages, for one complete our
     */
    private void addEntryIntoDataStructure(List<TelematicMessage> messagesForAFullHour) throws Exception {
        // Collect all necessary data for creating entry in data structure
        UUID id = messagesForAFullHour.get(0).telematicsId;
        Date creationDate = messagesForAFullHour.get(0).getTimestampAsDate();
        Integer hour = messagesForAFullHour.get(0).getHourOfDayOfCreation();
        DrivenDistance drivenDistanceInHour = new DrivenDistance(sumDrivenDistancesOfMessages(messagesForAFullHour));

        // data-structure has not even saved the id
        if(isNewId(id)){
            // new tree map
            TreeMap<Date, DrivenDistance[]> map = new TreeMap<>();
            // Complete new entry
            DrivenDistance[] distances = new DrivenDistance[24];
            // one entry in array for specific hour
            distances[hour] = drivenDistanceInHour;
            // one entry in map
            map.put(creationDate, distances);

            // put completely new entry into data structure
            dataStructure.put(id, map);
        }
        else if(isNewDateForId(id, creationDate)){
            // data-structure has no entry for date
            DrivenDistance[] distances = new DrivenDistance[24];
            // one entry in array for specific hour
            distances[hour] = drivenDistanceInHour;

            dataStructure.get(id).put(creationDate, distances);
        }
        else if(dataStructure.get(id).get(creationDate)[hour] == null){
            // should be null, because there is only now enough data for creating new entry for this hour in structure
            // just a knew entry/node at the end of the data-structure
            dataStructure.get(id).get(creationDate)[hour] = drivenDistanceInHour;
        }
        else{
            // Something unexpected happened
            throw new Exception("Entry for hour already exists.");
        }
    }

    private boolean isNewId(UUID id){
        return !this.dataStructure.containsKey(id);
    }


    private boolean isNewDateForId(UUID id, Date date){
        if(isNewId(id)) throw new IllegalStateException("Missing ID in data-structure");
        return !dataStructure.get(id).containsKey(date);
    }

    private double sumDrivenDistancesOfMessages(List<TelematicMessage> messages){
        double sum = 0;
        for(TelematicMessage message : messages){
            sum += message.drivenDistanceMeters;
        }
        return sum;
    }
}

import com.google.gson.Gson;
import com.sun.xml.internal.ws.encoding.soap.DeserializationException;

import javax.jms.*;
import java.io.*;
import java.util.*;

public class DriversLogbook implements MessageListener{
    // Directory to which the listOfMessagesForTelematicsUnit.txt files will be saved
    private static final String listsDirectory = "C://telematicsLists/";
    private static final Gson gson = new Gson();
    // JMS-stuff
    private static final Connection connection = JMSManagement.getConnection();
    private static final Topic distributor = JMSManagement.getTopicDistributor();
    private static Session session;
    private static MessageConsumer durableTopicConsumer;

    // Actually lists are saved locally on hard drive, but this will serve as cache for doing operations on this lists
    static private HashMap<UUID, List<TelematicMessage>> listsForTelematics = new HashMap<>();


    public static void main(String[] args) {
        DriversLogbook logbook = new DriversLogbook();
        logbook.initialize();
    }


    /**
     * Will subscribe an instance of this class as durable subscriber to topic "Distributor"
     */
    public void initialize(){
        try{
            connection.setClientID(this.getClass().getName());
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // Driver's logbook will read ALL messages from topic "distributor"
            durableTopicConsumer = session.createDurableSubscriber(distributor, this.getClass().getName());

            durableTopicConsumer.setMessageListener(this);
        } catch(JMSException ex){
            ex.printStackTrace();
        }


        // Create directory which will hold files containing the list of messages
        new File(listsDirectory).mkdirs();
    }


    /**
     * Message listener, which will save new message to corresponding file in hard drive
     */
    @Override
    public void onMessage(Message message) {
        // get text message which was written into the topic by the Filter.java class
        TextMessage filteredTelematicsMessagesJson = (TextMessage)message;

        // deserialize
        TelematicMessage deserializedMessage = null;
        try {
            deserializedMessage =
                    TelematicMessage.deserialize(filteredTelematicsMessagesJson.getText());

        } catch (JMSException e) {
            e.printStackTrace();
        }
        if(deserializedMessage == null){
            throw new DeserializationException("Have not been able to deserialize JSON");
        }

        // if there is no file create one
        if(!checkFileExists(deserializedMessage.telematicsId)){
            // new file with json of a list containing the message will be written
            writeNewListToHardDisk(deserializedMessage);
        }
        else{
            // append message to the corresponding list saved on the hard drive
            addMessageToListOnHardDrive(deserializedMessage);
        }
    }


    /**
     * Computes complete driven distance of one telematics unit by looking at all existing messages
     */
    public long drivenDistanceOfTelematics(UUID telematicsId) {
        // get corresponding list
        List<TelematicMessage> list = listsForTelematics.get(telematicsId);

        // iterate through list and compute whole driven distance
        Iterator<TelematicMessage> iterator = list.iterator();
        Long drivenDistanceInMeters = 0L;

        while(iterator.hasNext()){
            drivenDistanceInMeters += iterator.next().drivenDistanceMeters;
        }

        return drivenDistanceInMeters;
    }

    /**
     * Writes new list with message as first element to text file
     * @param message that will be in list as first element
     */
    public static void writeNewListToHardDisk(TelematicMessage message){
        // serialize list with first message in it
        List<TelematicMessage> messages = new LinkedList<TelematicMessage>();
        messages.add(message);
        String jsonList = gson.toJson(messages);

        // save list to drive. Name of file is UUID of telematics, which produced this message.
        try {
            PrintWriter out = new PrintWriter(listsDirectory + message.telematicsId + ".txt");

            // writes serialized json-list to file
            out.println(jsonList);

            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * will read content from file(file name information is included in message), deserialize it,
     * append message, serialize it back to json, and write it to hard drive
     * @param message that will be appended to list on hard drive
     */
    public static void addMessageToListOnHardDrive(TelematicMessage message) {
        try {
            String pathToFile = new String(listsDirectory + message.telematicsId + ".txt");
            BufferedReader reader = new BufferedReader(new FileReader(pathToFile));

            // assumption whole list is written in first line of text file
            String jsonList = reader.readLine();

            // close resource
            reader.close();

            // deserialize
            List<TelematicMessage> list = gson.fromJson(jsonList, List.class);

            // append message to existing list
            list.add(message);

            // serialize list back to json
            String listAsJson = gson.toJson(list);

            // Write updated list back to file
            File file = new File(pathToFile);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(listAsJson);

            // close resource
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if file in directory C://telematicsLists, with telematics id as name, exists.
     * @param telematicsId is the name of the text file, containing the list with all messages in it
     */
    public static boolean checkFileExists(UUID telematicsId){
        File mayExist = new File(listsDirectory + telematicsId.toString() + ".txt");
        return mayExist.exists();
    }
}

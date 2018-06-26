import com.sun.xml.internal.ws.encoding.soap.DeserializationException;

import javax.jms.*;
import javax.naming.NamingException;
import java.io.*;
import java.util.*;

public class DriversLogbook implements MessageListener{
    static private String listsDirectory = "C://telematicsLists/";

    // Each telematics unit will have it's own list with messages
    static HashMap<UUID, List<TelematicMessage>> listsForTelematics = new HashMap<>();

    private Connection connection; private Session session;
    Topic distributor; MessageConsumer durableTopicConsumer;


    /**
     * Sets connection, session, and consumer settings for JMS
     */
    public void initialize() throws JMSException, NamingException {
        connection = JMSManagement.getConnection();

        // Client identifizierbar machen
        connection.setClientID(this.getClass().getName());
        connection.start();

        session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);

        // Driver's logbook will read messages from topic "distributor"
        distributor = JMSManagement.getTopicDistributor();
        durableTopicConsumer = session.createDurableSubscriber(distributor, this.getClass().getName());

        durableTopicConsumer.setMessageListener(this);

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
                    Constants.gson.fromJson(filteredTelematicsMessagesJson.getText(), TelematicMessage.class);

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
        String jsonList = Constants.gson.toJson(messages);

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
            List<TelematicMessage> list = Constants.gson.fromJson(jsonList, List.class);

            // append message to existing list
            list.add(message);

            // serialize list back to json
            String listAsJson = Constants.gson.toJson(list);

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

package vv.fh.rosenheim.Subscriber;

import com.google.gson.Gson;
import com.sun.xml.internal.ws.encoding.soap.DeserializationException;
import vv.fh.rosenheim.HelpingClasses.JMSManagement;
import vv.fh.rosenheim.Telematics.Producer.Produces.TelematicMessage;

import javax.jms.*;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DriversLogbook implements MessageListener, Runnable{
    // Directory to which the listOfMessagesForTelematicsUnit.txt files will be saved
    private static final String listsDirectory = "C://telematicsLists/";
    private static final Gson gson = new Gson();
    // JMS-stuff
    private static final Connection connection = JMSManagement.getConnection();
    private static final Topic distributor = JMSManagement.getTopicDistributor();
    private static Session session;
    private static MessageConsumer durableTopicConsumer;

    /**
     * Will subscribe an instance of this class as durable subscriber to topic "Distributor"
     * and create directory in which list for messages will be saved as .txt files
     */
    public void initialize(){
        try{
            Class DriversLogbook = this.getClass();
            connection.setClientID(DriversLogbook.getName());
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

    // Actually lists are saved locally on hard drive, but this will serve as cache for doing operations on this lists
    static private HashMap<UUID, List<TelematicMessage>> listsForTelematics = new HashMap<>();


    public static void main(String[] args) {
        DriversLogbook logbook = new DriversLogbook();
        logbook.initialize();

        Thread printDistances = new Thread(logbook);
        printDistances.start();
    }

    @Override
    public void run() {
        // Print complete driven distance of telematics
        while(true){
            try {
                Thread.sleep(15 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            printDrivenDistances();

        }
    }

    private void printDrivenDistances(){
        // get all files
        File folder = new File(listsDirectory);
        File[] listOfFiles = folder.listFiles();

        for(File file : listOfFiles){
            String pathToFile = new String(listsDirectory + file.getName());
            BufferedReader reader = null;
            String jsonList = null;
            try {
                reader = new BufferedReader(new FileReader(pathToFile));

                // assumption whole list is written in first line of text file
                jsonList = reader.readLine();

                // close resource
                reader.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch(IOException ex){
                ex.printStackTrace();
            }

            // deserialize
            TelematicMessage[] list = gson.fromJson(jsonList, TelematicMessage[].class);

            // calculate driven distance for this messages
            long drivenDistance = drivenDistanceOfTelematics(list);
            System.out.println("Telematics Unit: " + file.getName());
            System.out.println("Driven distance: " + drivenDistance);
        }
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
        if(!checkFileExists(deserializedMessage.getTelematicsId())){
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
    public long drivenDistanceOfTelematics(TelematicMessage[] messages) {
        // iterate through list and compute whole driven distance
        long drivenDistanceInMeters = 0;
        for(int i = 0; i < messages.length; i++){
            drivenDistanceInMeters += messages[i].getDrivenDistanceMeters();
        }

        return drivenDistanceInMeters;
    }

    /**
     * Writes new array with message as first element to text file
     * @param message that will be in list as first element
     */
    public static void writeNewListToHardDisk(TelematicMessage message){
        // serialize list with first message in it
        TelematicMessage[] messages = new TelematicMessage[1];
        messages[0] = message;
        String jsonArr = gson.toJson(messages);

        // save array to drive. Name of file is UUID of telematics, which produced this message.
        try {
            PrintWriter out = new PrintWriter(listsDirectory + message.getTelematicsId() + ".txt");

            // writes serialized json-list to file
            out.println(jsonArr);

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
            String pathToFile = new String(listsDirectory + message.getTelematicsId() + ".txt");
            BufferedReader reader = new BufferedReader(new FileReader(pathToFile));

            // assumption whole list is written in first line of text file
            String jsonArr = reader.readLine();

            // close resource
            reader.close();

            // deserialize
            TelematicMessage[] messages = gson.fromJson(jsonArr, TelematicMessage[].class);

            // append message to existing list
            TelematicMessage[] moreMessages = new TelematicMessage[messages.length + 1];
            moreMessages[messages.length] = message;
            int i = 0;
            for(TelematicMessage message1 : messages){
                moreMessages[i] = messages[i];
                i++;
            }


            // serialize list back to json
            String arrAsJson = gson.toJson(moreMessages);

            // Write updated list back to file
            File file = new File(pathToFile);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(arrAsJson);

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

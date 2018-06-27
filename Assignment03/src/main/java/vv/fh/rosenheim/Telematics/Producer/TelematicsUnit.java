package vv.fh.rosenheim.Telematics.Producer;

import vv.fh.rosenheim.HelpingClasses.JMSManagement;
import vv.fh.rosenheim.Telematics.Producer.Produces.TelematicAlarm;
import vv.fh.rosenheim.Telematics.Producer.Produces.TelematicMessage;
import vv.fh.rosenheim.Transport.Gps;

import javax.jms.*;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;


public class TelematicsUnit implements Runnable{

    // JMS Constants
    private static final Session session = JMSManagement.getSession();
    private static final Queue tripdata = JMSManagement.getQueueTripData();
    private static MessageProducer producer;
    // Other
    private static final Random ran = new Random();
    public static final char ENTER = '\n';
    public static final int EOF = -1;

    /**
     * JMS:
     * Call this method on an instance of this class, or else, no messages can be send
     */
    private static void initialize(){
        try{
            producer = session.createProducer(tripdata);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        } catch(JMSException ex){
            ex.printStackTrace();
        }

    }

    /**
     * Uniquely identifiable number of telematics unit
     */
    UUID id;

    /**
     * Coordinates of vehicle, which will be provided by the telematics unit
     */
    Gps locationOfVehicle;

    /**
     * Driven distance of the vehicle
     */
    Long drivenDistanceMeters = 0L;

    /**
     * settable period of time after which a message will be send
     */
    int waitingPeriodInSeconds;

    /**
     * Random time after which an alarm message will be send
     */
    int sendAlarmInSeconds;

    /**
     * Flag which indicates if alarm should be send or not (From the task I conclude that only one alarm should be sent)
     */
    boolean alarmToBeSend = true;

    /**
     * There should be a preferred direction, or else truck driver would drive in circles
     */
    Gps.Direction preferredDirection = Gps.Direction.randomDirection();;



    // Constructor
    public TelematicsUnit(Gps locationOfVehicle, int waitingPeriodInSeconds) {
        this.id = UUID.randomUUID(); // generates unique id
        this.sendAlarmInSeconds = ran.nextInt(40) + 20; // possible outcome of 20-60
        this.locationOfVehicle = locationOfVehicle;
        this.waitingPeriodInSeconds = waitingPeriodInSeconds;
    }


    /**
     * Sends messages in a certain time interval. Besides that will send an alarm-message once.
     */
    @Override
    public void run() {
        System.out.println("Started monitoring vehicle: " + id);

        // get a random preferred direction, so it looks like driver has a destination
        preferredDirection = Gps.Direction.randomDirection();
        System.out.println("Preferred direction of monitored vehicle: " + preferredDirection);

        // Starting location of vehicle
        System.out.println("Location: " + locationOfVehicle.toString());

        // Will send messages in a settable period of time and will send an alarm in a random period of time (only once)
        while(true) {
            try {
                // alarm was not yet send
                if(alarmToBeSend){
                    if (waitingPeriodInSeconds > sendAlarmInSeconds) {
                        // alarm message has to be send before normal message
                        Thread.sleep(sendAlarmInSeconds * 1000);
                        generateAndSendAlarm(sendAlarmInSeconds, "Accelerated too quickly");

                        // now wait rest of time period for normal messages
                        int timeToWait = waitingPeriodInSeconds - sendAlarmInSeconds;
                        Thread.sleep(timeToWait * 1000);
                        generateAndSendMessage(timeToWait);
                    }
                    else if(sendAlarmInSeconds > waitingPeriodInSeconds){
                        // normal message has to be send before the alarm
                        Thread.sleep(waitingPeriodInSeconds * 1000);
                        generateAndSendMessage(waitingPeriodInSeconds);
                        sendAlarmInSeconds -= waitingPeriodInSeconds;
                    }
                    else { // if(waitingPeriodInSeconds == sendAlarmInSeconds)
                        Thread.sleep(waitingPeriodInSeconds * 1000);
                        generateAndSendMessage(waitingPeriodInSeconds);
                        // Since new driving data was generated already, the passed 0 will assure
                        // that all values stay the same, but still an alarm-message can be send
                        generateAndSendAlarm(0, "Accelerated too quickly");
                    }
                }
                else{ // if there is no alarm to be send -> Just send message
                    Thread.sleep(waitingPeriodInSeconds * 1000);
                    generateAndSendMessage(waitingPeriodInSeconds);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Computes a new realistic gps location, depending on the driven distance
     * @param locationOfVehicle based on this param, the new location will be computed
     * @return a realistic new location
     */
    private Gps drive(Gps locationOfVehicle){

        Gps newLocation = locationOfVehicle;

        // Longer drives can be made
        while(drivenDistanceMeters > 550){
            // if random direction gets calculated here, there is more randomness
            Gps.Direction randomDirection = Gps.Direction.weightedRandomDirection(preferredDirection);
            switch (randomDirection){
                case NORD: newLocation.driveToTheNorthLong();
                case EAST: newLocation.driveToTheEastLong();
                case SOUTH: newLocation.driveToTheSouthLong();
                case WEST: newLocation.driveToTheWestLong();
            }
            // this distance was now covered
            drivenDistanceMeters -= 550;
        }

        // Only shorter drives can be made
        while(drivenDistanceMeters > 35){
            // if random direction gets calculated here, there is more randomness
            Gps.Direction randomDirection = Gps.Direction.weightedRandomDirection(preferredDirection);
            switch (randomDirection) {
                case NORD: newLocation.driveToTheNorthShort();
                case EAST: newLocation.driveToTheEastShort();
                case SOUTH: newLocation.driveToTheSouthShort();
                case WEST: newLocation.driveToTheWestShort();
            }
            // this distance was now covered
            drivenDistanceMeters -= 35;
        }

        return newLocation;
    };


    /**
     * @param waitingPeriod influences the returned driven distance, so is more realistic
     * @returns a theoretically possible value for a driven distance
     */
    private Long getRandomDrivenDistance(int waitingPeriod){
        // driven distance will get a random, but realistic value:
        // Assumption: max distance an truck can drive in one second is 33 meters per second (with 120 km per hour)
        // Example: time period for message = 300s (5 minutes) -> 5 minutes with 120 km/h ->
        // 33 meters/second * 300 = 9900 meters; Possible values = {0; 33; ...; 9900}
        return ThreadLocalRandom.current().nextLong(0, 33) * waitingPeriodInSeconds;
    }

    /**
     * Generates some driving data, and initialize instance of this class with it. Then send message
     * @param waitedPeriodOfTimeSeconds is needed, or else no realistic driving data can be generated
     */
    private void generateAndSendMessage(Integer waitedPeriodOfTimeSeconds){
        System.out.println("Sending message");
        // Generate and save driving data
        drivenDistanceMeters += getRandomDrivenDistance(waitedPeriodOfTimeSeconds);
        Gps newLocationOfVehicle = drive(locationOfVehicle);
        locationOfVehicle = newLocationOfVehicle;
        // Create message
        TelematicMessage message = new TelematicMessage(id, drivenDistanceMeters, locationOfVehicle);
        try{
            sendMessage(message);
        } catch(JMSException ex){
            ex.printStackTrace();
        }
    }


    /**
     * Generate and save driving data. Send alarm with this data.
     */
    private void generateAndSendAlarm(Integer waitedPeriodOfTimeSeconds, String alarmReason){

        System.out.println("Sending alarm");

        // Alarm will be send only one time
        alarmToBeSend = false;

        // Generate data
        drivenDistanceMeters += getRandomDrivenDistance(waitedPeriodOfTimeSeconds);
        Gps newLocationOfVehicle = drive(locationOfVehicle);
        locationOfVehicle = newLocationOfVehicle;

        // Create alarm-message by means of generated data
        TelematicAlarm alarm = new TelematicAlarm(id, drivenDistanceMeters, locationOfVehicle, alarmReason);
        try{
            sendAlarm(alarm);
        } catch(JMSException ex){
            ex.printStackTrace();
        }
    }

    private void sendMessage(TelematicMessage telMessage) throws JMSException{

        String json = TelematicMessage.serialize(telMessage);
        TextMessage message = session.createTextMessage(json);
        producer.send(message);
    }


    private void sendAlarm(TelematicAlarm alarm) throws JMSException{
        String json = TelematicAlarm.serialize(alarm);
        TextMessage message = session.createTextMessage(json);
        producer.send(message);
    }




    public static void main(String[] args) throws NamingException, JMSException, IOException {
        // Initializes this class as producer, or else no messages can be send
        initialize();

        // User input that will be collected
        Integer unitsToStart = 0, waitingTimePeriod = 0;
        try{
            unitsToStart = askUserAndExpectInt("How many Telematics you want to start? Type in a number: ");
        } catch(IllegalArgumentException ex){
            // re-run main, so user can adjust input
            main(args);
        }

        try{
            waitingTimePeriod = askUserAndExpectInt("After how many seconds do you want the units to send a message?");
        } catch(IllegalArgumentException ex){
            // re-run main, so user can adjust input
            main(args);
        }

        ExecutorService executor = Executors.newFixedThreadPool(unitsToStart);

        // Start as many telematics units, as wished by user
        while(unitsToStart > 0){
            executor.submit(new TelematicsUnit(new Gps(47.8674365, 12.10730650000005), waitingTimePeriod));
            unitsToStart--;
        }
    }

    public static Integer askUserAndExpectInt(String messageForUser) throws IllegalArgumentException{
        // Expected input from user
        Integer expected = 0;
        // Message for user
        System.out.println(messageForUser);
        // get user input
        String input = listenToUserInput();
        try{
            expected = Integer.parseInt(input);
        } catch (Exception ex){
            System.err.println("Please type in a number");
            throw new IllegalArgumentException("Integer was expected");
        }
        return expected;
    }



    public static String listenToUserInput(){
        StringBuffer buffer = new StringBuffer();
        // Listening to users input
        InputStream is = System.in;

        while(true)
        {
            try {
                int c = 0;
                while ((c = is.read()) > EOF) {
                    // User input will be added to the buffer
                    if (c == ENTER) break;
                    buffer.append((char) c);
                }
                // convert buffer to string
                String userinput = buffer.toString();
                return userinput;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


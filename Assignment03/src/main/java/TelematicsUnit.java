import com.google.gson.Gson;

import javax.jms.*;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;


// TODO Use executor service
// TODO alles nach todos absuchen
// TODO Alarm-Zeugs einbauen
public class TelematicsUnit implements Runnable{

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
     * There should be a preferred direction, or else truck driver would drive in circles
     */
    Gps.Direction preferredDirection = Gps.Direction.randomDirection();;



    // Constructor
    public TelematicsUnit(Gps locationOfVehicle, int waitingPeriodInSeconds) {
        this.id = UUID.randomUUID(); // generates unique id
        this.locationOfVehicle = locationOfVehicle;
        this.waitingPeriodInSeconds = waitingPeriodInSeconds;
    }



    @Override
    public void run() {
        // get a random preferred direction, so it looks like driver has a destination
        preferredDirection = Gps.Direction.randomDirection();
        System.out.println("Preferred direction: " + preferredDirection);

        System.out.println("Started monitoring vehicle: " + id);
        System.out.println("Location: " + locationOfVehicle.toString());

        while(true){
            try {
                // Thread.sleep() expects milliseconds
                int waitingPeriodInMilliseconds = waitingPeriodInSeconds * 1000;
                Thread.sleep(waitingPeriodInMilliseconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Woke up");
            // get random number for driven distance
            System.out.println("Checking drivenDistance value: " + drivenDistanceMeters);

            drivenDistanceMeters += getRandomDrivenDistance(waitingPeriodInSeconds);
            System.out.println("Driven distance: " + drivenDistanceMeters);

            // simulate some driving and save the location
            Gps newLocationOfVehicle = drive(locationOfVehicle);
            locationOfVehicle = newLocationOfVehicle;

            System.out.println("New location: " + newLocationOfVehicle);

            // generate message
            TelematicMessage message = new TelematicMessage(id, drivenDistanceMeters, locationOfVehicle);

            // send message
            try {
                sendMessage(message);
            } catch (JMSException e) {
                e.printStackTrace();
            } catch (NamingException e) {
                e.printStackTrace();
            }

            this.locationOfVehicle = newLocationOfVehicle;
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


    private static final Session session = JMSManagement.getSession();
    private static final Queue tripdata = JMSManagement.getQueueTripData();
    private static MessageProducer producer;

    /**
     * Sends message to ActiveMQ-server. (http://localhost:8161/admin/index.jsp)
     * @param telMessage containing the id of telematics, the driven distance, location and a timestamp
     */
    private void sendMessage(TelematicMessage telMessage) throws JMSException, NamingException {

        // producer handles sending of messages
        producer = session.createProducer(tripdata);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        // send json as text
        String json = TelematicMessage.serialize(telMessage);
        TextMessage message = session.createTextMessage(json);
        producer.send(message);
    }




    public static void main(String[] args) throws NamingException, JMSException, IOException {
        System.out.println("How many Telematics you want to start? Type in a number: ");
        String input = listenToUserInput();

        Integer unitsToStart = Integer.parseInt(input);

        /**
        ExecutorService executorService =
                new ThreadPoolExecutor(unitsToStart, 20, 999999999999999999L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>());

        List<Callable<TelematicsUnit>> unitsToBeStarted = new ArrayList<>();
         */

        // Run message producer in own thread
        Thread thread = new Thread(new TelematicsUnit(new Gps(47.8674365, 12.10730650000005), 5));
        thread.start();
    }

    // TODO
    public static final char ENTER = '\n';
    public static final int EOF = -1;

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


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class Gps{

    double latitude, longitude;

    public Gps(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * cardinal directions
     */
    public enum Direction {
        NORD, EAST, SOUTH, WEST; // the directions

        // some necessary casting and computations
        private static final List<Direction> VALUES =
                Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        /**
         * @returns a random direction (enum value)
         */
        public static Direction randomDirection()  {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }

        // Chance to get the preferred direction is at 82 %
        // R: pbinom(6, 6, 0.25) - pbinom(0, 6, 0.25) = 0.8220215

        /**
         * probability experiment must be repeated 6 times so chance is at 82%
         * Look up binomial distribution
         * @param preferredDirection 82,2% Chance that this direction will be returned
         */
        public static Direction weightedRandomDirection(Direction preferredDirection) {
            // probability experiment will be repeated 6 times
            boolean gotPreferredDirection = false;
            int i = 0;
            do{
                // there is 6 times the chance, that the preferred direction will be returned
                if(randomDirection() == preferredDirection) return preferredDirection;

            }while(i < 5);
            // 6 time (last time)
            // if preferred direction have not been returned till now, return result of last run
            return randomDirection();
        }
    };

    // Functions that can change the coordinates a little

    /**
     * Rough estimation: Increment second decimal place of latitude,
     * will lead to a change of 500 up to 600 meters northwards, from the start location (linear distance)
     */
    public void driveToTheNorthLong(){
        this.latitude += 0.01;
    }

    /**
     * Rough estimation: Increment second decimal place of longitude,
     * will lead to a change of 500 up to 600 meters eastwards, from the start location (linear distance)
     */
    public void driveToTheEastLong(){
        this.longitude += 0.01;
    }

    /**
     * Rough estimation: Decrement second decimal place of latitude,
     * will lead to a change of 500 up to 600 meters southwards, from the start location (linear distance)
     */
    public void driveToTheSouthLong() {
        this.latitude -= 0.01;
    }

    /**
     * Rough estimation: Decrement third decimal place of longitude,
     * will lead to a change of 30 up to 40 meters westwards, from the start location (linear distance)
     */
    public void driveToTheWestLong(){
        this.longitude -= 0.01;
    }

    /**
     * Rough estimation: Increment third decimal place of latitude,
     * will lead to a change of 30 up to 40 meters northwards, from the start location (linear distance)
     */
    public void driveToTheNorthShort(){
        this.latitude += 0.001;
    }

    /**
     * Rough estimation: Increment second decimal place of longitude,
     * will lead to a change of 30 up to 40 meters eastwards, from the start location (linear distance)
     */
    public void driveToTheEastShort(){
        this.longitude += 0.001;
    }

    /**
     * Rough estimation: Decrement third decimal place of latitude,
     * will lead to a change of 30 up to 40 meters southwards, from the start location (linear distance)
     */
    public void driveToTheSouthShort() {
        this.latitude -= 0.001;
    }

    /**
     * Rough estimation: Decrement third decimal place of longitude,
     * will lead to a change of 30 up to 40 meters westwards, from the start location (linear distance)
     */
    public void driveToTheWestShort(){
        this.longitude -= 0.001;
    }

    @Override
    public String toString() {
        return "Gps{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
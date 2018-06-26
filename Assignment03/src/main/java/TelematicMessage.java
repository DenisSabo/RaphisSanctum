import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.UUID;

public class TelematicMessage{
    /**
     * Each telematics unit has an id, which will be saved in message,
     * so it is possible to know, which telematics unit, created this message
     */
    UUID telematicsId;

    /**
     * Creation time of this message
     */
    String timestamp;

    /**
     * Telematics unit records distance, which was driven in a certain time period
     */
    Long drivenDistanceMeters;

    /**
     * GPS coordinates of the telematics unit
     */
    Gps coordinates;

    public TelematicMessage(UUID telematicsId, Long drivenDistanceMeters, Gps coordinates) {
        this.timestamp = new SimpleDateFormat("dd.MM.yyyy HH:mm.ss.SSS").format(System.currentTimeMillis());
        this.telematicsId = telematicsId;
        this.drivenDistanceMeters = drivenDistanceMeters;
        this.coordinates = coordinates;
    }
}

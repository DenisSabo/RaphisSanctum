
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    /**
     * Can be used to convert date to string and vice versa
     */
    private DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm.ss.SSS");

    public TelematicMessage(UUID telematicsId, Long drivenDistanceMeters, Gps coordinates) {
        this.timestamp = format.format(System.currentTimeMillis());
        this.telematicsId = telematicsId;
        this.drivenDistanceMeters = drivenDistanceMeters;
        this.coordinates = coordinates;
    }

    /**
     * @returns used date format for messages
     */
    public DateFormat getFormat() {
        return format;
    }

    /**
     * @param json which is believed to be a serialzized telematic message
     */
    public static TelematicMessage deserialize(String json){
        // easy json deserialize
        Gson gson = new Gson();
        return gson.fromJson(json, TelematicMessage.class);
    }

    /**
     * Serializes instance to JSON
     */
    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public Date getTimestampAsDate() {
        Date date = null;
        try {
            date = format.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public Calendar getTimestampAsCalender() {
        Calendar cal = null;
        try {
            cal.setTime(getFormat().parse(timestamp));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;
    }

    public Integer getHourOfDayOfCreation() {
        return getTimestampAsCalender().get(Calendar.HOUR_OF_DAY);
    }
}

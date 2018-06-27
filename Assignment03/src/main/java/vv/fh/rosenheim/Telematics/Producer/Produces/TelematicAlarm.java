package vv.fh.rosenheim.Telematics.Producer.Produces;

import com.google.gson.Gson;
import vv.fh.rosenheim.Transport.Gps;

import java.util.UUID;

public class TelematicAlarm extends TelematicMessage {

    String alarmReason;

    public TelematicAlarm(UUID telematicsId, Long drivenDistanceMeters, Gps coordinates, String alarmReason) {
        super(telematicsId, drivenDistanceMeters, coordinates);
        this.alarmReason = alarmReason;
    }

    /**
     * @param json which is believed to be a serialzized telematic message
     */
    public static TelematicAlarm deserialize(String json){
        // easy json deserialize
        Gson gson = new Gson();
        return gson.fromJson(json, TelematicAlarm.class);
    }

    /**
     *
     * @param message that will be converted to JSON
     */
    public static String serialize(TelematicAlarm message){
        Gson gson = new Gson();
        return gson.toJson(message, TelematicAlarm.class);
    }

    public String getAlarmReason() {
        return alarmReason;
    }

    public void setAlarmReason(String alarmReason) {
        this.alarmReason = alarmReason;
    }
}

import com.sun.xml.internal.ws.encoding.soap.DeserializationException;

import javax.jms.*;
import javax.naming.NamingException;
import java.io.File;
import java.util.*;

public class DataWarehouse implements MessageListener{

    Map<Date, ArrayList<TelematicMessage>> map = new HashMap<>();

    // Tag
    // Stunde
    // Gefahrene Strecke in der Stunde
    public class Einheit {
        int hourOfDay;
        int drivenDistance;

        public Einheit(int hourOfDay, int drivenDistance) {
            this.hourOfDay = hourOfDay;
            this.drivenDistance = drivenDistance;
        }
    }

    public class Datenstruktur {
        /**
         * Saves driven distance, for each hour in a specific day
         */
        Map<Date, ArrayList<Einheit>> map = new HashMap<>();


        public Datenstruktur(Map<Date, ArrayList<Einheit>> map) {
            this.map = map;
        }


        /**
         *
         * @param date
         * @param einheit
         */
        public void add(Date date, Einheit einheit){
            if(this.map.get(date).get(einheit.hourOfDay) == null){
                // add einheit to list
                this.map.get(date).add(einheit.hourOfDay, einheit);
            }
            else{
                // will return, because this method does not replace already existing values
                return;
            }


        }

        public void replace(Date date, Einheit einheit){
            // TODO sinnvoll?
        }
    }

    private Connection connection; private Session session;
    Topic distributor; MessageConsumer durableTopicConsumer;

    public void initialize() throws JMSException, NamingException {
        connection = JMSManagement.getConnection();

        // Client identifizierbar machen
        connection.setClientID(this.getClass().getName());
        connection.start();

        session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);

        // Data warehouse will read messages from topic "distributor"
        distributor = JMSManagement.getTopicDistributor();
        durableTopicConsumer = session.createDurableSubscriber(distributor, this.getClass().getName());

        durableTopicConsumer.setMessageListener(this);
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

        // TODO Zwischenspeicher für Messages, bis man eine Stunde zusammen hat

        // TODO dann umwandeln in Datenstruktur
    }
}

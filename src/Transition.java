import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Transition")
public class Transition {
    // class for deserialization of "transition" tags of a XML-file

    // "Class variable/property should always be declared as public"
    // src: https://www.codeproject.com/Articles/487571/XML-Serialization-and-Deserialization-Part-2

    @XmlElement(name = "state_initial")
    public String stateInitial;

    @XmlElement(name = "input_symbol")
    public String inputSymbol;

    @XmlElement(name = "state_final")
    public String stateFinal;

    @XmlElement(name = "output_symbol")
    public String outputSymbol;

    public Transition(){
        // default constructor
    }
}

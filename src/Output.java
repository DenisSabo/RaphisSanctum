import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public class Output {
    // class for deserialization of "Output" tags of a XML-file
    @XmlElement(name = "state_initial")
    public String stateInitial;

    @XmlElement(name = "input_symbol")
    public String inputSymbol;

    @XmlElement(name = "output_symbol")
    public String outputSymbol;

    public Output(){
        // default constructor
    }
}

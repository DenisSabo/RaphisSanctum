import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Transition")
public class Transition {
    // class for deserialization of "transition" tags of a XML-file

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

    // Getter and setter
    /*
    public String getInputSymbol() {
        return this.inputSymbol;
    }

    public String getStateFinal() {
        return this.stateFinal;
    }

    public String getStateInitial() {
        return this.stateInitial;
    }

    public void setInputSymbol(String inputSymbol) {
        this.inputSymbol = inputSymbol;
    }

    public void setStateInitial(String stateInitial) {
        this.stateInitial = stateInitial;
    }

    public void setStateFinal(String stateFinal) {
        this.stateFinal = stateFinal;
    }
    */
}

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;
import java.util.List;

@XmlRootElement(name="Outputs")
public class Outputs {
    // Class for deserialization of "Outputs" in Xml-file
    @XmlElement(name = "Output")
    public List<Output> outputList = new LinkedList<>();

    public Outputs(){
        // default constructor
    }
}
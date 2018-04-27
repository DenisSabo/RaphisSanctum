package xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;
import java.util.List;

@XmlRootElement(name="Transitions")
public class Transitions {
    // Class for deserialization of "transitions" in Xml-file
    @XmlElement(name = "Transition")
    public List<Transition> transitionList = new LinkedList<>();

    public Transitions(){
        // default constructor
    }
}
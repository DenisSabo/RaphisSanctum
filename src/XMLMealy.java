import com.sun.xml.internal.txw2.output.XmlSerializer;
import org.xml.sax.SAXParseException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.*;
import java.util.HashSet;


@XmlRootElement(name="Mealy")
@XmlAccessorType(XmlAccessType.NONE)
public class XMLMealy extends Mealy{

    @XmlAttribute(name="name", required=true)
    private String mealyName;

    @XmlElement(name = "starting_state", required = true)
    private String startingState;

    @XmlList
    // Zustände des Mealy-Automaten
    private HashSet<String> states = new HashSet<>();

    @XmlList
    // Eingabealphabet
    private HashSet<String> inputSymbols = new HashSet<>();

    @XmlList
    // Ausgabealphabet
    private HashSet<String> outputSymbols = new HashSet<>();

    // "Class variable/property should always be declared as public"
    // src: https://www.codeproject.com/Articles/487571/XML-Serialization-and-Deserialization-Part-2
    @XmlElement(name = "Transitions")
    // Wrapper/Parent of all transition elements
    public Transitions transitions = new Transitions();

    // Zustandsuntergangstabelle
    private String transitionTable[][] = {};

    @XmlElement(name = "Outputs")
    public Outputs outputs = new Outputs();

    // Tabelle für die Ausgabefunktion
    private String outputTable[][] = {};


    public XMLMealy(){
        // default constructor
    }

    public static XMLMealy createMealy(String path){
        // TODO file.close();
        try{
            FileInputStream file = new FileInputStream(new File(path));
            JAXBContext ctx= JAXBContext.newInstance(XMLMealy.class);
            // Deserialize
            Unmarshaller u = ctx.createUnmarshaller();
            try{
                XMLMealy k = (XMLMealy) u.unmarshal(file);
                k.initializeTransitionTable();
                k.initializeParent();
                return k;
            }
            catch(UnmarshalException ex){
                System.out.println("Invalid xml file! Please correct.");
                ex.printStackTrace();
                return null;
            }
        }
        catch(FileNotFoundException ex){
            System.out.println("File does not exist. Please type in valid path to Xml file!");
            return null; // TODO
        }
        catch(JAXBException ex){
            ex.printStackTrace();
            return null; // TODO
        }
        catch (IOException e) {
            e.printStackTrace();
            return null; // TODO
        } catch (IllegalXmlFileException e) {
            e.printStackTrace();
            return null; // TODO
        }
        /*catch (IllegalXmlFileException e) {
            e.printStackTrace();
            return null;
        }*/
    }

    // TODO Wie macht man es richtig ?!?!
    private void initializeParent(){
        super.mealyName = mealyName;
        super.states = toState(states);
        super.inputSymbols = toSymbol(inputSymbols);
        super.outputSymbols = toSymbol(outputSymbols);
        super.transitionTable = transitionTable;
        super.outputTable = outputTable;
        super.currentState = new State(startingState);
    }

    // Initializes the transition table, that is currently a empty two dimensional array
    private void initializeTransitionTable() throws IllegalXmlFileException {
        // Note: only use function, after Xml-File-Data was safed in this instance
        // State x Symbol
        /* First row will be initialized with all existing input symbols, and first column with
            all possible states. transitionTable[0][0] is null so I added 1 to the size of both dimensions*/
        transitionTable = new String[states.size() + 1][inputSymbols.size() + 1];
        // Same thing for output table
        outputTable = new String[states.size() + 1][inputSymbols.size() + 1];

        this.transitionTable[0][0] = null; // must be empty, because State x Symbol
        this.outputTable[0][0] = null; // must be empty, because State x Symbol

        // Iterate through input symbols and safe them in first row
        int j = 0;
        for(String symbol : inputSymbols){
            j++;
            transitionTable[0][j] = symbol;
            outputTable[0][j] = symbol;
        }
        // Now iterate through all states and safe them in first column
        int i = 0;
        for(String state : states){
            i++;
            transitionTable[i][0] = state;
            outputTable[i][0] = state;
        }
        // Now we have to safe the results of f:x -> State x Symbol in the "table cells"
        for(Transition t : this.transitions.transitionList){
            // Safes indexes of found elements
            int statePosition = 0;
            int inputSymbolPosition = 0;
            // get initial state, input symbol and final state of this iterate through transitionList
            String stateInitial = t.stateInitial.toString();
            String inputSymbol = t.inputSymbol.toString();
            String stateFinal = t.stateFinal.toString();
            String outputSymbol = t.outputSymbol.toString();

            // Iterating through first column and searching for fitting state
            for(i = 1; i < transitionTable.length; i++){
                if(stateInitial.equals(transitionTable[i][0])){
                    // Safe position of found state
                    statePosition = i;
                    break; // TODO "schlechter Programmierstil" aber gute Lesbarkeit !
                }
            }

            // Iterating through first row and searching for fitting symbol
            // TODO hats nicht gefunden ...
            for(j = 1; j < transitionTable[0].length; j++){
                if(inputSymbol.equals(transitionTable[0][j])){
                    // Safe position of found symbol
                    inputSymbolPosition = j;
                    break;
                }
            }

            // Input symbol was not found in first row or ...
            // ... state was not found in first column and so the xml file "is wrong"
            if(inputSymbolPosition == 0 || statePosition == 0){
                // Illegal Xml-File. Symbol or state used in transition, that is not defined!!!
                if(inputSymbolPosition == 0){
                    // TODO system.out.print trotz geworfenem Fehler????
                    System.out.println("The symbol: '");
                    System.out.print(inputSymbol);
                    System.out.print("' is not defined in the input alphabet.");
                    throw new IllegalXmlFileException();
                }
                if(statePosition == 0){
                    System.out.println("The state: '");
                    System.out.print(stateInitial);
                    System.out.print("' is not defined in the states of your xml file.");
                }
                System.out.println("Please correct.");
                throw new IllegalXmlFileException();

            }
            transitionTable[statePosition][inputSymbolPosition] = stateFinal;
            outputTable[statePosition][inputSymbolPosition] = outputSymbol;
        }
    }

    private static HashSet<State> toState(HashSet<String> states){
        HashSet<State> stateStates = new HashSet<>();
        for(String state : states){
            stateStates.add(new State<String>(state));
        }
        return stateStates;
    }

    private static HashSet<Symbol> toSymbol(HashSet<String> symbols){
        HashSet<Symbol> symbolSymbols = new HashSet<>();
        for(String symbol : symbols){
            symbolSymbols.add(new Symbol<String>(symbol));
        }
        return symbolSymbols;
    }
}

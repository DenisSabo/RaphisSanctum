package xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.*;
import java.util.HashSet;
import mealy.Mealy;
import mealy.State;
import mealy.Symbol;
import myExceptions.IllegalXmlFileException;

@XmlRootElement(name= "Mealy")
@XmlAccessorType(XmlAccessType.NONE)
public class XmlMealy extends Mealy {

    // Attribute will be deserialized from xml-file and safed into this variable
    @XmlAttribute(name="name", required=true)
    private String mealyName;

    @XmlElement(name = "starting_state", required = true)
    private String startingState;

    @XmlList
    private HashSet<String> states = new HashSet<>();

    @XmlList
    private HashSet<String> inputSymbols = new HashSet<>();

    @XmlList
    private HashSet<String> outputSymbols = new HashSet<>();

    @XmlElement(name = "Transitions")
    // Wrapper/Parent of all transition elements (contains output symbols as well)
    private Transitions transitions = new Transitions();

    // Tables for function f:x -> State x Symbol
    private String transitionTable[][] = {};
    private String outputTable[][] = {};


    public XmlMealy(){
        // default constructor
    }

    // Returns a XmlMealy/Mealy, that was created out of a Xml-File
    public static XmlMealy createMealy(String path) throws IllegalXmlFileException{
        FileInputStream file = null;
        try{
            file = new FileInputStream(new File(path));
            JAXBContext ctx= JAXBContext.newInstance(XmlMealy.class);
            // Deserialize
            Unmarshaller u = ctx.createUnmarshaller();
            try{
                XmlMealy k = (XmlMealy) u.unmarshal(file);
                k.initializeTables();
                k.initializeParent();
                if(k == null) throw new IllegalXmlFileException("Invalid Xml-File. Unable to deserialize.");
                return k;
            }
            catch(UnmarshalException ex){
                ex.printStackTrace();
                throw new IllegalXmlFileException("Invalid Xml-File. Unable to deserialize.");
            }
        }
        catch(FileNotFoundException ex){
            System.out.println("File does not exist. Please type in valid path to Xml file!");
            ex.printStackTrace();
        }
        catch(JAXBException ex){
            ex.printStackTrace();
        }
        finally{
            try{
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        // If function does not return a XMLMealy, return null
        return null;
    }

    // TODO Wie macht man es richtig ?!?! Eltern wird nicht richtig initialisiert, wegen Xml-Annotations
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
    private void initializeTables() throws IllegalXmlFileException {
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
            String stateInitial = t.stateInitial;
            String inputSymbol = t.inputSymbol;
            String stateFinal = t.stateFinal;
            String outputSymbol = t.outputSymbol;

            // Iterating through first column and searching for fitting state
            for(i = 1; i < transitionTable.length; i++){
                if(stateInitial.equals(transitionTable[i][0])){
                    // Safe position of found state
                    statePosition = i;
                    break;
                }
            }

            // Iterating through first row and searching for fitting symbol
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
                // Illegal Xml-File. Symbol or state was used in transition-tag, that was not defined before
                // (input-symbols, states)-tag !
                if(inputSymbolPosition == 0){
                    // Illegal symbol
                    throw new IllegalXmlFileException("Symbol " + inputSymbol + " is not defined in the input alphabet." +
                        " Please correct.");
                }
                if(statePosition == 0){
                    // Illegal state
                    throw new IllegalXmlFileException("State " + stateInitial + " is not defined in tag <states>. " +
                            "Please correct.");
                }
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
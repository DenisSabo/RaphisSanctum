import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;


public abstract class Mealy implements CONS{
    // name of the mealy machine
    String mealyName = new String();

    // current state of the mealy machine
    State currentState;

    // set of all possible states of the mealy machine
    HashSet<State> states = new HashSet<>();

    // input alphabet
    HashSet<Symbol> inputSymbols = new HashSet<>();

    // output alphabet
    HashSet<Symbol> outputSymbols = new HashSet<>();

    // transition table
    String transitionTable[][] = {};

    // table for outputs
    String outputTable[][] = {};

    // "activates" mealy
    public void runMealy() throws IOException, InvalidValue {

        // Shows user the current mealy "build"
        printMealy();
        System.out.println("Now type in a legal input symbol!");

        // "Second" reactive system, so user can interact with his mealy
        reactiveMealy();
    }

    // Some printing functions to let the user know what's happening
    private void printAllStates(){
        System.out.println("Exisiting states: ");
        for(State state : states){
            System.out.print("'");
            System.out.print(state.getState());
            System.out.print("' ");
        }
        System.out.println("");
    }
    private void printAllInputs(){
        System.out.println("Possible inputs: ");
        for(Symbol symbol : inputSymbols){
            System.out.print("'");
            System.out.print(symbol.getSymbol());
            System.out.print("' ");
        }
        System.out.println("");
    }
    private void printAllOutputs(){
        System.out.println("Possible outputs: ");
        for(Symbol symbol : outputSymbols){
            System.out.print("'");
            System.out.print(symbol.getSymbol());
            System.out.print("' ");
        }
        System.out.println("");
    }

    private void printTransitionTable(){
        System.out.println("The transition table looks like this:");
        final PrettyPrinter printer = new PrettyPrinter(System.out);
        printer.print(transitionTable);
    }

    private void printOutputTable(){
        System.out.println("The output table looks like this:");
        final PrettyPrinter printer = new PrettyPrinter(System.out);
        printer.print(outputTable);
    }
    private void printMealy(){
        System.out.println("Running mealy: " + mealyName);
        System.out.println("Current State: " + currentState.getState());
        printAllStates();
        printAllInputs();
        printAllOutputs();
        printTransitionTable();
        printOutputTable();
    }

    public State transNext(State currentState, Symbol inputSymbol) throws InvalidValue {
        // Safes indexes of found elements
        int statePosition = 0;
        int inputSymbolPosition = 0;

        // Iterating through first column and searching for fitting state
        for(int i = 1; i < transitionTable.length; i++){
            //TODO mehehehe
            if(currentState.getState().equals((Object)transitionTable[i][0])){
                // Safe position of found state
                statePosition = i;
                break;
            }
        }

        // Iterating through first row and searching for fitting symbol
        for(int j = 1; j < transitionTable[0].length; j++){
            if(inputSymbol.getSymbol().equals(transitionTable[0][j])){
                // Safe position of found symbol
                inputSymbolPosition = j;
                break;
            }
        }
        if(statePosition == 0 || inputSymbolPosition == 0){
            System.out.println("Please use symbols in the alphabet.");
            printAllInputs();
            throw new InvalidValue();
        }
        else{
            // TODO bad solution
            return new State(transitionTable[statePosition][inputSymbolPosition]);
        }
    }

    // TODO almost the same as transNext ...
    public Symbol output(State currentState, Symbol inputSymbol) throws InvalidValue{
        // Safes indexes of found elements
        int statePosition = 0;
        int inputSymbolPosition = 0;

        // Iterating through first column and searching for fitting state
        for(int i = 1; i < outputTable.length; i++){
            if(currentState.getState().equals((Object)outputTable[i][0])){
                // Safe position of found state
                statePosition = i;
                break;
            }
        }

        // Iterating through first row and searching for fitting symbol
        for(int j = 1; j < outputTable[0].length; j++){
            if(inputSymbol.getSymbol().equals(outputTable[0][j])){
                // Safe position of found symbol
                inputSymbolPosition = j;
                break;
            }
        }
        if(statePosition == 0 || inputSymbolPosition == 0){
            System.out.println("Please use symbols that exist in the alphabet.");
            printAllInputs();
            throw new InvalidValue();
        }
        else{
            // TODO bad solution
            return new Symbol(outputTable[statePosition][inputSymbolPosition]);
        }
    }


    public void reactiveMealy() throws IOException, InvalidValue {
        StringBuffer userInput = new StringBuffer();
        InputStream is = System.in;
        int c = 0;

        while(true){
            // While not "end of file"
            while((c = is.read()) > EOF){
                if (c == CONS.ENTER) break;
                // Summarize chars in one buffer
                userInput.append((char) c);
            }
            // Convert buffer to string
            String input = userInput.toString();
            Symbol userSymbol = new Symbol(input);

            currentState = transNext(currentState, userSymbol);
            System.out.println("Mealy entered new State: " + currentState.getState().toString());
            // TODO Vielleicht auf Endzustand/Fehlerzustand testen !
            System.out.println("Waiting for new input");
            reactiveMealy();
        }
    }
}

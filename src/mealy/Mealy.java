package mealy;

import mealy.helpingClasses.CONS;
import mealy.helpingClasses.PrettyPrinter;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Mealy implements CONS, Runnable{
    // name of the mealy machine
    protected String mealyName = new String();

    // current state of the mealy machine
    protected State currentState;

    // set of all possible states of the mealy machine
    protected HashSet<State> states = new HashSet<>();

    // input alphabet
    protected HashSet<Symbol> inputSymbols = new HashSet<>();

    // output alphabet
    protected HashSet<Symbol> outputSymbols = new HashSet<>();

    // transition table
    protected String transitionTable[][] = {};

    // table for outputs
    protected String outputTable[][] = {};

    // BlockingQueues where interaction with other threads will take place
    BlockingQueue<Symbol> inputs = new LinkedBlockingQueue<>(10);
    BlockingQueue<Symbol> outputs = new LinkedBlockingQueue<>(10);

    // Getter for blocking-queues are needed, because other java classes need reference, so they can be able to write
    // or read queues
    public BlockingQueue<Symbol> getInputs() {
        return inputs;
    }
    public BlockingQueue<Symbol> getOutputs() {
        return outputs;
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
        System.out.println("Now create .msg file with legal input symbol in the input directory!");
    }

    public State transNext(State currentState, Symbol inputSymbol) throws InvalidValue {
        // Safes indexes of found elements
        int statePosition = 0;
        int inputSymbolPosition = 0;

        // Iterating through first column and searching for fitting state
        for(int i = 1; i < transitionTable.length; i++){
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
            State nextState = new State(transitionTable[statePosition][inputSymbolPosition]);
            System.out.println("Next state: " + nextState.getState().toString());
            return nextState;
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

    @Override
    public void run() {
        // Mealy will print all necessary information for client
        printMealy();
        // Watch for new inputs in blocking queue, change state if new symbol and write new output in queue
        while(true){
            try {
                // Try to take input symbol out of blocking-queue inputs
                Symbol input = inputs.take();
                // If inputs is empty, blocking queue will wait for new input in queue
                // so following code will only be executed if there is a input in variable input
                Symbol output = output(currentState, input); // Get output of transition
                transNext(currentState, input); // Go into next state
                // Write output-symbol in queue
                outputs.put(output);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (InvalidValue invalidValue) {
                invalidValue.printStackTrace();
            }
        }
    }
}

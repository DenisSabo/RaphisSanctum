package mealy;

import mealy.helpingClasses.CONS;
import mealy.helpingClasses.PrettyPrinter;
import org.apache.commons.io.FileUtils;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Mealy implements CONS, Runnable{
    // name of the mealy machine
    private String mealyName;

    // current state of the mealy machine
    private State currentState;

    // set of all possible states of the mealy machine
    private HashSet<State> states = new HashSet<>();

    // input alphabet
    private HashSet<Symbol> inputSymbols = new HashSet<>();

    // output alphabet
    private HashSet<Symbol> outputSymbols = new HashSet<>();

    // transition table
    private String transitionTable[][] = {};

    // table for outputs
    private String outputTable[][] = {};

    // BlockingQueues where interaction with other threads will take place
    BlockingQueue<Symbol> inputs = new LinkedBlockingQueue<>(10);
    BlockingQueue<Symbol> outputs = new LinkedBlockingQueue<>(10);

    // Class InputHandler handles input by watching directory "input" and writing new symbols into inputs
    InputHandler handleInput = new InputHandler(inputs);
    // Class OutputHandler handles output by reading from outputs directory (that is filled by mealy) and
    // writing new outputs into directory "output"
    OutputHandler handleOutput = new OutputHandler(outputs);
    // InputHandler and OutputHandler will run in own threads
    Thread forInputs = new Thread(handleInput);
    Thread forOutputs = new Thread(handleOutput);

    // Getter for blocking-queues are needed, because other java classes need reference, so they can be able to write
    // or read queues
    public BlockingQueue<Symbol> getInputs() {
        return inputs;
    }
    public BlockingQueue<Symbol> getOutputs() {
        return outputs;
    }

    public Mealy() throws IOException {
        // default constructor
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
        System.out.println("Create 'end.msg'-file in input-directory, to stop all threads properly.");
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

    public void transNext(Symbol inputSymbol) throws InvalidValue {
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
            currentState = nextState; // Changes currentState
            System.out.println("Next state: " + nextState.getState().toString());
        }
    }

    // TODO almost the same as transNext ...
    public Symbol output(Symbol inputSymbol) throws InvalidValue{
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
        // First step: Start threads that handle input and output symbols
        forInputs.start(); // Actually a InputHandler
        forOutputs.start(); // Actually a OutputHandler

        // Mealy will print all necessary information for client
        printMealy();
        // Watch for new inputs in blocking queue, change state if new symbol and write new output in queue
        while(true){
            try {
                // Try to take input symbol out of blocking-queue inputs, which is written by InputHandler
                Symbol input = inputs.take();

                // Symbol "end" is like a command to stop the mealy with its threads
                if(input.getSymbol().equals("end")){
                    outputs.put(input); // Informs thread that he has to stop
                    closeMealy();
                    return; // Stops this thread
                }

                Symbol output = output(input); // Get output of transition
                // Write output-symbol in queue
                outputs.put(output);

                transNext(input); // Go into next state


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (InvalidValue invalidValue) {
                invalidValue.printStackTrace();
            }
        }
    }

    public void setMealyName(String mealyName) {
        this.mealyName = mealyName;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public void setOutputTable(String[][] outputTable) {
        this.outputTable = outputTable;
    }

    public void setTransitionTable(String[][] transitionTable) {
        this.transitionTable = transitionTable;
    }

    public void setStates(HashSet<State> states) {
        this.states = states;
    }

    public void setInputSymbols(HashSet<Symbol> inputSymbols) {
        this.inputSymbols = inputSymbols;
    }

    public void setOutputSymbols(HashSet<Symbol> outputSymbols) {
        this.outputSymbols = outputSymbols;
    }

    private void deleteOutputs() {
        try {

            FileUtils.cleanDirectory(new File("./output/"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeMealy(){
        if(forInputs.isAlive()) forInputs.interrupt(); // Check if thread is still alive and close if so
        if(forOutputs.isAlive()) forOutputs.interrupt(); // -"-
        deleteOutputs(); // Cleans Output-directory
        System.out.println("");
        System.out.println("''''''''''''''''''''''''''Closed Mealy successfully''''''''''''''''''''''''''");
        System.out.println("");
    }
}

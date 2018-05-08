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

/*
* Author: Denis Sabolotni
* Description: Mealy: Handles transition into new states, and produces Output Symbols, accordingly to current new state.
*               Has printing-functions to present all needed information for user.
*               Role: Producer and Consumer
*               Producer: produces Output symbols and writes them into BlockingQueue for output symbols
*               Consumer: Waits for input symbols in BlockingQueue for input symbols
*               3 threads: Mealy itself, InputHandler and OutputHandler. Communication over BlockingQueues
* */
public class
Mealy implements CONS, Runnable{
    // name of the mealy machine
    private String mealyName;

    // current state of the mealy machine
    private State currentState;

    private State finalState;

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

    // function searches for right value by finding given input symbol in first row
    // and by finding current state in first column
    private String iterateTable(Symbol input, String table[][]) throws InvalidValue {
        // Variables safe column and row position, so right table cell(value) can be returned
        int statePosition = 0;
        int inputSymbolPosition = 0;

        // Iterating through first column and searching for fitting state
        for(int i = 1; i < table.length; i++){
            if(currentState.getState().equals((Object)table[i][0])){
                // Safe position of found state
                statePosition = i;
                break;
            }
        }

        // Iterating through first row and searching for fitting symbol
        for(int j = 1; j < table[0].length; j++){
            if(input.getSymbol().equals(table[0][j])){
                // Safe position of found symbol
                inputSymbolPosition = j;
                break;
            }
        }
        // If inputSymbolPosition has value 0, Symbol was not found in table -> User used wrong input-symbol
        if(inputSymbolPosition == 0){
            System.err.println("Please use symbols that exist in the alphabet.");
            printAllInputs();
            throw new InvalidValue();
        }
        else if(statePosition == 0){
            // Only happens if current state was not found in table -> happens if there is a mistake in xml file
            System.err.println("Probably something wrong with your Xml-File. Could not find current state");
            System.err.println(currentState.getState());
            throw new InvalidValue();
        }
        else{
            return table[statePosition][inputSymbolPosition];
        }
    }

    public void transNext(Symbol inputSymbol) throws InvalidValue {
        State nextState = new State(iterateTable(inputSymbol, transitionTable));
        currentState = nextState; // Changes currentState
        System.out.println("Next state: " + nextState.getState().toString());
    }

    public Symbol output(Symbol inputSymbol) throws InvalidValue{
        String output = iterateTable(inputSymbol, outputTable);
        return new Symbol(output);
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

                    return; // Stops this thread
                }

                Symbol output = output(input); // Get output of transition
                // Write output-symbol in queue
                outputs.put(output);

                transNext(input); // Go into next state
                if(currentState.getState().equals(finalState.getState())){
                    // Checks if Mealy is in state, that indicates end of mealy
                    // if so -> Close All threads, and mealy itself (Zentrale Stelle, die alle Threads schließt)
                    closeMealy();
                    return;
                }


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

    public void setFinalState(State finalState) {
        this.finalState = finalState;
    }

    private void deleteOutputs() {
        try {
            // Clean output-directory
            FileUtils.cleanDirectory(new File("./output/"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeMealy(){
        // Closes complete Mealy (All 3 threads)
        if(forInputs.isAlive()) forInputs.interrupt();
        if(forOutputs.isAlive()) forOutputs.interrupt();
        deleteOutputs(); // Cleans Output-directory
        System.out.println("");
        System.out.println("''''''''''''''''''''''''''Closed Mealy successfully''''''''''''''''''''''''''");
        System.out.println("");
    }
}

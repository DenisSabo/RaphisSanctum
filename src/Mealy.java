import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.HashSet;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;


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
        System.out.println("Now create .msg file with legal input symbol in the input directory!");

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


    public void reactiveMealy() throws IOException, InvalidValue {
        // Reactive system for mealy, because mealy must be able to react to users input
        StringBuffer userInput = new StringBuffer();
        InputStream is = System.in;
        // A watch-service that will watch the directory "input" for changes
        WatchService watcher = FileSystems.getDefault().newWatchService();
        Path dir = FileSystems.getDefault().getPath("./input");
        try{// wait for key to be signaled
            WatchKey key = dir.register(watcher, ENTRY_CREATE);
            // event processing loop
            while(true){
                try{

                    key = watcher.take();
                }
                catch(InterruptedException ex){
                    System.err.println(ex);
                    return;
                }
                for (WatchEvent<?> event: key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    // This key is registered only
                    // for ENTRY_CREATE events,
                    // but an OVERFLOW event can
                    // occur regardless if events
                    // are lost or discarded.
                    if (kind == OVERFLOW) {
                        continue;
                    }

                    // The filename is the in context of the event.
                    WatchEvent<Path> ev = (WatchEvent<Path>)event;
                    Path filename = ev.context();

                    // Use filename as symbol
                    // but first check if filending is "msg"
                    String filesName = filename.getFileName().toString();
                    if(CONS.checkFileEnding(filesName, "msg")){
                        Symbol fileSymbol = new Symbol(filesName.split("\\.")[0]);
                        // try to change to next state
                        try {
                            currentState = transNext(currentState, fileSymbol);
                            // deletes file
                            Files.delete(FileSystems.getDefault().getPath("./input/"+filesName));
                            // rerun function
                            reactiveMealy();
                        }
                        catch(InvalidValue ex){
                            System.err.println(ex);
                        }
                    }
                    else{
                        System.err.println("Invalid file ending. Only '.msg' files allowed.");
                        Files.delete(FileSystems.getDefault().getPath("./input/"+filesName));
                        reactiveMealy();
                    }
                }

            }
        }
        catch(IOException ex){
            // TODO maybe print all errors into err stream
            System.err.println(ex);
            return;
        }
    }
}

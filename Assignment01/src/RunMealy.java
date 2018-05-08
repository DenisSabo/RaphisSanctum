import mealy.XmlMealy;
import mealy.helpingClasses.CONS;
import myExceptions.IllegalXmlFileException;

import java.io.IOException;

public class RunMealy implements CONS {
    // Mealy machine
    // INPUT: C:\path\to\bsp.xml
    // Todo write own class to handle errors
    public static void main(String[] args){
        while(true){
            runMealy();
        }
    }

    // TODO Sicherheitsfassade
    private static void runMealy(){
        // tell user what to do
        System.out.println("Please type in the path to the xml file, representing a Mealy.");
        System.out.println("You can just press enter, and a default.xml file will be loaded!");

        // "Reactive" function that listens to user's input
        String userInput = CONS.listenToUserInput();

        // Instantiate new XmlMealy
        XmlMealy xm = null;
        try {
            xm = new XmlMealy();
        } catch (IOException e) {
            e.printStackTrace(); // Todo throws error in main
        }

        // Create Mealy out of xml-file
        try {
            xm = xm.createMealy(userInput);
        } catch (IllegalXmlFileException e) { // Todo throws error in main
            e.printStackTrace();
        }

        Thread mealy = new Thread(xm);

        // Starts mealy in own thread
        mealy.start();

        try {
            // Main has to wait, till mealy-thread stops
            mealy.join();
            // If mealy thread has stopped, program will be restarted because of while(true){...}
            System.out.println("Restart program");

        } catch (InterruptedException e) { // Todo throws error in main
            e.printStackTrace();
        }
    }
}

package mealy;

import mealy.helpingClasses.CONS;
import myExceptions.IllegalXmlFileException;
import xml.XmlMealy;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RunMealy implements CONS {

    // Blocking queues for input and output
    public static final BlockingQueue<Symbol> inputs = new LinkedBlockingQueue<Symbol>(10);
    public static final BlockingQueue<Symbol> outputs = new LinkedBlockingQueue<Symbol>(10);

    // Mealy machine
    // INPUT: C:\path\to\bsp.xml
    public static void main(String[] args) throws IOException, IllegalXmlFileException {
        // tell user what to do
        System.out.println("Please type in the path to the xml file, representing a Mealy.");

        // "Reactive" function listenToUserInput
        String userInput = CONS.listenToUserInput();

        // Now check if file has ".xml"-ending
        if(CONS.checkFileEnding(userInput, "xml")){
            // Instantiate new XmlMealy
            XmlMealy xm = new XmlMealy();

            // Try to create a Mealy out of Xml-File
            try{
                xm = xm.createMealy(userInput);
            }
            catch(IllegalXmlFileException ex){
                // If xml-file was invalid, go back into main function (maybe user will give legal file now)
                main(args);
            }
            // Get queues of generated mealy and pass them to handler
            /* InputHandler watches input-directory and writes new input symbols into
                referenced (input) queue, that will be read by mealy */
            InputHandler inputHandler = new InputHandler(xm.getInputs());
            /* OutputHandler watches/reads referenced (output) queue (filled my mealy)
                and writes "output-"files into output directory */
            OutputHandler outputHandler = new OutputHandler(xm.getOutputs());

            // Run three different threads, that will communicate through queues of mealy
            new Thread(inputHandler).start();
            new Thread(outputHandler).start();
            new Thread(xm).start();
        }
        else{
            System.out.println("File has to be a Xml-File. Please try again.");
            main(args);
        }
    }
}

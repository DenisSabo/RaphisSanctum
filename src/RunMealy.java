import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RunMealy implements CONS{

    // Blocking queues for input and output
    public static final BlockingQueue<Symbol> inputs = new LinkedBlockingQueue<Symbol>(10);
    public static final BlockingQueue<Symbol> outputs = new LinkedBlockingQueue<Symbol>(10);

    // Mealy machine
    // INPUT: C:\path\to\bsp.xml
    public static void main(String[] args) throws IOException {
        System.out.println("Please type in the path to the xml file, representing a Mealy.");
        // Reaktives System
        StringBuffer buffer = new StringBuffer();
        while(true){
            // Listening to users input
            InputStream is = System.in;
            try {
                int c = 0;
                while ((c = is.read()) > CONS.EOF) {
                    // User input will be added to the buffer
                    if (c == CONS.ENTER) break;
                    buffer.append((char) c);
                }
                // convert buffer to string
                String path = buffer.toString();

                // if path references xml file, try to create "XML-Mealy"
                if(CONS.checkFileEnding(path, "xml")){
                    XMLMealy xm = new XMLMealy();
                    xm = xm.createMealy(path);
                    if(xm == null){
                        // if function returns null, return to main
                        // TODO better solution: throwing errors
                        main(args);
                    }
                    // Get queues of generated mealy and pass them to handler
                    InputHandler inputHandler = new InputHandler(xm.getInputs());
                    OutputHandler outputHandler = new OutputHandler(xm.getOutputs());

                    // Run three different threads
                    new Thread(inputHandler).start();
                    new Thread(outputHandler).start();
                    new Thread(xm).start();
                    // TODO how or when to terminate threads?
                }
                else{
                    System.out.println("Wrong file ending. Use Xml only!");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally{
                try{
                    // TODO is = system.in ... Do I have to close this stream?
                    is.close();
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}

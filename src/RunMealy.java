import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class RunMealy implements CONS{
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
                if(checkFileEnding(path, "xml")){
                    XMLMealy xm = new XMLMealy();
                    xm = xm.createMealy(path); // TODO add try catch block
                    if(xm == null){
                        // if function returns null, return to main
                        // TODO better solution: throwing errors
                        main(args);
                    }
                    xm.runMealy();
                }
                else if(checkFileEnding(path, "json")){
                    // TODO or not?
                }
                else{
                    System.out.println("Wrong file ending. Use Xml only!");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (InvalidValue invalidValue) {
                invalidValue.printStackTrace();
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

    public static boolean checkFileEnding(String path, String expectedEnding){
        String extension = "";

        int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i+1);
        }
        if(expectedEnding.equals(extension)) return true;
        else return false;
    }
}

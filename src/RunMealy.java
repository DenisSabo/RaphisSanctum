import org.omg.DynamicAny.DynAnyPackage.InvalidValue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Pattern;

public class RunMealy {
    // Mealy machine
    // INPUT: C:\Users\denis\Desktop\bsp.xml
    public static void main(String[] args) throws IOException {
        // Reaktives System
        StringBuffer buffer = new StringBuffer();
        while(true){
            // User soll Pfad der XML- oder JSON-Datei per Konsole eingeben
            InputStream is = System.in;
            try {
                int c = 0;
                // TODO add EOF as a constant
                while ((c = is.read()) > -1) { // -1 bedeutet: EOF
                    // User input will be put together
                    if (c == '\n') break; // TODO maybe constant \n = ENTER
                    buffer.append((char) c);
                }
                // convert buffer to string
                String path = buffer.toString();

                // if path references xml file, create "XML-Mealy"
                if(checkFileEnding(path, "xml")){
                    XMLMealy xm = new XMLMealy();
                    xm = xm.createMealy(path);
                    if(xm == null){
                        // if function returns null, return to main
                        main(args);
                    }
                    xm.runMealy();
                }
                else if(checkFileEnding(path, "json")){
                    // TODO
                }
                else{
                    System.out.println("Wrong file ending. Use XML or JSON only!");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (InvalidValue invalidValue) {
                invalidValue.printStackTrace();
            } finally{
                try{
                    // TODO check if everything is closed ....
                    is.close();
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }


            // Verarbeitung der Eingaben (Zustandsübergang)

            // Ausgaben des reaktiven Systems (Neuer Zustand)
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

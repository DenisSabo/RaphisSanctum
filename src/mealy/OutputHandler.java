package mealy;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class OutputHandler implements Runnable{
    // Reads BlockingQueue, which safes output symbols of mealy
    BlockingQueue<Symbol> ouputSymbols;

    public OutputHandler(BlockingQueue<Symbol> outputs){
        this.ouputSymbols = outputs;
    }

    @Override
    public void run() {
        while(true){
            try {
                // Takes a output symbol out of queue, if one is inserted into queue
                Symbol output = ouputSymbols.take();
                if(output.getSymbol().equals("end")){
                    return; // Stops this thread
                }
                // Creates .msg file in output directory
                String path = "./output/" + output.getSymbol() + ".msg";
                File f = new File(path);
                f.createNewFile();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

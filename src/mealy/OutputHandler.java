package mealy;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class OutputHandler implements Runnable{
    // Reads BlockingQueue, which safes output symbols of mealy
    private BlockingQueue<Symbol> ouputSymbols;

    public OutputHandler(BlockingQueue<Symbol> outputs){
        this.ouputSymbols = outputs;
    }

    @Override
    public void run() {
        while(true){
            try {
                // Takes a output symbol out of queue, if one is inserted into queue
                Symbol output = ouputSymbols.take();


                // Creates .msg file in output directory
                String path = "./output/" + output.getSymbol() + ".msg";
                File f = new File(path);
                f.createNewFile();

            } catch (InterruptedException e) {
                // Do not print in err, because this thread will be closed intentionally from outside
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

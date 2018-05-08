package mealy;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/*
* Author: Denis Sabolotni
* Description: OutputHandler waits for OutputSymbols in referenced BlockingQueue
*               Role: Consumer
*               Instance of class Mealy is producer of output symbols
* */

public class OutputHandler implements Runnable{
    //Counter which counts how much output symbols were written in directory already
    private int counter = 0;

    // Reads BlockingQueue, which safes output symbols of mealy
    private BlockingQueue<Symbol> ouputSymbols;

    // Constructor
    public OutputHandler(BlockingQueue<Symbol> outputs){
        this.ouputSymbols = outputs;
    }

    @Override
    public void run() {
        while(true){
            try {
                // Takes output symbol in BlockingQueue
                Symbol output = ouputSymbols.take();

                // Creates .msg file in output directory
                // For example: "./output/00_0.msg (OutputSymbol_counterValue.msg)
                // Output symbols that will be written in ouput-directory will use counter in name,
                // to avoid name conflicts
                String path = "./output/" + output.getSymbol() + "_" + counter +  ".msg";
                counter++;

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

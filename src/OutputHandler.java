import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class OutputHandler implements Runnable{
    // Reads BlockingQueue, which safes output symbols of mealy
    BlockingQueue<Symbol> ouputSymbols = new LinkedBlockingQueue<>();

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
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

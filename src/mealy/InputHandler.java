package mealy;

import mealy.helpingClasses.CONS;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.BlockingQueue;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/*
* Author: Denis Sabolotni
* Description: InputHandler watches the input-directory and writes the .msg inputs into given blocking-queue
* */
public class InputHandler implements Runnable {
    // Reference to a blocking queue in which this class can write new input symbols by watching input directory
    BlockingQueue<Symbol> inputSymbols;

    // WatcherService that will watch directory input/
    WatchService watcher = FileSystems.getDefault().newWatchService();
    Path dir = Paths.get("./input/");

    WatchKey key = dir.register(watcher, ENTRY_CREATE);

    public InputHandler(BlockingQueue<Symbol> inputSymbolsQueue) throws IOException {
        this.inputSymbols = inputSymbolsQueue;
    }

    @Override
    public void run() {
        // wait for key to be signaled
        WatchKey key;
        while (true) {
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                // This key is registered only
                // for ENTRY_CREATE events,
                // but an OVERFLOW event can
                // occur regardless if events
                // are lost or discarded.
                if (kind == OVERFLOW) {
                    continue;
                }

                // The filename is the
                // context of the event.
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path filename = ev.context();
                String filesName = filename.getFileName().toString();
                if (CONS.checkFileEnding(filesName, "msg")) {
                    Symbol inputSymbol = new Symbol(CONS.getFileName(filesName));
                    // Try to put new input symbol in blocking queue
                    try {
                        inputSymbols.put(inputSymbol);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Try to delete file
                    try {
                        Path pathToDelete = Paths.get("./input/" + filesName);
                        Files.delete(pathToDelete);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

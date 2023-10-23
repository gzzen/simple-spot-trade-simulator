package persistence;

import model.logger.Event;
import model.logger.EventLog;
import model.Portfolio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

// Writer that save the information of portfolio to the path as JSON file
public class JsonWriter {
    private static final int TAB = 4;
    private PrintWriter writer;
    private String path;
    private EventLog log = EventLog.getInstance();


    // constructor that assigns the input path to the path for writer
    public JsonWriter(String path) {
        this.path = path;
    }

    // reference: JsonSerializationDemo
    // MODIFIES: this
    // EFFECTS: open the writer at the given path.
    // throw FileNotFoundException if such path does not exist
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(path));
    }

    // reference: JsonSerializationDemo
    // MODIFIES: this
    // EFFECTS: save the information on portfolio to file as JSON
    public void write(Portfolio portfolio) {
        writer.print(portfolio.toJson().toString(TAB));
        log.logEvent(new Event("IO: Portfolio saved"));
    }

    // reference: JsonSerializationDemo
    // MODIFIES: this
    // EFFECTS: close the file writer
    public void close() {
        writer.close();
    }

}

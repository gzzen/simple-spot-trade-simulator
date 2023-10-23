package ui;

import model.logger.Event;
import model.logger.EventLog;

// printer that print to the console all the events that have been logged since the application started
public class OnExitPrinter {

    private static EventLog log = EventLog.getInstance();

    public static void print() {
        for (Event e : log) {
            System.out.println(e.toString());
        }
    }
}

package model;

import model.logger.Event;
import model.logger.EventLog;

// Exception for excess maximum amount of available securities or cash
public class ExcessQuantityException extends Exception {

    EventLog log = EventLog.getInstance();

    public ExcessQuantityException() {
        super();
        log.logEvent(new Event("Action failed: Excess quantity"));
    }
}

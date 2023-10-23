package model;

import model.logger.Event;
import model.logger.EventLog;

// An exception that indicates the user does not have enough
// cash on portfolio
public class InsufficientFundException extends Exception {

    EventLog log = EventLog.getInstance();

    public InsufficientFundException() {
        super();
        log.logEvent(new Event("Action failed: Insiffucient funds"));

    }
}

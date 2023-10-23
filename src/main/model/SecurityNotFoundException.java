package model;

import model.logger.Event;
import model.logger.EventLog;

// Exception for not finding the security for a given code
public class SecurityNotFoundException extends Exception {

    EventLog log = EventLog.getInstance();

    public SecurityNotFoundException() {
        super();
        log.logEvent(new Event("Action failed: Security not found"));
    }
}

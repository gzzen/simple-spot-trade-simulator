package persistence;

import org.json.JSONObject;

// convert an object to JSON format by implementing this interface
public interface Writable {

    // EFFECTS: return the object as JSON format
    JSONObject toJson();
}

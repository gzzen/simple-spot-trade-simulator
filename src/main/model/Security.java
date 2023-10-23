package model;

import org.json.JSONObject;
import persistence.Writable;

// Security represents a security in market that contains a Holding object for
// holding information and a code for reference.
public class Security implements Writable {

    private Holding holding;
    private String code;

    // MODIFIES: this
    // EFFECTS: constructor method that initializes class-level variables
    public Security(String code) {
        this.code = code;
        holding = new Holding();
    }

    // REQUIRES: price, quantity > 0
    // MODIFIES: this
    // EFFECTS: record the price and quantity for one purchase in holding
    public void buy(int price, int quantity) {
        holding.record(price, quantity);
    }

    // REQUIRES: quantity > 0
    // MODIFIES: this
    // EFFECTS: remove quantity for one selling in holding,
    // and return the original costs
    public int sell(int quantity) throws ExcessQuantityException {
        return holding.remove(quantity);
    }

    // getter
    public String getCode() {
        return code;
    }

    // EFFECTS: return the code and holding information of the security
    // as a string
    public String toString() {
        return "Code: " + code + ", Holding: "
                + (holding.isEmpty() ? "none" : holding.toString());
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonSecurity = new JSONObject();
        jsonSecurity.put("code", code);
        jsonSecurity.put("holding", holding.toJson());
        return jsonSecurity;
    }

    // getter
    public Holding getHolding() {
        return holding;
    }
}

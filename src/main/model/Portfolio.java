package model;

import model.logger.Event;
import model.logger.EventLog;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;

// Portfolio contains a list of securities and cash, representing
// users' simulated trading account.
public class Portfolio implements Writable {

    private ArrayList<Security> listOfSecurity;
    private int cash;
    private EventLog log;

    // REQUIRES: initialCash >= 0
    // MODIFIES: this
    // EFFECTS: constructor method that initialize class-level variables
    public Portfolio(int initialCash) {
        cash = initialCash;
        listOfSecurity = new ArrayList<>();
        log = EventLog.getInstance();
    }

    // getter
    public int getCash() {
        return cash;
    }

    // MODIFIES: this
    // EFFECTS: reset the amount of cash to 0
    public void resetCash() {
        cash = 0;
        log.logEvent(new Event("cash reset to 0"));
    }

    // MODIFIES: this
    // EFFECTS: add a certain valid amount of cash to the current cash
    public void addCash(int amount) {
        cash += amount;
        log.logEvent(new Event(amount + " cash added to portfolio"));
    }

    // REQUIRES: a security exists in listOfSecurity for the given code
    // EFFECTS: return a security in listOfSecurity for a given code;
    // throw SecurityNotFoundException if there is no such security
    private Security getSecurity(String code) throws SecurityNotFoundException {
        for (Security security: listOfSecurity) {
            if (security.getCode().equals(code)) {
                return security;
            }
        }
        throw new SecurityNotFoundException();
    }

    // REQUIRES: price, quantity > 0
    // MODIFIES: this
    // EFFECTS: buy a security by code at given price and quantity
    public void buy(String code, int price, int quantity) throws InsufficientFundException {
        Security security;
        int cost = price * quantity;
        if (checkSufficientCash(cash, cost)) {
            try {
                security = getSecurity(code);
            } catch (SecurityNotFoundException e) {
                security = new Security(code);
                listOfSecurity.add(security);
            }
            security.buy(price, quantity);
            cash -= cost;
            log.logEvent(new Event(code + ": " + price + " price, " + quantity + " quantity bought"));
        } else {
            throw new InsufficientFundException();
        }
    }

    // REQUIRES: cash, cost > 0
    // EFFECTS: check if there is sufficient cash for a cost
    private boolean checkSufficientCash(int cash, int cost) {
        return cash >= cost;
    }

    // REQUIRES: price, quantity > 0
    // MODIFIES: this
    // EFFECTS: sell a security by code at a given price and quantity,
    // and return the return rate for this transaction
    public double sell(String code, int price, int quantity)
            throws SecurityNotFoundException, ExcessQuantityException {
        Security security;
        double returnRate;
        security = getSecurity(code);
        cash += price * quantity;
        returnRate = (double) (price * quantity) / (double) (security.sell(quantity)) - 1;
        log.logEvent(new Event(code + ": " + price + " price, " + quantity + " quantity sold"));
        return returnRate;
    }

    // EFFECTS: return a string with information of the holding
    public String toString() {
        String info = "";
        for (Security security : listOfSecurity) {
            info += security.toString() + "\n";
        }
        return info;
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonPortfolio = new JSONObject();
        JSONArray jsonSecurities = new JSONArray();
        for (Security security : listOfSecurity) {
            jsonSecurities.put(security.toJson());
        }
        jsonPortfolio.put("cash", cash);
        jsonPortfolio.put("securities", jsonSecurities);
        return jsonPortfolio;
    }

    // getter
    public ArrayList<Security> getListOfSecurity() {
        return listOfSecurity;
    }

    // EFFECTS: return a security from a list of securities given the code
    // return null if not found
    public Security getSecurityFromList(String code) {
        for (Security security : listOfSecurity) {
            if (security.getCode().equals(code)) {
                return security;
            }
        }
        return null;
    }
}

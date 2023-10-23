package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;

// Holding represents a list-like object of price-quantity pair by a list of
// price and a list of quantity. The price and quantity for the same transaction
// have same index in two lists.
public class Holding implements Writable {

    private ArrayList<Integer> listOfPrice;
    private ArrayList<Integer> listOfQuantity;

    // MODIFIES: this
    // EFFECTS: constructor method that initialize class-level variables
    public Holding() {
        listOfPrice = new ArrayList<>();
        listOfQuantity = new ArrayList<>();
    }

    // REQUIRES:
    // listOfPrice.size() == listOfQuantity.size()
    // for all i: listOfQuantity at i is the quantity for listOfPrice at i
    // price, quantity > 0
    // MODIFIES: this
    // EFFECTS: record a new transaction by its price and quantity
    public void record(int price, int quantity) {
        if (listOfPrice.contains(price)) {
            int index = listOfPrice.indexOf(price);
            int initialQuantity = listOfQuantity.get(index);
            listOfQuantity.set(index, initialQuantity + quantity);
        } else {
            listOfPrice.add(price);
            listOfQuantity.add(quantity);
        }
    }

    // REQUIRES:
    // listOfPrice and listOfQuantity are non-empty
    // amount of stocks in hold > quality
    // listOfPrice.size() == listOfQuantity.size()
    // for all i: listOfQuantity at i is the quantity for listOfPrice at i
    // quantity > 0
    // MODIFIES: this
    // EFFECTS: remove the given quantity of security from holdings
    // and returns the original costs
    // or throw ExcessQuantityException if there is not enough to remove
    public int remove(int quantity) throws ExcessQuantityException {
        ArrayList<Integer> removedListOfPrice = new ArrayList<>();
        ArrayList<Integer> removedListOfQuantity = new ArrayList<>();
        ArrayList<Integer> bufferListOfPrice = (ArrayList<Integer>) listOfPrice.clone();
        ArrayList<Integer> bufferListOfQuantity = (ArrayList<Integer>) listOfQuantity.clone();
        removeOperation(quantity,
                removedListOfPrice,
                removedListOfQuantity,
                bufferListOfPrice,
                bufferListOfQuantity);
        listOfPrice = bufferListOfPrice;
        listOfQuantity = bufferListOfQuantity;
        return cost(removedListOfPrice, removedListOfQuantity);
    }

    // REQUIRES:
    // bufferListOfPrice and bufferListOfQuantity are copies of listOfPrice and listOfQuantity
    // with all properties from the two lists
    // EFFECTS: helper method to remove() that attempts the remove operation on buffer lists
    // or throw ExcessQuantityException if there is not enough to remove
    private void removeOperation(int quantity,
                                 ArrayList<Integer> removedListOfPrice,
                                 ArrayList<Integer> removedListOfQuantity,
                                 ArrayList<Integer> bufferListOfPrice,
                                 ArrayList<Integer> bufferListOfQuantity) throws ExcessQuantityException {
        do {
            if (!bufferListOfPrice.isEmpty()) {
                int firstQuantity = bufferListOfQuantity.get(0);
                if (quantity - firstQuantity >= 0) {
                    quantity -= firstQuantity;
                    removedListOfPrice.add(bufferListOfPrice.remove(0));
                    removedListOfQuantity.add(bufferListOfQuantity.remove(0));
                } else {
                    removedListOfPrice.add(bufferListOfPrice.get(0));
                    removedListOfQuantity.add(quantity);
                    bufferListOfQuantity.set(0, firstQuantity - quantity);
                    break;
                }
            } else {
                throw new ExcessQuantityException();
            }
        } while (quantity > 0);
    }

    // REQUIRES:
    // listOfPrice.size() == listOfQuantity.size()
    // for all i: listOfQuantity at i is the quantity for listOfPrice at i
    // every price & quantity in listOfPrice, listOfQuantity > 0
    // EFFECTS: return the total cost by multiplying every price and quantity
    private int cost(ArrayList<Integer> listOfPrice, ArrayList<Integer> listOfQuantity) {
        int totalCost = 0;
        for (int i = 0; i < listOfPrice.size(); i++) {
            totalCost += listOfPrice.get(i) * listOfQuantity.get(i);
        }
        return totalCost;
    }

    // REQUIRES:
    // listOfPrice.size() == listOfQuantity.size()
    // EFFECTS: return if the holding is empty
    public boolean isEmpty() {
        return listOfQuantity.isEmpty();
    }

    // REQUIRES:
    // listOfPrice.size() == listOfQuantity.size()
    // for all i: listOfQuantity at i is the quantity for listOfPrice at i
    // EFFECTS: return the holding information as a string of dictionary
    public String toString() {
        String holdingInfo = "[";
        for (int i = 0; i < listOfPrice.size(); i++) {
            holdingInfo += " " + listOfPrice.get(i) + ":" + listOfQuantity.get(i) + " ";
        }
        holdingInfo += "]";
        return holdingInfo;
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonHolding = new JSONObject();
        JSONArray jsonPrices = new JSONArray();
        JSONArray jsonQuantities = new JSONArray();
        for (int i = 0; i < listOfPrice.size(); i++) {
            jsonPrices.put(new JSONObject().put("price", listOfPrice.get(i).toString()));
            jsonQuantities.put(new JSONObject().put("quantity", listOfQuantity.get(i).toString()));
        }
        jsonHolding.put("prices", jsonPrices);
        jsonHolding.put("quantities", jsonQuantities);
        return jsonHolding;
    }

}

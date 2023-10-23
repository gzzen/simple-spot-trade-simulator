package persistence;

import model.logger.Event;
import model.logger.EventLog;
import model.Portfolio;
import model.Security;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

// reader for portfolio in a given path
public class JsonReader {

    private String path;
    private EventLog log = EventLog.getInstance();

    // constructor that specifies a given path
    public JsonReader(String path) {
        this.path = path;
    }

    // reference: JsonSerializationDemo
    // EFFECTS: read the specified file and parse to portfolio object
    public Portfolio read() throws IOException {
        String stringPortfolio = readFile();
        log.logEvent(new Event("IO: Portfolio loaded"));
        return jsonToPortfolio(new JSONObject(stringPortfolio));
    }

    // reference: JsonSerializationDemo
    // EFFECTS: read every line in file and convert to string
    // throws IOException if IO error occurs
    private String readFile() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8)) {
            stream.forEach(s -> stringBuilder.append(s));
        }
        return stringBuilder.toString();
    }

    // EFFECTS: read the JSON object as portfolio
    private Portfolio jsonToPortfolio(JSONObject jsonPortfolio) {
        Portfolio portfolio = new Portfolio(jsonPortfolio.getInt("cash"));
        for (Object objectSecurity : jsonPortfolio.getJSONArray("securities")) {
            JSONObject jsonSecurity = (JSONObject) objectSecurity;
            ArrayList<String> codes = new ArrayList<>();
            String code = jsonSecurity.getString("code");
            JSONObject jsonHolding = jsonSecurity.getJSONObject("holding");
            JSONArray jsonQuantities = jsonHolding.getJSONArray("quantities");
            JSONArray jsonPrices = jsonHolding.getJSONArray("prices");
            for (int i = 0; i < jsonQuantities.length(); i++) {
                int quantity = jsonQuantities.getJSONObject(i).getInt("quantity");
                int price = jsonPrices.getJSONObject(i).getInt("price");
                if (!codes.contains(code)) {
                    Security security = new Security(code);
                    security.getHolding().record(price, quantity);
                    portfolio.getListOfSecurity().add(security);
                    codes.add(code);
                } else {
                    Security security = portfolio.getSecurityFromList(code);
                    security.getHolding().record(price, quantity);
                }
            }
        }
        return portfolio;
    }

}

package persistence;

import model.Portfolio;
import model.Security;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class JsonReaderTest {

    @Test
    void testSucceedRead() {
        JsonReader reader = new JsonReader("./data/test-read.json");
        Portfolio portfolio = new Portfolio(680);
        Portfolio readPortfolio = new Portfolio(0);
        Security appl = new Security("APPL");
        appl.getHolding().record(10, 10);
        appl.getHolding().record(20, 10);
        Security amzn = new Security("AMZN");
        amzn.getHolding().record(20, 1);
        portfolio.getListOfSecurity().add(appl);
        portfolio.getListOfSecurity().add(amzn);
        try {
            readPortfolio = reader.read();
        } catch (IOException e) {
            fail();
        }
        assertEquals(readPortfolio.toJson().toString(), portfolio.toJson().toString());
    }

    @Test
    void testReadWithIOExceptionThrown() {
        JsonReader reader = new JsonReader("./data/test.json");
        try {
            reader.read();
            fail();
        } catch (IOException e) {
            // expected
        }
    }

}

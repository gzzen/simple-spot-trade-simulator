package persistence;

import model.InsufficientFundException;
import model.Portfolio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class JsonWriterTest {

    JsonWriter writer;
    JsonWriter invalidWriter;
    JsonReader actualReader;
    JsonReader expectedReader;

    @BeforeEach
    void RunBefore() {
        writer = new JsonWriter("./data/test-write.json");
        invalidWriter = new JsonWriter("");
        actualReader = new JsonReader("./data/test-write.json");
        expectedReader = new JsonReader("./data/test-write-expected.json");

    }

    @Test
    void testGeneralWrite() {
        Portfolio portfolio = new Portfolio(1000);
        try {
            portfolio.buy("APPL", 10, 10);
            portfolio.buy("APPL", 20, 10);
            portfolio.buy("AMZN", 10, 10);
        } catch (InsufficientFundException e) {
            fail();
        }
        try {
            writer.open();
        } catch (FileNotFoundException e) {
            fail();
        }
        writer.write(portfolio);
        writer.close();
        try {
            assertEquals(actualReader.read().toString(), expectedReader.read().toString());
        } catch (IOException e) {
            fail();
        }

    }

    @Test
    void testInvalidPath() {
        try {
            invalidWriter.open();
            fail();
        } catch (FileNotFoundException e) {
            // expected
        }
    }

}

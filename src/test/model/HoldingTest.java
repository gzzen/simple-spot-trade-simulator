package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HoldingTest {

    Holding holding;

    @BeforeEach
    void runBefore() {
        holding = new Holding();
    }

    @Test
    void testRecord() {
        assertTrue(holding.toString().equals("[]"));

        holding.record(1000, 10);
        assertTrue(holding.toString().equals("[ 1000:10 ]"));

        holding.record(1000, 10);
        assertTrue(holding.toString().equals("[ 1000:20 ]"));

        holding.record(20, 100);
        assertTrue(holding.toString().equals("[ 1000:20  20:100 ]"));
    }

    @Test
    void testRemove() {
        try {
            assertEquals(0, holding.remove(1000));
        } catch (ExcessQuantityException e) {
            System.out.println("ExcessQuantityException caught - which is good!");
        }

        holding.record(1000, 10);
        try {
            assertEquals(0, holding.remove(1000));
        } catch (ExcessQuantityException e) {
            System.out.println("ExcessQuantityException caught - which is good!");
        }

        try {
            assertEquals(1000 * 10, holding.remove(10));
            assertTrue(holding.isEmpty());
        } catch (ExcessQuantityException e) {
            fail("unexpected ExcessQuantityException");
        }

        holding.record(1200, 10);
        holding.record(1000, 10);
        try {
            assertEquals(17000, holding.remove(15));
        } catch (ExcessQuantityException e) {
            fail("unexpected ExcessQuantityException");
        }
    }

    @Test
    void testIsEmpty() {
        assertTrue(holding.isEmpty());
        holding.record(1000, 10);
        holding.record(100, 5);
        assertFalse(holding.isEmpty());
        try {
            holding.remove(15);
        } catch (ExcessQuantityException e) {
            fail();
        }
        assertTrue(holding.isEmpty());
    }

    @Test
    void testToString() {
        assertTrue(holding.toString().equals("[]"));
        holding.record(1000, 10);
        assertTrue(holding.toString().equals("[ 1000:10 ]"));
        holding.record(1000, 1000);
        holding.record(20, 10);
        holding.record(2000, 802);
        assertTrue(holding.toString().equals("[ 1000:1010  20:10  2000:802 ]"));
    }

    @Test
    void testToJson() {
        String jsonTest = "{\"quantities\":[{\"quantity\":\"10\"}," +
                "{\"quantity\":\"10\"}],\"prices\":" +
                "[{\"price\":\"1000\"},{\"price\":\"20\"}]}";
        holding.record(1000, 10);
        holding.record(20, 10);
        assertEquals(jsonTest, holding.toJson().toString());
    }
}

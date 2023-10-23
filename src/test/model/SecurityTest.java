package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SecurityTest {

    Security apple;
    Security amazon;
    Security doge;

    @BeforeEach
    void runBefore() {
        apple = new Security("APPL");
        amazon = new Security("AMZN");
        doge = new Security("DOGE");
    }

    @Test
    void testBuy() {
        assertTrue(apple.toString().equals("Code: APPL, Holding: none"));
        apple.buy(500, 100);
        assertTrue(apple.toString().equals("Code: APPL, Holding: [ 500:100 ]"));
        apple.buy(505, 30);
        assertTrue(apple.toString().equals("Code: APPL, Holding: [ 500:100  505:30 ]"));

        amazon.buy(100, 10);
        amazon.buy(100, 1);
        assertTrue(amazon.toString().equals("Code: AMZN, Holding: [ 100:11 ]"));

    }

    @Test
    void testSell() {
        try {
            assertEquals(0, apple.sell(1));
        } catch (ExcessQuantityException e) {
            System.out.println("ExcessQuantityException caught - which is good!");
        }
        assertTrue(apple.toString().equals("Code: APPL, Holding: none"));

        amazon.buy(100, 100);
        amazon.buy(150, 100);
        try {
            assertEquals(100 * 100 + 150 * 50, amazon.sell(150));
        } catch (ExcessQuantityException e) {
            fail();
        }
        assertTrue(amazon.toString().equals("Code: AMZN, Holding: [ 150:50 ]"));

        try {
            assertEquals(0, amazon.sell(100));
        } catch (ExcessQuantityException e) {
            System.out.println("ExcessQuantityException caught - which is good!");
        }
        assertTrue(amazon.toString().equals("Code: AMZN, Holding: [ 150:50 ]"));

        try {
            assertEquals(150 * 50, amazon.sell(50));
        } catch (ExcessQuantityException e) {
            fail();
        }

    }

    @Test
    void testToString() {
        assertTrue(doge.toString().equals("Code: DOGE, Holding: none"));
        doge.buy(1, 1000);
        assertTrue(doge.toString().equals("Code: DOGE, Holding: [ 1:1000 ]"));
        doge.buy(2, 500);
        assertTrue(doge.toString().equals("Code: DOGE, Holding: [ 1:1000  2:500 ]"));
        doge.buy(1, 500);
        assertTrue(doge.toString().equals("Code: DOGE, Holding: [ 1:1500  2:500 ]"));
        try {
            doge.sell(500);
        } catch (ExcessQuantityException e) {
            fail();
        }
        assertTrue(doge.toString().equals("Code: DOGE, Holding: [ 1:1000  2:500 ]"));
        try {
            doge.sell(1500);
        } catch (ExcessQuantityException e) {
            fail();
        }
        assertTrue(doge.toString().equals("Code: DOGE, Holding: none"));
    }

    @Test
    void testToJson() {
        String jsonTest = "{\"holding\":{\"quantities\":[{\"quantity\":\"30\"},{\"quantity\":\"40\"}]," +
                "\"prices\":[{\"price\":\"100\"},{\"price\":\"200\"}]},\"code\":\"APPL\"}";
        apple.buy(100, 30);
        apple.buy(200, 40);
        assertEquals(jsonTest, apple.toJson().toString());
    }

}

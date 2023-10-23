package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PortfolioTest {
    Portfolio portfolio, poorPortfolio;

    @BeforeEach
    void runBefore() {
        portfolio = new Portfolio(10000);
        poorPortfolio = new Portfolio(0);
    }

    @Test
    void testResetCash() {
        portfolio.resetCash();
        assertEquals(0, portfolio.getCash());

        poorPortfolio.resetCash();
        assertEquals(0, poorPortfolio.getCash());
    }

    @Test
    void testAddCash() {
        portfolio.addCash(1000);
        assertEquals(11000, portfolio.getCash());

        portfolio.addCash(200);
        assertEquals(11200, portfolio.getCash());
    }

    @Test
    void testBuy() {
        try {
            poorPortfolio.buy("APPL", 500, 100);
        } catch (InsufficientFundException e) {
            System.out.println("InsufficientFundException caught");
        }
        assertTrue(poorPortfolio.toString().equals(""));
        assertEquals(0, poorPortfolio.getCash());

        try {
            portfolio.buy("APPL", 500, 10);
        } catch (InsufficientFundException e) {
            fail();
        }
        assertTrue(portfolio.toString().equals("Code: APPL, Holding: [ 500:10 ]\n"));
        assertEquals(5000, portfolio.getCash());

        try {
            portfolio.buy("AMZN", 100, 40);
        } catch (InsufficientFundException e) {
            fail();
        }
        assertTrue(portfolio.toString().equals("Code: APPL, Holding: [ 500:10 ]\n" +
                "Code: AMZN, Holding: [ 100:40 ]\n"));
        assertEquals(1000, portfolio.getCash());

        try {
            portfolio.buy("APPL", 500, 50);
        } catch (InsufficientFundException e) {
            System.out.println("InsufficientFundException caught");
        }

        try {
            portfolio.buy("AMZN", 100, 10);
        } catch (InsufficientFundException e) {
            System.out.println("InsufficientFundException caught");
        }
        assertTrue(portfolio.toString().equals("Code: APPL, Holding: [ 500:10 ]\n" +
                "Code: AMZN, Holding: [ 100:50 ]\n"));
        assertEquals(0, portfolio.getCash());
    }

    @Test
    void testSell() {
        try {
            portfolio.sell("APPL", 100, 10);
        } catch (SecurityNotFoundException e) {
            // expected
        } catch (ExcessQuantityException e) {
            fail();
        }
        assertTrue(portfolio.toString().equals(""));
        assertEquals(10000, portfolio.getCash());

        try {
            portfolio.buy("APPL", 500, 10);
            portfolio.buy("AMZN", 100, 10);
        } catch (InsufficientFundException e) {
            fail();
        }

        try {
            portfolio.sell("APPL", 600, 5);
        } catch (SecurityNotFoundException e) {
            fail();
        } catch (ExcessQuantityException e) {
            fail();
        }

        assertTrue(portfolio.toString().equals("Code: APPL, Holding: [ 500:5 ]\n" +
                "Code: AMZN, Holding: [ 100:10 ]\n"));
        assertEquals(7000, portfolio.getCash());

        try {
            portfolio.sell("DOGE", 100, 10);
        } catch (SecurityNotFoundException e) {
            // expected
        } catch (ExcessQuantityException e) {
            fail();
        }
        assertTrue(portfolio.toString().equals("Code: APPL, Holding: [ 500:5 ]\n" +
                "Code: AMZN, Holding: [ 100:10 ]\n"));
        assertEquals(7000, portfolio.getCash());
    }

    @Test
    void testToString() {
        assertTrue(portfolio.toString().equals(""));
        try {
            portfolio.buy("APPL", 500, 10);
            portfolio.buy("AMZN", 100, 5);
            assertTrue(portfolio.toString().equals("Code: APPL, Holding: [ 500:10 ]\n" +
                    "Code: AMZN, Holding: [ 100:5 ]\n"));
            portfolio.sell("APPL", 500, 10);
            assertTrue(portfolio.toString().equals("Code: APPL, Holding: none\n" +
                    "Code: AMZN, Holding: [ 100:5 ]\n"));
        } catch (InsufficientFundException e) {
            fail();
        } catch (SecurityNotFoundException e) {
            fail();
        } catch (ExcessQuantityException e) {
            fail();
        }
    }

    @Test
    void testToJson() {
        String jsonTest =
                "{\"cash\":8000,\"securities\":[{\"holding\":{\"quantities\":[{\"quantity\":\"10\"}]," +
                        "\"prices\":[{\"price\":\"100\"}]}," +
                        "\"code\":\"APPL\"}," +
                        "{\"holding\":{\"quantities\":[{\"quantity\":\"10\"}],\"prices\":[{\"price\":\"100\"}]}," +
                        "\"code\":\"AMZN\"}]}";
        try {
            portfolio.buy("APPL", 100, 10);
            portfolio.buy("AMZN", 100, 10);
        } catch (InsufficientFundException e) {
            fail();
        }
        assertEquals(jsonTest, portfolio.toJson().toString());

    }

    @Test
    void testGetSecurityFromList() {
        try {
            portfolio.buy("APPL", 100, 10);
            portfolio.buy("AMZN", 100, 10);
        } catch (InsufficientFundException e) {
            fail();
        }
        assertEquals(portfolio.getListOfSecurity().get(0), portfolio.getSecurityFromList("APPL"));
        assertEquals(portfolio.getListOfSecurity().get(1), portfolio.getSecurityFromList("AMZN"));
        assertEquals(null, portfolio.getSecurityFromList("FB"));
    }
}

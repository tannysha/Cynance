package com.coms309.Cynance;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class InvestmentTest {

    @LocalServerPort
    private int port;

    @Before
    public void setup() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void testGetStockChart() {
        String symbol = "AAPL";
        String from = "2024-04-01";
        String to = "2024-04-10";

        Response response = RestAssured.given()
                .queryParam("symbol", symbol)
                .queryParam("from", from)
                .queryParam("to", to)
                .get("/api/investments/graph");

        assertEquals(200, response.getStatusCode());

        String json = response.getBody().asString();
        assertTrue(json.contains("graph"), "Response should contain graph data.");
        assertTrue(json.contains("latest"), "Response should contain latest data.");
    }
}

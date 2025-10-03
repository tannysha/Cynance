package com.coms309.Cynance;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class SearchTest {

    @LocalServerPort
    private int port;

    @Before
    public void setup() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        registerTestUserAndData();
    }

    private void registerTestUserAndData() {
        Map<String, String> user = new HashMap<>();
        user.put("username", "search_test_user");
        user.put("password", "password");
        user.put("email", "search_test_user@example.com");

        RestAssured.given()
                .contentType("application/json")
                .body(user)
                .post("/api/users/register");

        Map<String, Object> expense = new HashMap<>();
        expense.put("title", "Netflix Expense");
        expense.put("price", "12.99");
        expense.put("date", "2025-05-01");
        expense.put("description", "Netflix subscription monthly");

        RestAssured.given()
                .contentType("application/json")
                .body(expense)
                .post("/api/expenses/add/search_test_user");

        Map<String, Object> income = new HashMap<>();
        income.put("source", "Part-time Job");
        income.put("amount", "100.00");
        income.put("date", "2025-05-01");
        income.put("description", "Netflix payout");

        RestAssured.given()
                .contentType("application/json")
                .body(income)
                .post("/api/incomes/add/search_test_user");

        Map<String, Object> sub = new HashMap<>();
        sub.put("title", "Netflix");
        sub.put("startDate", "2025-01-01");
        sub.put("endDate", "2025-12-31");
        sub.put("description", "Netflix plan");
        sub.put("price", "12.99");

        RestAssured.given()
                .contentType("application/json")
                .body(sub)
                .post("/api/subscriptions/add/search_test_user");
    }

    @Test
    public void testSearchReturnsAllMatchingItems() {
        Response response = RestAssured
                .given()
                .queryParam("query", "Netflix")
                .get("/api/search/search_test_user");

        assertEquals(200, response.statusCode());
        String json = response.asString();
        assertTrue(json.contains("Netflix"));
        assertTrue(json.contains("Netflix plan"));
        assertTrue(json.contains("Netflix subscription monthly"));
    }
}

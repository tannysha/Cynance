package com.coms309.Cynance;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExpensesTest {

    @LocalServerPort
    private int port;

    private static final String TEST_USER = "expense_test_user";

    @Before
    public void setup() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        registerTestUser();
    }

    private void registerTestUser() {
        Map<String, String> user = new HashMap<>();
        user.put("username", TEST_USER);
        user.put("password", "password");
        user.put("email", TEST_USER + "@example.com");

        RestAssured.given()
                .contentType("application/json")
                .body(user)
                .post("/api/users/register");
    }

    @Test
    public void test1_addExpense() {
        Map<String, Object> expense = new HashMap<>();
        expense.put("title", "Groceries");
        expense.put("date", "2025-05-04");
        expense.put("description", "Walmart");
        expense.put("price", "50.0");

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(expense)
                .post("/api/expenses/add/" + TEST_USER);

        assertEquals(200, response.getStatusCode());
        assertTrue(response.asString().contains("Expense added successfully"));
    }

    @Test
    public void test2_getExpense() {
        Response response = RestAssured.get("/api/expenses/get/" + TEST_USER + "/Groceries");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.asString().contains("Walmart"));
    }

    @Test
    public void test3_updateExpense() {
        Map<String, Object> updatedExpense = new HashMap<>();
        updatedExpense.put("title", "Groceries"); // Required to remain the same
        updatedExpense.put("date", "2025-05-06");
        updatedExpense.put("description", "Target");
        updatedExpense.put("price", "55.0");

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(updatedExpense)
                .put("/api/expenses/update/" + TEST_USER + "/Groceries");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.asString().contains("updated successfully"));
    }

    @Test
    public void test4_listExpenses() {
        Response response = RestAssured.get("/api/expenses/list/" + TEST_USER);

        assertEquals(200, response.getStatusCode());
        assertTrue(response.asString().contains("Groceries"));
    }

    @Test
    public void test5_categoryAnalytics() {
        Response response = RestAssured.get("/api/expenses/analytics/" + TEST_USER);

        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 404);
    }

    @Test
    public void test6_maxCategoryAnalytics() {
        Response response = RestAssured.get("/api/expenses/analytics/max-full/" + TEST_USER);

        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 404);
    }

    @Test
    public void test7_deleteExpense() {
        Response response = RestAssured.delete("/api/expenses/delete/" + TEST_USER + "/Groceries");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.asString().contains("deleted successfully"));
    }
}

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
public class SubscriptionTest {

    @LocalServerPort
    private int port;

    @Before
    public void setup() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        registerTestUser();
    }

    private void registerTestUser() {
        Map<String, String> user = new HashMap<>();
        user.put("username", "sub_test_user");
        user.put("password", "password");
        user.put("email", "sub_test_user@example.com");

        RestAssured.given()
                .contentType("application/json")
                .body(user)
                .post("/api/users/register");
    }

    @Test
    public void testAddSubscription() {
        Map<String, Object> subscription = new HashMap<>();
        subscription.put("title", "Spotify");
        subscription.put("startDate", "2024-01-01");
        subscription.put("endDate", "2024-12-31");
        subscription.put("description", "Music Streaming");
        subscription.put("price", "9.99");

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(subscription)
                .post("/api/subscriptions/add/sub_test_user");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.asString().contains("Subscription added successfully"));
    }

    @Test
    public void testListSubscriptions() {
        Response response = RestAssured.get("/api/subscriptions/list/sub_test_user");
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testUpdateSubscription() {
        // Register the user if not already
        registerTestUser();

        // Add subscription
        Map<String, Object> subscription = new HashMap<>();
        subscription.put("title", "Spotify");
        subscription.put("startDate", "2024-01-01");
        subscription.put("endDate", "2024-12-31");
        subscription.put("description", "Music");
        subscription.put("price", "9.99");

        Response addResponse = RestAssured.given()
                .contentType("application/json")
                .body(subscription)
                .post("/api/subscriptions/add/sub_test_user");
        assertEquals(200, addResponse.getStatusCode());

        // Update it with the same title
        subscription.put("price", "7.99");
        subscription.put("description", "Updated Music");

        Response updateResponse = RestAssured.given()
                .contentType("application/json")
                .body(subscription)
                .put("/api/subscriptions/update/sub_test_user");
        assertEquals(200, updateResponse.getStatusCode());
    }


    @Test
    public void testDeleteSubscription() {
        Map<String, String> request = new HashMap<>();
        request.put("title", "Spotify");

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(request)
                .delete("/api/subscriptions/remove/sub_test_user");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.asString().contains("Subscription removed successfully"));
    }
}

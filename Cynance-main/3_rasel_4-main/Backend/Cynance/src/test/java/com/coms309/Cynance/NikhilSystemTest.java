package com.coms309.Cynance;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class NikhilSystemTest {

    @LocalServerPort
    private int port;

    @Before
    public void setup() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    // Shared user registration logic
    private void registerTestUser() {
        Map<String, String> user = new HashMap<>();
        user.put("username", "test123");
        user.put("password", "test123");
        user.put("email", "test123@test.com");

        RestAssured.given()
                .contentType("application/json")
                .body(user)
                .post("/api/users/register");
    }

    @Test
    public void testUserRegistration() {
        Map<String, String> user = new HashMap<>();
        user.put("username", "test123");
        user.put("password", "test123");
        user.put("email", "test123@test.com");

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(user)
                .post("/api/users/register");

        if (response.getStatusCode() != 201 && response.getStatusCode() != 409) {
            throw new RuntimeException("User registration failed: " + response.getStatusCode());//needed as regiter is donw twice thus meaning theat the chance of the register failing is much higher

        }
    }

    @Test
    public void testUserLogin() throws JSONException {
        registerTestUser();

        Map<String, String> loginPayload = new HashMap<>();
        loginPayload.put("username", "test123");
        loginPayload.put("password", "test123");

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(loginPayload)
                .post("/api/users/login");

        assertEquals(200, response.getStatusCode());

        JSONObject responseJson = new JSONObject(response.getBody().asString());
        assertEquals("Login successful", responseJson.get("message"));
    }

    @Test
    public void testUpdateEmail() {
        registerTestUser();

        Map<String, String> newEmail = new HashMap<>();
        newEmail.put("newEmail", "updatedtest123@test.com");

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(newEmail)
                .put("/api/users/update/email/test123");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testGetUserInfo() throws JSONException {
        registerTestUser();

        Response response = RestAssured.given()
                .get("/api/users/username/test123");

        assertEquals(200, response.getStatusCode());
        JSONObject json = new JSONObject(response.getBody().asString());
        assertEquals("test123", json.get("username"));
        assertEquals("updatedtest123@test.com", json.get("email"));
    }
}

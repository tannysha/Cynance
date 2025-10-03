package com.coms309.Cynance;

import com.coms309.Cynance.model.Admin;
import com.coms309.Cynance.model.BugReport;
import com.coms309.Cynance.model.User;
import com.coms309.Cynance.repository.AdminRepository;
import com.coms309.Cynance.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NikhilAdminTest {

    @LocalServerPort
    private int port;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setup() {
        RestAssured.port = port;

        if (!adminRepository.findByUsername("admin1").isPresent()) {
            Admin admin = new Admin("admin1", "pass123", "Admin One");
            adminRepository.save(admin);
        }

        if (!userRepository.findByUsername("testUser").isPresent()) {
            User user = new User();
            user.setUsername("testUser");
            user.setEmail("test@xyz.com");
            user.setPassword("password");
            userRepository.save(user);
        }
    }

    @Test
    public void testAdminLoginSuccess() {
        given()
                .contentType(ContentType.JSON)
                .body(new Admin("admin1", "adminpass123", null))
                .when()
                .post("/admin/login")
                .then()
                .statusCode(200)
                .body(equalTo("Login successful."));
    }

    @Test
    public void testAdminLoginFail() {
        given()
                .contentType(ContentType.JSON)
                .body(new Admin("admin1", "wrongpass", null))
                .when()
                .post("/admin/login")
                .then()
                .statusCode(401)
                .body(equalTo("Invalid credentials."));
    }

    @Test
    public void testSubmitBug() {
        BugReport report = new BugReport("Bug title", "Description of bug", "admin1");

        given()
                .contentType(ContentType.JSON)
                .body(report)
                .when()
                .post("/admin/bug-report")
                .then()
                .statusCode(200)
                .body(equalTo("Bug report submitted."));
    }

    @Test
    public void testGetAllBugs() {
        given()
                .when()
                .get("/admin/admin1/getBug")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
    public void testBanUser() {
        given()
                .when()
                .put("/admin/admin1/ban/testUser")
                .then()
                .statusCode(200)
                .body(containsString("has been banned"));
    }

    @Test
    public void testUnbanUser() {
        given()
                .when()
                .put("/admin/admin1/unban/testUser")
                .then()
                .statusCode(200)
                .body(containsString("has been unbanned"));
    }
}

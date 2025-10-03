package com.dbIntegration.exp3;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomePage {
    @RequestMapping("/")
    public String welcome() {
        return "Welcome to the backend of a company";
    }
    @RequestMapping("/getStarted")
    public String getStarted() {
        return "To get started chose one of three paths, Employee, Building or Project";
    }
}

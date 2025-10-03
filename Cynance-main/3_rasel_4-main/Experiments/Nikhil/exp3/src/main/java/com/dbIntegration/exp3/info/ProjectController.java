package com.dbIntegration.exp3.info;

import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
public class ProjectController {
    private LinkedList<Project> projects = new LinkedList<>();

    // Add a new project
    @PostMapping("/addProject/{projectID}/{projectName}")
    public Project addProject(@PathVariable String projectID, @PathVariable String projectName) {
        Project project = new Project(projectName, projectID);
        projects.add(project);
        return project;
    }

    // Update an existing project
    @PutMapping("/updateProject/{projectID}/{projectName}")
    public Project updateProject(@PathVariable String projectID, @PathVariable String projectName) {
        for (Project project : projects) {
            if (project.getProjectID().equals(projectID)) {
                project.setProjectName(projectName);
                return project;
            }
        }
        return null;
    }

    // Get all projects
    @GetMapping("/projects")
    public List<Project> getAllProjects() {
        return projects;
    }

    // Get project by ID
    @GetMapping("/project/{projectID}")
    public Project getProjectById(@PathVariable String projectID) {
        for (Project project : projects) {
            if (project.getProjectID().equals(projectID)) {
                return project;
            }
        }
        return null;
    }

}    // Delete a project
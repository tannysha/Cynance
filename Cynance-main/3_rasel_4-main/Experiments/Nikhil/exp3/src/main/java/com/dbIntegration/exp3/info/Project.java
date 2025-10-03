package com.dbIntegration.exp3.info;

public class Project {
    private String projectName;
    private String projectID;

    public Project(String projectName, String projectID) {
        this.projectName = projectName;
        this.projectID = projectID;
    }
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public String getProjectID() {
        return projectID;
    }
    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }
    public Project(String projectID) {
        this.projectID = projectID;
    }
}

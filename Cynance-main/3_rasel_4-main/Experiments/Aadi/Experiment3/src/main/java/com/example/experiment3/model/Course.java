package com.example.experiment3.model;


public class Course {
    private Long id;
    private String courseNumber;
    private String courseName;
    private String description;
    private String prerequisites;

    public Course() {}

    public Course(Long id, String courseNumber, String courseName, String description, String prerequisites) {
        this.id = id;
        this.courseNumber = courseNumber;
        this.courseName = courseName;
        this.description = description;
        this.prerequisites = prerequisites;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCourseNumber() { return courseNumber; }
    public void setCourseNumber(String courseNumber) { this.courseNumber = courseNumber; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPrerequisites() { return prerequisites; }
    public void setPrerequisites(String prerequisites) { this.prerequisites = prerequisites; }

}

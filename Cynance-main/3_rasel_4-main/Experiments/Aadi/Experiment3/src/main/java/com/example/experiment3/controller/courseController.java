package com.example.experiment3.controller;

import com.example.experiment3.model.Course;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class courseController {
    private List<Course> courses = new ArrayList<>();
    private Long idCounter = 1L;

    @GetMapping
    public List<Course> getAllCourses() {
        return courses;
    }

    @GetMapping("/{id}")
    public Course getCourse(@PathVariable Long id) {
        for (Course course : courses) {
            if (course.getId().equals(id)) {
                return course;
            }
        }
        return null;
    }

    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        course.setId(idCounter++);
        courses.add(course);
        return course;
    }

    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Long id, @RequestBody Course updatedCourse) {
        for (Course course : courses) {
            if (course.getId().equals(id)) {
                course.setCourseNumber(updatedCourse.getCourseNumber());
                course.setCourseName(updatedCourse.getCourseName());
                course.setDescription(updatedCourse.getDescription());
                course.setPrerequisites(updatedCourse.getPrerequisites());
                return course;
            }
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable Long id) {
        Iterator<Course> iterator = courses.iterator();
        while (iterator.hasNext()) {
            Course course = iterator.next();
            if (course.getId().equals(id)) {
                iterator.remove();
            }
        }
    }


}

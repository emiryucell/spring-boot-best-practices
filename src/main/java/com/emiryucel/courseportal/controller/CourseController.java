package com.emiryucel.courseportal.controller;

import com.emiryucel.courseportal.service.CourseService;
import com.emiryucel.courseportal.dto.CourseDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
@Validated
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);
    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
        logger.info("CourseController initialized");
    }

    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(@Valid @RequestBody CourseDTO courseDTO) {
        logger.info("Creating new course with title: {}", courseDTO.getTitle());
        CourseDTO createdCourse = courseService.createCourse(courseDTO);
        logger.info("Course created successfully with ID: {}", createdCourse.getId());
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable String id, @Valid @RequestBody CourseDTO courseDTO) {
        logger.info("Updating course with ID: {}", id);
        CourseDTO updatedCourse = courseService.updateCourse(id, courseDTO);
        logger.info("Course with ID: {} updated successfully", id);
        return ResponseEntity.ok(updatedCourse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable String id) {
        logger.debug("Fetching course with ID: {}", id);
        CourseDTO course = courseService.getCourseById(id);
        if (course != null) {
            logger.debug("Course with ID: {} found", id);
        } else {
            logger.warn("Course with ID: {} not found", id);
        }
        return ResponseEntity.ok(course);
    }

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        logger.debug("Fetching all courses");
        List<CourseDTO> courses = courseService.getAllCourses();
        logger.debug("Retrieved {} courses", courses.size());
        return ResponseEntity.ok(courses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
        logger.info("Deleting course with ID: {}", id);
        courseService.deleteCourse(id);
        logger.info("Course with ID: {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
} 
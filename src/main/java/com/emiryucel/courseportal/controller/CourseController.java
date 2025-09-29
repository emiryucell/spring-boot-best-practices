package com.emiryucel.courseportal.controller;

import com.emiryucel.courseportal.dto.CourseDTO;
import com.emiryucel.courseportal.dto.CourseResponseDTO;
import com.emiryucel.courseportal.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
@Validated
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;


    @PostMapping
    public ResponseEntity<CourseResponseDTO> createCourse(@Valid @RequestBody CourseDTO courseDTO) {
        log.info("Creating new course with title: {}", courseDTO.getTitle());
        CourseResponseDTO createdCourse = courseService.createCourse(courseDTO);
        log.info("Course created successfully with ID: {}", createdCourse.getId());
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> updateCourse(@PathVariable String id, @Valid @RequestBody CourseDTO courseDTO) {
        log.info("Updating course with ID: {}", id);
        CourseResponseDTO updatedCourse = courseService.updateCourse(id, courseDTO);
        log.info("Course with ID: {} updated successfully", id);
        return ResponseEntity.ok(updatedCourse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable String id) {
        log.debug("Fetching course with ID: {}", id);
        CourseResponseDTO course = courseService.getCourseById(id);
        log.debug("Course with ID: {} found", id);
        return ResponseEntity.ok(course);
    }

    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses() {
        log.debug("Fetching all courses");
        List<CourseResponseDTO> courses = courseService.getAllCourses();
        log.debug("Retrieved {} courses", courses.size());
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<CourseResponseDTO>> getAllCoursesPaginated(Pageable pageable) {
        log.debug("Fetching courses with pagination - page: {}, size: {}, sort: {}", 
                  pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        
        Page<CourseResponseDTO> courses = courseService.getAllCourses(pageable);
        log.debug("Retrieved {} courses on page {} of {}", 
                  courses.getNumberOfElements(), 
                  courses.getNumber() + 1, 
                  courses.getTotalPages());
        
        return ResponseEntity.ok(courses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
        log.info("Deleting course with ID: {}", id);
        courseService.deleteCourse(id);
        log.info("Course with ID: {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }


} 
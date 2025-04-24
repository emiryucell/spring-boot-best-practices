package com.emiryucel.courseportal.controller;

import com.emiryucel.courseportal.service.LecturerService;
import com.emiryucel.courseportal.dto.LecturerDTO;
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
@RequestMapping("/lecturer")
@Validated
public class LecturerController {

    private static final Logger logger = LoggerFactory.getLogger(LecturerController.class);
    private final LecturerService lecturerService;

    @Autowired
    public LecturerController(LecturerService lecturerService) {
        this.lecturerService = lecturerService;
        logger.info("LecturerController initialized");
    }

    @PostMapping
    public ResponseEntity<LecturerDTO> createLecturer(@Valid @RequestBody LecturerDTO lecturerDTO) {
        logger.info("Creating new lecturer: {} {}", lecturerDTO.getFirstName(), lecturerDTO.getLastName());
        LecturerDTO createdLecturer = lecturerService.createLecturer(lecturerDTO);
        logger.info("Lecturer created successfully with ID: {}", createdLecturer.getId());
        return new ResponseEntity<>(createdLecturer, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LecturerDTO> updateLecturer(@PathVariable String id, @Valid @RequestBody LecturerDTO lecturerDTO) {
        logger.info("Updating lecturer with ID: {}", id);
        LecturerDTO updatedLecturer = lecturerService.updateLecturer(id, lecturerDTO);
        logger.info("Lecturer with ID: {} updated successfully", id);
        return ResponseEntity.ok(updatedLecturer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LecturerDTO> getLecturerById(@PathVariable String id) {
        logger.debug("Fetching lecturer with ID: {}", id);
        LecturerDTO lecturer = lecturerService.getLecturerById(id);
        logger.debug("Lecturer found with ID: {}", id);
        return ResponseEntity.ok(lecturer);
    }

    @GetMapping
    public ResponseEntity<List<LecturerDTO>> getAllLecturers() {
        logger.debug("Fetching all lecturers");
        List<LecturerDTO> lecturers = lecturerService.getAllLecturers();
        logger.debug("Retrieved {} lecturers", lecturers.size());
        return ResponseEntity.ok(lecturers);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLecturer(@PathVariable String id) {
        logger.info("Deleting lecturer with ID: {}", id);
        lecturerService.deleteLecturer(id);
        logger.info("Lecturer with ID: {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lecturerId}/courses/{courseId}")
    public ResponseEntity<LecturerDTO> assignCourse(@PathVariable String lecturerId, @PathVariable String courseId) {
        logger.info("Assigning course {} to lecturer {}", courseId, lecturerId);
        LecturerDTO updatedLecturer = lecturerService.assignCourse(lecturerId, courseId);
        logger.info("Course assigned successfully");
        return ResponseEntity.ok(updatedLecturer);
    }

    @DeleteMapping("/{lecturerId}/courses/{courseId}")
    public ResponseEntity<LecturerDTO> removeCourse(@PathVariable String lecturerId, @PathVariable String courseId) {
        logger.info("Removing course {} from lecturer {}", courseId, lecturerId);
        LecturerDTO updatedLecturer = lecturerService.removeCourse(lecturerId, courseId);
        logger.info("Course removed successfully");
        return ResponseEntity.ok(updatedLecturer);
    }
} 
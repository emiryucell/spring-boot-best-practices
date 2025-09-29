package com.emiryucel.courseportal.controller;

import com.emiryucel.courseportal.dto.LecturerDTO;
import com.emiryucel.courseportal.dto.LecturerResponseDTO;
import com.emiryucel.courseportal.service.LecturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lecturer")
@Validated
@RequiredArgsConstructor
@Slf4j
public class LecturerController {

    private final LecturerService lecturerService;


    @PostMapping
    public ResponseEntity<LecturerResponseDTO> createLecturer(@Valid @RequestBody LecturerDTO lecturerDTO) {
        log.info("Creating new lecturer: {} {}", lecturerDTO.getFirstName(), lecturerDTO.getLastName());
        LecturerResponseDTO createdLecturer = lecturerService.createLecturer(lecturerDTO);
        log.info("Lecturer created successfully with name: {}", createdLecturer.getFirstName()+createdLecturer.getLastName());
        return new ResponseEntity<>(createdLecturer, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LecturerResponseDTO> updateLecturer(@PathVariable String id, @Valid @RequestBody LecturerDTO lecturerDTO) {
        log.info("Updating lecturer with ID: {}", id);
        LecturerResponseDTO updatedLecturer = lecturerService.updateLecturer(id, lecturerDTO);
        log.info("Lecturer with ID: {} updated successfully", id);
        return ResponseEntity.ok(updatedLecturer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LecturerResponseDTO> getLecturerById(@PathVariable String id) {
        log.debug("Fetching lecturer with ID: {}", id);
        LecturerResponseDTO lecturer = lecturerService.getLecturerById(id);
        log.debug("Lecturer found with ID: {}", id);
        return ResponseEntity.ok(lecturer);
    }

    @GetMapping
    public ResponseEntity<List<LecturerResponseDTO>> getAllLecturers() {
        log.debug("Fetching all lecturers");
        List<LecturerResponseDTO> lecturers = lecturerService.getAllLecturers();
        log.debug("Retrieved {} lecturers", lecturers.size());
        return ResponseEntity.ok(lecturers);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLecturer(@PathVariable String id) {
        log.info("Deleting lecturer with ID: {}", id);
        lecturerService.deleteLecturer(id);
        log.info("Lecturer with ID: {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lecturerId}/courses/{courseId}")
    public ResponseEntity<LecturerResponseDTO> assignCourse(@PathVariable String lecturerId, @PathVariable String courseId) {
        log.info("Assigning course {} to lecturer {}", courseId, lecturerId);
        LecturerResponseDTO updatedLecturer = lecturerService.assignCourse(lecturerId, courseId);
        log.info("Course assigned successfully");
        return ResponseEntity.ok(updatedLecturer);
    }

    @DeleteMapping("/{lecturerId}/courses/{courseId}")
    public ResponseEntity<LecturerResponseDTO> removeCourse(@PathVariable String lecturerId, @PathVariable String courseId) {
        log.info("Removing course {} from lecturer {}", courseId, lecturerId);
        LecturerResponseDTO updatedLecturer = lecturerService.removeCourse(lecturerId, courseId);
        log.info("Course removed successfully");
        return ResponseEntity.ok(updatedLecturer);
    }
} 
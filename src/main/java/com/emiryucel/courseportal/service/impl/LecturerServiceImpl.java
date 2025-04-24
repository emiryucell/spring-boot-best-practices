package com.emiryucel.courseportal.service.impl;

import com.emiryucel.courseportal.model.Lecturer;
import com.emiryucel.courseportal.model.Course;
import com.emiryucel.courseportal.repository.LecturerRepository;
import com.emiryucel.courseportal.repository.CourseRepository;
import com.emiryucel.courseportal.service.LecturerService;
import com.emiryucel.courseportal.dto.LecturerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.HashSet;

@Service
@Transactional
public class LecturerServiceImpl implements LecturerService {

    private static final Logger logger = LoggerFactory.getLogger(LecturerServiceImpl.class);
    private final LecturerRepository lecturerRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public LecturerServiceImpl(LecturerRepository lecturerRepository, CourseRepository courseRepository) {
        this.lecturerRepository = lecturerRepository;
        this.courseRepository = courseRepository;
        logger.info("LecturerServiceImpl initialized");
    }

    @Override
    public LecturerDTO createLecturer(LecturerDTO lecturerDTO) {
        logger.debug("Converting DTO to entity for lecturer: {} {}", lecturerDTO.getFirstName(), lecturerDTO.getLastName());
        
        // Check if email already exists
        if (lecturerRepository.findByEmail(lecturerDTO.getEmail()).isPresent()) {
            logger.error("Email already exists: {}", lecturerDTO.getEmail());
            throw new RuntimeException("Email already exists: " + lecturerDTO.getEmail());
        }
        
        Lecturer lecturer = convertToEntity(lecturerDTO);
        logger.debug("Saving lecturer to database");
        Lecturer savedLecturer = lecturerRepository.save(lecturer);
        logger.debug("Lecturer saved successfully with ID: {}", savedLecturer.getId());
        return convertToDTO(savedLecturer);
    }

    @Override
    public LecturerDTO updateLecturer(String id, LecturerDTO lecturerDTO) {
        logger.debug("Fetching lecturer with ID: {} for update", id);
        Lecturer existingLecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Lecturer not found with id: {}", id);
                    return new RuntimeException("Lecturer not found with id: " + id);
                });
        
        // Check if email is being changed and if new email already exists
        if (!existingLecturer.getEmail().equals(lecturerDTO.getEmail())) {
            if (lecturerRepository.findByEmail(lecturerDTO.getEmail()).isPresent()) {
                logger.error("Email already exists: {}", lecturerDTO.getEmail());
                throw new RuntimeException("Email already exists: " + lecturerDTO.getEmail());
            }
        }
        
        logger.debug("Updating lecturer fields for ID: {}", id);
        existingLecturer.setFirstName(lecturerDTO.getFirstName());
        existingLecturer.setLastName(lecturerDTO.getLastName());
        existingLecturer.setEmail(lecturerDTO.getEmail());
        existingLecturer.setDepartment(lecturerDTO.getDepartment());
        existingLecturer.setBio(lecturerDTO.getBio());
        
        logger.debug("Saving updated lecturer");
        Lecturer updatedLecturer = lecturerRepository.save(existingLecturer);
        logger.debug("Lecturer updated successfully");
        return convertToDTO(updatedLecturer);
    }

    @Override
    public LecturerDTO getLecturerById(String id) {
        logger.debug("Fetching lecturer with ID: {}", id);
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Lecturer not found with id: {}", id);
                    return new RuntimeException("Lecturer not found with id: " + id);
                });
        logger.debug("Lecturer found successfully");
        return convertToDTO(lecturer);
    }

    @Override
    public List<LecturerDTO> getAllLecturers() {
        logger.debug("Fetching all lecturers");
        List<Lecturer> lecturers = lecturerRepository.findAll();
        logger.debug("Found {} lecturers", lecturers.size());
        return lecturers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteLecturer(String id) {
        logger.debug("Deleting lecturer with ID: {}", id);
        lecturerRepository.deleteById(id);
        logger.debug("Lecturer deleted successfully");
    }

    @Override
    public LecturerDTO assignCourse(String lecturerId, String courseId) {
        logger.debug("Assigning course {} to lecturer {}", courseId, lecturerId);
        Lecturer lecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new RuntimeException("Lecturer not found with id: " + lecturerId));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        
        course.setLecturer(lecturer);
        lecturer.getCourses().add(course);
        
        Lecturer updatedLecturer = lecturerRepository.save(lecturer);
        logger.debug("Course assigned successfully");
        return convertToDTO(updatedLecturer);
    }

    @Override
    public LecturerDTO removeCourse(String lecturerId, String courseId) {
        logger.debug("Removing course {} from lecturer {}", courseId, lecturerId);
        Lecturer lecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new RuntimeException("Lecturer not found with id: " + lecturerId));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        
        course.setLecturer(null);
        lecturer.getCourses().remove(course);
        
        Lecturer updatedLecturer = lecturerRepository.save(lecturer);
        logger.debug("Course removed successfully");
        return convertToDTO(updatedLecturer);
    }

    private Lecturer convertToEntity(LecturerDTO lecturerDTO) {
        logger.trace("Converting LecturerDTO to Lecturer entity");
        Lecturer lecturer = new Lecturer();
        lecturer.setFirstName(lecturerDTO.getFirstName());
        lecturer.setLastName(lecturerDTO.getLastName());
        lecturer.setEmail(lecturerDTO.getEmail());
        lecturer.setDepartment(lecturerDTO.getDepartment());
        lecturer.setBio(lecturerDTO.getBio());
        return lecturer;
    }

    private LecturerDTO convertToDTO(Lecturer lecturer) {
        logger.trace("Converting Lecturer entity to LecturerDTO");
        LecturerDTO lecturerDTO = new LecturerDTO();
        lecturerDTO.setId(lecturer.getId());
        lecturerDTO.setFirstName(lecturer.getFirstName());
        lecturerDTO.setLastName(lecturer.getLastName());
        lecturerDTO.setEmail(lecturer.getEmail());
        lecturerDTO.setDepartment(lecturer.getDepartment());
        lecturerDTO.setBio(lecturer.getBio());
        lecturerDTO.setCreatedAt(lecturer.getCreatedAt());
        lecturerDTO.setUpdatedAt(lecturer.getUpdatedAt());
        
        // Convert course IDs
        if (lecturer.getCourses() != null) {
            lecturerDTO.setCourseIds(lecturer.getCourses().stream()
                    .map(Course::getId)
                    .collect(Collectors.toSet()));
        } else {
            lecturerDTO.setCourseIds(new HashSet<>());
        }
        
        return lecturerDTO;
    }
} 
package com.emiryucel.courseportal.service.impl;

import com.emiryucel.courseportal.model.Course;
import com.emiryucel.courseportal.repository.CourseRepository;
import com.emiryucel.courseportal.service.CourseService;
import com.emiryucel.courseportal.dto.CourseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private static final Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);
    private final CourseRepository courseRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
        logger.info("CourseServiceImpl initialized");
    }

    @Override
    public CourseDTO createCourse(CourseDTO courseDTO) {
        logger.debug("Converting DTO to entity for course: {}", courseDTO.getTitle());
        Course course = convertToEntity(courseDTO);
        logger.debug("Saving course to database");
        Course savedCourse = courseRepository.save(course);
        logger.debug("Course saved successfully with ID: {}", savedCourse.getId());
        return convertToDTO(savedCourse);
    }

    @Override
    public CourseDTO updateCourse(String id, CourseDTO courseDTO) {
        logger.debug("Fetching course with ID: {} for update", id);
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Course not found with id: {}", id);
                    return new RuntimeException("Course not found with id: " + id);
                });
        
        logger.debug("Updating course fields for ID: {}", id);
        existingCourse.setTitle(courseDTO.getTitle());
        existingCourse.setDescription(courseDTO.getDescription());
        existingCourse.setInstructor(courseDTO.getInstructor());
        existingCourse.setPrice(courseDTO.getPrice());
        
        logger.debug("Saving updated course");
        Course updatedCourse = courseRepository.save(existingCourse);
        logger.debug("Course updated successfully");
        return convertToDTO(updatedCourse);
    }

    @Override
    public CourseDTO getCourseById(String id) {
        logger.debug("Fetching course with ID: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Course not found with id: {}", id);
                    return new RuntimeException("Course not found with id: " + id);
                });
        logger.debug("Course found successfully");
        return convertToDTO(course);
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        logger.debug("Fetching all courses");
        List<Course> courses = courseRepository.findAll();
        logger.debug("Found {} courses", courses.size());
        return courses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCourse(String id) {
        logger.debug("Deleting course with ID: {}", id);
        courseRepository.deleteById(id);
        logger.debug("Course deleted successfully");
    }

    private Course convertToEntity(CourseDTO courseDTO) {
        logger.trace("Converting CourseDTO to Course entity");
        Course course = new Course();
        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        course.setInstructor(courseDTO.getInstructor());
        course.setPrice(courseDTO.getPrice());
        return course;
    }

    private CourseDTO convertToDTO(Course course) {
        logger.trace("Converting Course entity to CourseDTO");
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setId(course.getId());
        courseDTO.setTitle(course.getTitle());
        courseDTO.setDescription(course.getDescription());
        courseDTO.setInstructor(course.getInstructor());
        courseDTO.setPrice(course.getPrice());
        courseDTO.setCreatedAt(course.getCreatedAt());
        courseDTO.setUpdatedAt(course.getUpdatedAt());
        return courseDTO;
    }
} 
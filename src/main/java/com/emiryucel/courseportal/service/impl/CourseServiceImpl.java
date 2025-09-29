package com.emiryucel.courseportal.service.impl;

import com.emiryucel.courseportal.dto.CourseDTO;
import com.emiryucel.courseportal.dto.CourseResponseDTO;
import com.emiryucel.courseportal.exception.ResourceNotFoundException;
import com.emiryucel.courseportal.mapper.CourseMapper;
import com.emiryucel.courseportal.model.Course;
import com.emiryucel.courseportal.repository.CourseRepository;
import com.emiryucel.courseportal.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Override
    public CourseResponseDTO createCourse(CourseDTO courseDTO) {
        log.debug("Creating new course with title: {}", courseDTO.getTitle());
        
        Course course = courseMapper.toEntity(courseDTO);
        Course savedCourse = courseRepository.save(course);
        log.debug("Course saved successfully with ID: {}", savedCourse.getId());
        
        return courseMapper.toResponseDto(savedCourse);
    }

    @Override
    public CourseResponseDTO updateCourse(String id, CourseDTO courseDTO) {
        log.debug("Updating course with ID: {}", id);
        
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Course not found with id: {}", id);
                    return new ResourceNotFoundException("Course not found with id: " + id);
                });
        
        courseMapper.updateEntityFromDto(courseDTO, existingCourse);
        
        Course updatedCourse = courseRepository.save(existingCourse);
        log.debug("Course updated successfully with ID: {}", id);
        
        return courseMapper.toResponseDto(updatedCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponseDTO getCourseById(String id) {
        log.debug("Fetching course with ID: {}", id);
        
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Course not found with id: {}", id);
                    return new ResourceNotFoundException("Course not found with id: " + id);
                });
        
        log.debug("Course found successfully with ID: {}", id);
        return courseMapper.toResponseDto(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getAllCourses() {
        log.debug("Fetching all courses");
        
        List<Course> courses = courseRepository.findAll();
        log.debug("Found {} courses", courses.size());
        
        return courseMapper.toResponseDtoList(courses);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponseDTO> getAllCourses(Pageable pageable) {
        log.debug("Fetching courses with pagination - page: {}, size: {}, sort: {}", 
                  pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        
        Page<Course> coursePage = courseRepository.findAll(pageable);
        log.debug("Found {} courses on page {} of {}", 
                  coursePage.getNumberOfElements(), 
                  coursePage.getNumber() + 1, 
                  coursePage.getTotalPages());
        
        return coursePage.map(courseMapper::toResponseDto);
    }

    @Override
    public void deleteCourse(String id) {
        log.debug("Deleting course with ID: {}", id);
        
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Course not found with id: {}", id);
                    return new ResourceNotFoundException("Course not found with id: " + id);
                });
                
        if (course.getLecturer() != null) {
            course.getLecturer().removeCourse(course);
        }
        
        courseRepository.deleteById(id);
        log.debug("Course deleted successfully with ID: {}", id);
    }


} 
package com.emiryucel.courseportal.service;

import com.emiryucel.courseportal.dto.CourseDTO;
import com.emiryucel.courseportal.dto.CourseResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface CourseService {
    CourseResponseDTO createCourse(CourseDTO courseDTO);
    CourseResponseDTO updateCourse(String id, CourseDTO courseDTO);
    CourseResponseDTO getCourseById(String id);
    List<CourseResponseDTO> getAllCourses();
    Page<CourseResponseDTO> getAllCourses(Pageable pageable);
    void deleteCourse(String id);

} 
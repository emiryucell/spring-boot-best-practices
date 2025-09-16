package com.emiryucel.courseportal.service;

import com.emiryucel.courseportal.dto.CourseDTO;
import com.emiryucel.courseportal.dto.CourseResponseDTO;

import java.util.List;


public interface CourseService {
    CourseResponseDTO createCourse(CourseDTO courseDTO);
    CourseResponseDTO updateCourse(String id, CourseDTO courseDTO);
    CourseResponseDTO getCourseById(String id);
    List<CourseResponseDTO> getAllCourses();
    void deleteCourse(String id);

} 
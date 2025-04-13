package com.emiryucel.courseportal.service;

import com.emiryucel.courseportal.dto.CourseDTO;

import java.util.List;

public interface CourseService {
    CourseDTO createCourse(CourseDTO courseDTO);
    CourseDTO updateCourse(String id, CourseDTO courseDTO);
    CourseDTO getCourseById(String id);
    List<CourseDTO> getAllCourses();
    void deleteCourse(String id);
} 
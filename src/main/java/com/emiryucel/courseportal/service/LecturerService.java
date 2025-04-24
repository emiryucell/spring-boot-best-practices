package com.emiryucel.courseportal.service;

import com.emiryucel.courseportal.dto.LecturerDTO;
import com.emiryucel.courseportal.model.Course;
import com.emiryucel.courseportal.model.Lecturer;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public interface LecturerService {
    LecturerDTO createLecturer(LecturerDTO lecturerDTO);
    LecturerDTO updateLecturer(String id, LecturerDTO lecturerDTO);
    LecturerDTO getLecturerById(String id);
    List<LecturerDTO> getAllLecturers();
    void deleteLecturer(String id);
    LecturerDTO assignCourse(String lecturerId, String courseId);
    LecturerDTO removeCourse(String lecturerId, String courseId);
    Lecturer convertToEntity(LecturerDTO lecturerDTO);
    LecturerDTO convertToDTO(Lecturer lecturer);
} 
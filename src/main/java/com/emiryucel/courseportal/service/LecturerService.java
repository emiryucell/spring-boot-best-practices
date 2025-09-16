package com.emiryucel.courseportal.service;

import com.emiryucel.courseportal.dto.LecturerDTO;
import com.emiryucel.courseportal.dto.LecturerResponseDTO;

import java.util.List;


public interface LecturerService {
    LecturerResponseDTO createLecturer(LecturerDTO lecturerDTO);
    LecturerResponseDTO updateLecturer(String id, LecturerDTO lecturerDTO);
    LecturerResponseDTO getLecturerById(String id);
    List<LecturerResponseDTO> getAllLecturers();
    void deleteLecturer(String id);
    LecturerResponseDTO assignCourse(String lecturerId, String courseId);
    LecturerResponseDTO removeCourse(String lecturerId, String courseId);
} 
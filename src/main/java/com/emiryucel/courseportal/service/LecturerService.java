package com.emiryucel.courseportal.service;

import com.emiryucel.courseportal.dto.LecturerDTO;
import java.util.List;

public interface LecturerService {
    LecturerDTO createLecturer(LecturerDTO lecturerDTO);
    LecturerDTO updateLecturer(String id, LecturerDTO lecturerDTO);
    LecturerDTO getLecturerById(String id);
    List<LecturerDTO> getAllLecturers();
    void deleteLecturer(String id);
    LecturerDTO assignCourse(String lecturerId, String courseId);
    LecturerDTO removeCourse(String lecturerId, String courseId);
} 
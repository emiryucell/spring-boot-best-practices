package com.emiryucel.courseportal.service.impl;

import com.emiryucel.courseportal.dto.LecturerDTO;
import com.emiryucel.courseportal.dto.LecturerResponseDTO;
import com.emiryucel.courseportal.exception.DuplicateResourceException;
import com.emiryucel.courseportal.exception.ResourceNotFoundException;
import com.emiryucel.courseportal.mapper.LecturerMapper;
import com.emiryucel.courseportal.model.Course;
import com.emiryucel.courseportal.model.Lecturer;
import com.emiryucel.courseportal.repository.CourseRepository;
import com.emiryucel.courseportal.repository.LecturerRepository;
import com.emiryucel.courseportal.service.LecturerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LecturerServiceImpl implements LecturerService {

    private final LecturerRepository lecturerRepository;
    private final CourseRepository courseRepository;
    private final LecturerMapper lecturerMapper;

    @Override
    public LecturerResponseDTO createLecturer(LecturerDTO lecturerDTO) {
        log.debug("Creating new lecturer: {} {}", lecturerDTO.getFirstName(), lecturerDTO.getLastName());
        
        if (lecturerRepository.findByEmail(lecturerDTO.getEmail()).isPresent()) {
            log.error("Email already exists: {}", lecturerDTO.getEmail());
            throw new DuplicateResourceException("Email already exists: " + lecturerDTO.getEmail());
        }
        
        Lecturer lecturer = lecturerMapper.toEntity(lecturerDTO);
        Lecturer savedLecturer = lecturerRepository.save(lecturer);
        log.debug("Lecturer saved successfully with ID: {}", savedLecturer.getId());
        
        return lecturerMapper.toResponseDto(savedLecturer);
    }

    @Override
    public LecturerResponseDTO updateLecturer(String id, LecturerDTO lecturerDTO) {
        log.debug("Updating lecturer with ID: {}", id);
        
        Lecturer existingLecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Lecturer not found with id: {}", id);
                    return new ResourceNotFoundException("Lecturer not found with id: " + id);
                });
        
        if (!existingLecturer.getEmail().equals(lecturerDTO.getEmail())) {
            if (lecturerRepository.findByEmail(lecturerDTO.getEmail()).isPresent()) {
                log.error("Email already exists: {}", lecturerDTO.getEmail());
                throw new DuplicateResourceException("Email already exists: " + lecturerDTO.getEmail());
            }
        }
        
        lecturerMapper.updateEntityFromDto(lecturerDTO, existingLecturer);
        
        Lecturer updatedLecturer = lecturerRepository.save(existingLecturer);
        log.debug("Lecturer updated successfully with ID: {}", id);
        
        return lecturerMapper.toResponseDto(updatedLecturer);
    }

    @Override
    @Transactional(readOnly = true)
    public LecturerResponseDTO getLecturerById(String id) {
        log.debug("Fetching lecturer with ID: {}", id);
        
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Lecturer not found with id: {}", id);
                    return new ResourceNotFoundException("Lecturer not found with id: " + id);
                });
        
        log.debug("Lecturer found successfully with ID: {}", id);
        return lecturerMapper.toResponseDto(lecturer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LecturerResponseDTO> getAllLecturers() {
        log.debug("Fetching all lecturers");
        
        List<Lecturer> lecturers = lecturerRepository.findAll();
        log.debug("Found {} lecturers", lecturers.size());
        
        return lecturerMapper.toResponseDtoList(lecturers);
    }

    @Override
    public void deleteLecturer(String id) {
        log.debug("Deleting lecturer with ID: {}", id);
        
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Lecturer not found with id: {}", id);
                    return new ResourceNotFoundException("Lecturer not found with id: " + id);
                });
        
        lecturer.getCourses().forEach(course -> course.setLecturer(null));
        
        lecturerRepository.deleteById(id);
        log.debug("Lecturer deleted successfully with ID: {}", id);
    }

    @Override
    public LecturerResponseDTO assignCourse(String lecturerId, String courseId) {
        log.debug("Assigning course {} to lecturer {}", courseId, lecturerId);
        
        Lecturer lecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> {
                    log.error("Lecturer not found with id: {}", lecturerId);
                    return new ResourceNotFoundException("Lecturer not found with id: " + lecturerId);
                });
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.error("Course not found with id: {}", courseId);
                    return new ResourceNotFoundException("Course not found with id: " + courseId);
                });
        
        lecturer.addCourse(course);
        
        Lecturer updatedLecturer = lecturerRepository.save(lecturer);
        log.debug("Course assigned successfully to lecturer {}", lecturerId);
        
        return lecturerMapper.toResponseDto(updatedLecturer);
    }

    @Override
    public LecturerResponseDTO removeCourse(String lecturerId, String courseId) {
        log.debug("Removing course {} from lecturer {}", courseId, lecturerId);
        
        Lecturer lecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> {
                    log.error("Lecturer not found with id: {}", lecturerId);
                    return new ResourceNotFoundException("Lecturer not found with id: " + lecturerId);
                });
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.error("Course not found with id: {}", courseId);
                    return new ResourceNotFoundException("Course not found with id: " + courseId);
                });
        
        lecturer.removeCourse(course);
        
        Lecturer updatedLecturer = lecturerRepository.save(lecturer);
        log.debug("Course removed successfully from lecturer {}", lecturerId);
        
        return lecturerMapper.toResponseDto(updatedLecturer);
    }
} 
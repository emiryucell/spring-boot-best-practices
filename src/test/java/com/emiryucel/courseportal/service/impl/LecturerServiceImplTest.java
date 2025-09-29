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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Lecturer Service Implementation Tests")
class LecturerServiceImplTest {

    @Mock
    private LecturerRepository lecturerRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LecturerMapper lecturerMapper;

    @InjectMocks
    private LecturerServiceImpl lecturerService;

    private LecturerDTO lecturerDTO;
    private Lecturer lecturer;
    private LecturerResponseDTO lecturerResponseDTO;
    private Course course;

    @BeforeEach
    void setUp() {
        lecturerDTO = new LecturerDTO();
        lecturerDTO.setFirstName("John");
        lecturerDTO.setLastName("Doe");
        lecturerDTO.setEmail("john.doe@university.edu");
        lecturerDTO.setDepartment("Computer Science");
        lecturerDTO.setBio("Experienced professor with 10 years of teaching experience");

        lecturer = new Lecturer();
        lecturer.setId("lecturer-123");
        lecturer.setFirstName("John");
        lecturer.setLastName("Doe");
        lecturer.setEmail("john.doe@university.edu");
        lecturer.setDepartment("Computer Science");
        lecturer.setBio("Experienced professor with 10 years of teaching experience");
        lecturer.setCourses(new HashSet<>());
        lecturer.setCreatedAt(LocalDateTime.now());
        lecturer.setUpdatedAt(LocalDateTime.now());

        lecturerResponseDTO = new LecturerResponseDTO();
        lecturerResponseDTO.setId("lecturer-123");
        lecturerResponseDTO.setFirstName("John");
        lecturerResponseDTO.setLastName("Doe");
        lecturerResponseDTO.setEmail("john.doe@university.edu");
        lecturerResponseDTO.setDepartment("Computer Science");
        lecturerResponseDTO.setBio("Experienced professor with 10 years of teaching experience");

        course = new Course();
        course.setId("course-123");
        course.setTitle("Java Programming");
        course.setDescription("Complete Java programming course");
        course.setPrice(99.99);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create lecturer successfully")
    void givenValidLecturerDTO_whenCreateLecturer_thenReturnLecturerResponseDTO() {
        when(lecturerRepository.findByEmail(lecturerDTO.getEmail())).thenReturn(Optional.empty());
        when(lecturerMapper.toEntity(lecturerDTO)).thenReturn(lecturer);
        when(lecturerRepository.save(lecturer)).thenReturn(lecturer);
        when(lecturerMapper.toResponseDto(lecturer)).thenReturn(lecturerResponseDTO);

        LecturerResponseDTO result = lecturerService.createLecturer(lecturerDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("lecturer-123");
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@university.edu");
        assertThat(result.getDepartment()).isEqualTo("Computer Science");
        assertThat(result.getBio()).isEqualTo("Experienced professor with 10 years of teaching experience");

        verify(lecturerRepository, times(1)).findByEmail(lecturerDTO.getEmail());
        verify(lecturerMapper, times(1)).toEntity(lecturerDTO);
        verify(lecturerRepository, times(1)).save(lecturer);
        verify(lecturerMapper, times(1)).toResponseDto(lecturer);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when creating lecturer with existing email")
    void givenExistingEmail_whenCreateLecturer_thenThrowDuplicateResourceException() {
        when(lecturerRepository.findByEmail(lecturerDTO.getEmail())).thenReturn(Optional.of(lecturer));

        assertThatThrownBy(() -> lecturerService.createLecturer(lecturerDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already exists: " + lecturerDTO.getEmail());

        verify(lecturerRepository, times(1)).findByEmail(lecturerDTO.getEmail());
        verify(lecturerMapper, never()).toEntity(any());
        verify(lecturerRepository, never()).save(any());
        verify(lecturerMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("Should update lecturer successfully with same email")
    void givenValidLecturerIdAndDTO_whenUpdateLecturer_thenReturnUpdatedLecturer() {
        String lecturerId = "lecturer-123";
        Lecturer existingLecturer = new Lecturer();
        existingLecturer.setId(lecturerId);
        existingLecturer.setEmail("john.doe@university.edu");
        existingLecturer.setFirstName("Old Name");
        existingLecturer.setLastName("Old Last");
        existingLecturer.setDepartment("Old Department");

        when(lecturerRepository.findById(lecturerId)).thenReturn(Optional.of(existingLecturer));
        doNothing().when(lecturerMapper).updateEntityFromDto(lecturerDTO, existingLecturer);
        when(lecturerRepository.save(existingLecturer)).thenReturn(lecturer);
        when(lecturerMapper.toResponseDto(lecturer)).thenReturn(lecturerResponseDTO);

        LecturerResponseDTO result = lecturerService.updateLecturer(lecturerId, lecturerDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("lecturer-123");
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@university.edu");
        assertThat(result.getDepartment()).isEqualTo("Computer Science");

        verify(lecturerRepository, times(1)).findById(lecturerId);

        verify(lecturerRepository, never()).findByEmail(lecturerDTO.getEmail());
        verify(lecturerMapper, times(1)).updateEntityFromDto(lecturerDTO, existingLecturer);
        verify(lecturerRepository, times(1)).save(existingLecturer);
        verify(lecturerMapper, times(1)).toResponseDto(lecturer);
    }

    @Test
    @DisplayName("Should update lecturer with different email successfully")
    void givenDifferentEmail_whenUpdateLecturer_thenReturnUpdatedLecturer() {
        String lecturerId = "lecturer-123";
        String newEmail = "jane.doe@university.edu";
        
        LecturerDTO updateDTO = new LecturerDTO();
        updateDTO.setFirstName("Jane");
        updateDTO.setLastName("Doe");
        updateDTO.setEmail(newEmail);
        updateDTO.setDepartment("Mathematics");
        updateDTO.setBio("Updated bio");

        Lecturer existingLecturer = new Lecturer();
        existingLecturer.setId(lecturerId);
        existingLecturer.setEmail("john.doe@university.edu");
        existingLecturer.setFirstName("John");

        when(lecturerRepository.findById(lecturerId)).thenReturn(Optional.of(existingLecturer));
        when(lecturerRepository.findByEmail(newEmail)).thenReturn(Optional.empty());
        doNothing().when(lecturerMapper).updateEntityFromDto(updateDTO, existingLecturer);
        when(lecturerRepository.save(existingLecturer)).thenReturn(lecturer);
        when(lecturerMapper.toResponseDto(lecturer)).thenReturn(lecturerResponseDTO);

        LecturerResponseDTO result = lecturerService.updateLecturer(lecturerId, updateDTO);

        assertThat(result).isNotNull();
        verify(lecturerRepository, times(1)).findById(lecturerId);
        verify(lecturerRepository, times(1)).findByEmail(newEmail);
        verify(lecturerMapper, times(1)).updateEntityFromDto(updateDTO, existingLecturer);
        verify(lecturerRepository, times(1)).save(existingLecturer);
        verify(lecturerMapper, times(1)).toResponseDto(lecturer);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent lecturer")
    void givenNonExistentLecturerId_whenUpdateLecturer_thenThrowResourceNotFoundException() {
        String lecturerId = "non-existent-lecturer";
        when(lecturerRepository.findById(lecturerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lecturerService.updateLecturer(lecturerId, lecturerDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Lecturer not found with id: " + lecturerId);

        verify(lecturerRepository, times(1)).findById(lecturerId);
        verify(lecturerRepository, never()).findByEmail(any());
        verify(lecturerMapper, never()).updateEntityFromDto(any(), any());
        verify(lecturerRepository, never()).save(any());
        verify(lecturerMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when updating lecturer with existing email")
    void givenExistingEmail_whenUpdateLecturer_thenThrowDuplicateResourceException() {
        String lecturerId = "lecturer-123";
        String newEmail = "existing@university.edu";
        
        LecturerDTO updateDTO = new LecturerDTO();
        updateDTO.setEmail(newEmail);

        Lecturer existingLecturer = new Lecturer();
        existingLecturer.setId(lecturerId);
        existingLecturer.setEmail("john.doe@university.edu");

        Lecturer anotherLecturer = new Lecturer();
        anotherLecturer.setId("another-lecturer");
        anotherLecturer.setEmail(newEmail);

        when(lecturerRepository.findById(lecturerId)).thenReturn(Optional.of(existingLecturer));
        when(lecturerRepository.findByEmail(newEmail)).thenReturn(Optional.of(anotherLecturer));

        assertThatThrownBy(() -> lecturerService.updateLecturer(lecturerId, updateDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already exists: " + newEmail);

        verify(lecturerRepository, times(1)).findById(lecturerId);
        verify(lecturerRepository, times(1)).findByEmail(newEmail);
        verify(lecturerMapper, never()).updateEntityFromDto(any(), any());
        verify(lecturerRepository, never()).save(any());
        verify(lecturerMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("Should get lecturer by id successfully")
    void givenValidLecturerId_whenGetLecturerById_thenReturnLecturerResponseDTO() {
        String lecturerId = "lecturer-123";
        when(lecturerRepository.findById(lecturerId)).thenReturn(Optional.of(lecturer));
        when(lecturerMapper.toResponseDto(lecturer)).thenReturn(lecturerResponseDTO);

        LecturerResponseDTO result = lecturerService.getLecturerById(lecturerId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("lecturer-123");
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@university.edu");
        assertThat(result.getDepartment()).isEqualTo("Computer Science");

        verify(lecturerRepository, times(1)).findById(lecturerId);
        verify(lecturerMapper, times(1)).toResponseDto(lecturer);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when getting non-existent lecturer")
    void givenNonExistentLecturerId_whenGetLecturerById_thenThrowResourceNotFoundException() {
        String lecturerId = "non-existent-lecturer";
        when(lecturerRepository.findById(lecturerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lecturerService.getLecturerById(lecturerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Lecturer not found with id: " + lecturerId);

        verify(lecturerRepository, times(1)).findById(lecturerId);
        verify(lecturerMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("Should get all lecturers successfully")
    void givenLecturersExist_whenGetAllLecturers_thenReturnListOfLecturers() {
        Lecturer lecturer2 = new Lecturer();
        lecturer2.setId("lecturer-456");
        lecturer2.setFirstName("Alice");
        lecturer2.setLastName("Johnson");
        lecturer2.setEmail("alice.johnson@university.edu");
        lecturer2.setDepartment("Physics");

        LecturerResponseDTO lecturerResponseDTO2 = new LecturerResponseDTO();
        lecturerResponseDTO2.setId("lecturer-456");
        lecturerResponseDTO2.setFirstName("Alice");
        lecturerResponseDTO2.setLastName("Johnson");
        lecturerResponseDTO2.setEmail("alice.johnson@university.edu");
        lecturerResponseDTO2.setDepartment("Physics");

        List<Lecturer> lecturers = Arrays.asList(lecturer, lecturer2);
        List<LecturerResponseDTO> lecturerResponseDTOs = Arrays.asList(lecturerResponseDTO, lecturerResponseDTO2);

        when(lecturerRepository.findAll()).thenReturn(lecturers);
        when(lecturerMapper.toResponseDtoList(lecturers)).thenReturn(lecturerResponseDTOs);

        List<LecturerResponseDTO> result = lecturerService.getAllLecturers();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("lecturer-123");
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
        assertThat(result.get(0).getLastName()).isEqualTo("Doe");
        assertThat(result.get(1).getId()).isEqualTo("lecturer-456");
        assertThat(result.get(1).getFirstName()).isEqualTo("Alice");
        assertThat(result.get(1).getLastName()).isEqualTo("Johnson");

        verify(lecturerRepository, times(1)).findAll();
        verify(lecturerMapper, times(1)).toResponseDtoList(lecturers);
    }

    @Test
    @DisplayName("Should delete lecturer successfully")
    void givenValidLecturerId_whenDeleteLecturer_thenLecturerIsDeleted() {
        String lecturerId = "lecturer-123";
        Set<Course> courses = new HashSet<>();
        courses.add(course);
        lecturer.setCourses(courses);

        when(lecturerRepository.findById(lecturerId)).thenReturn(Optional.of(lecturer));
        doNothing().when(lecturerRepository).deleteById(lecturerId);

        lecturerService.deleteLecturer(lecturerId);

        verify(lecturerRepository, times(1)).findById(lecturerId);
        verify(lecturerRepository, times(1)).deleteById(lecturerId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent lecturer")
    void givenNonExistentLecturerId_whenDeleteLecturer_thenThrowResourceNotFoundException() {
        String lecturerId = "non-existent-lecturer";
        when(lecturerRepository.findById(lecturerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lecturerService.deleteLecturer(lecturerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Lecturer not found with id: " + lecturerId);

        verify(lecturerRepository, times(1)).findById(lecturerId);
        verify(lecturerRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should assign course to lecturer successfully")
    void givenValidIds_whenAssignCourse_thenReturnUpdatedLecturer() {
        String lecturerId = "lecturer-123";
        String courseId = "course-123";

        when(lecturerRepository.findById(lecturerId)).thenReturn(Optional.of(lecturer));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(lecturerRepository.save(lecturer)).thenReturn(lecturer);
        when(lecturerMapper.toResponseDto(lecturer)).thenReturn(lecturerResponseDTO);

        LecturerResponseDTO result = lecturerService.assignCourse(lecturerId, courseId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("lecturer-123");
        assertThat(lecturer.getCourses()).contains(course);
        assertThat(course.getLecturer()).isEqualTo(lecturer);

        verify(lecturerRepository, times(1)).findById(lecturerId);
        verify(courseRepository, times(1)).findById(courseId);
        verify(lecturerRepository, times(1)).save(lecturer);
        verify(lecturerMapper, times(1)).toResponseDto(lecturer);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when assigning course to non-existent lecturer")
    void givenNonExistentLecturerId_whenAssignCourse_thenThrowResourceNotFoundException() {
        String lecturerId = "non-existent-lecturer";
        String courseId = "course-123";

        when(lecturerRepository.findById(lecturerId)).thenReturn(Optional.empty());

 
        assertThatThrownBy(() -> lecturerService.assignCourse(lecturerId, courseId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Lecturer not found with id: " + lecturerId);

        verify(lecturerRepository, times(1)).findById(lecturerId);
        verify(courseRepository, never()).findById(any());
        verify(lecturerRepository, never()).save(any());
        verify(lecturerMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when assigning non-existent course to lecturer")
    void givenNonExistentCourseId_whenAssignCourse_thenThrowResourceNotFoundException() {
        String lecturerId = "lecturer-123";
        String courseId = "non-existent-course";

        when(lecturerRepository.findById(lecturerId)).thenReturn(Optional.of(lecturer));
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

 
        assertThatThrownBy(() -> lecturerService.assignCourse(lecturerId, courseId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Course not found with id: " + courseId);

        verify(lecturerRepository, times(1)).findById(lecturerId);
        verify(courseRepository, times(1)).findById(courseId);
        verify(lecturerRepository, never()).save(any());
        verify(lecturerMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("Should remove course from lecturer successfully")
    void givenValidIds_whenRemoveCourse_thenReturnUpdatedLecturer() {
        String lecturerId = "lecturer-123";
        String courseId = "course-123";

        when(lecturerRepository.findById(lecturerId)).thenReturn(Optional.of(lecturer));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(lecturerRepository.save(lecturer)).thenReturn(lecturer);
        when(lecturerMapper.toResponseDto(lecturer)).thenReturn(lecturerResponseDTO);

        LecturerResponseDTO result = lecturerService.removeCourse(lecturerId, courseId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("lecturer-123");

        verify(lecturerRepository, times(1)).findById(lecturerId);
        verify(courseRepository, times(1)).findById(courseId);
        verify(lecturerRepository, times(1)).save(lecturer);
        verify(lecturerMapper, times(1)).toResponseDto(lecturer);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when removing course from non-existent lecturer")
    void givenNonExistentLecturerId_whenRemoveCourse_thenThrowResourceNotFoundException() {
        String lecturerId = "non-existent-lecturer";
        String courseId = "course-123";

        when(lecturerRepository.findById(lecturerId)).thenReturn(Optional.empty());

 
        assertThatThrownBy(() -> lecturerService.removeCourse(lecturerId, courseId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Lecturer not found with id: " + lecturerId);

        verify(lecturerRepository, times(1)).findById(lecturerId);
        verify(courseRepository, never()).findById(any());
        verify(lecturerRepository, never()).save(any());
        verify(lecturerMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when removing non-existent course from lecturer")
    void givenNonExistentCourseId_whenRemoveCourse_thenThrowResourceNotFoundException() {
        String lecturerId = "lecturer-123";
        String courseId = "non-existent-course";

        when(lecturerRepository.findById(lecturerId)).thenReturn(Optional.of(lecturer));
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

 
        assertThatThrownBy(() -> lecturerService.removeCourse(lecturerId, courseId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Course not found with id: " + courseId);

        verify(lecturerRepository, times(1)).findById(lecturerId);
        verify(courseRepository, times(1)).findById(courseId);
        verify(lecturerRepository, never()).save(any());
        verify(lecturerMapper, never()).toResponseDto(any());
    }


}

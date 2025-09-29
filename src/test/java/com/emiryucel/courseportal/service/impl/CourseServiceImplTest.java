package com.emiryucel.courseportal.service.impl;

import com.emiryucel.courseportal.dto.CourseDTO;
import com.emiryucel.courseportal.dto.CourseResponseDTO;
import com.emiryucel.courseportal.exception.ResourceNotFoundException;
import com.emiryucel.courseportal.mapper.CourseMapper;
import com.emiryucel.courseportal.model.Course;
import com.emiryucel.courseportal.model.Lecturer;
import com.emiryucel.courseportal.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Course Service Implementation Tests")
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseServiceImpl courseService;

    private CourseDTO courseDTO;
    private Course course;
    private CourseResponseDTO courseResponseDTO;
    private Lecturer lecturer;

    @BeforeEach
    void setUp() {
        courseDTO = new CourseDTO();
        courseDTO.setTitle("Java Programming");
        courseDTO.setDescription("Complete Java programming course for beginners");
        courseDTO.setPrice(99.99);

        course = new Course();
        course.setId("course-123");
        course.setTitle("Java Programming");
        course.setDescription("Complete Java programming course for beginners");
        course.setPrice(99.99);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());

        courseResponseDTO = new CourseResponseDTO();
        courseResponseDTO.setId("course-123");
        courseResponseDTO.setTitle("Java Programming");
        courseResponseDTO.setDescription("Complete Java programming course for beginners");
        courseResponseDTO.setPrice(99.99);

        lecturer = new Lecturer();
        lecturer.setId("lecturer-123");
        lecturer.setFirstName("John");
        lecturer.setLastName("Doe");
        lecturer.setEmail("john.doe@university.edu");
        lecturer.setDepartment("Computer Science");
    }

    @Test
    @DisplayName("Should create course successfully")
    void givenValidCourseDTO_whenCreateCourse_thenReturnCourseResponseDTO() {
        when(courseMapper.toEntity(courseDTO)).thenReturn(course);
        when(courseRepository.save(course)).thenReturn(course);
        when(courseMapper.toResponseDto(course)).thenReturn(courseResponseDTO);

        CourseResponseDTO result = courseService.createCourse(courseDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("course-123");
        assertThat(result.getTitle()).isEqualTo("Java Programming");
        assertThat(result.getDescription()).isEqualTo("Complete Java programming course for beginners");
        assertThat(result.getPrice()).isEqualTo(99.99);

        verify(courseMapper, times(1)).toEntity(courseDTO);
        verify(courseRepository, times(1)).save(course);
        verify(courseMapper, times(1)).toResponseDto(course);
    }

    @Test
    @DisplayName("Should update course successfully")
    void givenValidCourseIdAndDTO_whenUpdateCourse_thenReturnUpdatedCourse() {
        String courseId = "course-123";
        Course existingCourse = new Course();
        existingCourse.setId(courseId);
        existingCourse.setTitle("Old Title");
        existingCourse.setDescription("Old Description");
        existingCourse.setPrice(79.99);

        Course updatedCourse = new Course();
        updatedCourse.setId(courseId);
        updatedCourse.setTitle("Java Programming");
        updatedCourse.setDescription("Complete Java programming course for beginners");
        updatedCourse.setPrice(99.99);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(existingCourse));
        doNothing().when(courseMapper).updateEntityFromDto(courseDTO, existingCourse);
        when(courseRepository.save(existingCourse)).thenReturn(updatedCourse);
        when(courseMapper.toResponseDto(updatedCourse)).thenReturn(courseResponseDTO);

        CourseResponseDTO result = courseService.updateCourse(courseId, courseDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(courseId);
        assertThat(result.getTitle()).isEqualTo("Java Programming");
        assertThat(result.getDescription()).isEqualTo("Complete Java programming course for beginners");
        assertThat(result.getPrice()).isEqualTo(99.99);

        verify(courseRepository, times(1)).findById(courseId);
        verify(courseMapper, times(1)).updateEntityFromDto(courseDTO, existingCourse);
        verify(courseRepository, times(1)).save(existingCourse);
        verify(courseMapper, times(1)).toResponseDto(updatedCourse);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent course")
    void givenNonExistentCourseId_whenUpdateCourse_thenThrowResourceNotFoundException() {
        String courseId = "non-existent-course";
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> courseService.updateCourse(courseId, courseDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Course not found with id: " + courseId);

        verify(courseRepository, times(1)).findById(courseId);
        verify(courseMapper, never()).updateEntityFromDto(any(), any());
        verify(courseRepository, never()).save(any());
        verify(courseMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("Should get course by id successfully")
    void givenValidCourseId_whenGetCourseById_thenReturnCourseResponseDTO() {
        String courseId = "course-123";
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseMapper.toResponseDto(course)).thenReturn(courseResponseDTO);

        CourseResponseDTO result = courseService.getCourseById(courseId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("course-123");
        assertThat(result.getTitle()).isEqualTo("Java Programming");
        assertThat(result.getDescription()).isEqualTo("Complete Java programming course for beginners");
        assertThat(result.getPrice()).isEqualTo(99.99);

        verify(courseRepository, times(1)).findById(courseId);
        verify(courseMapper, times(1)).toResponseDto(course);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when getting non-existent course")
    void givenNonExistentCourseId_whenGetCourseById_thenThrowResourceNotFoundException() {
        String courseId = "non-existent-course";
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.getCourseById(courseId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Course not found with id: " + courseId);

        verify(courseRepository, times(1)).findById(courseId);
        verify(courseMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("Should get all courses successfully")
    void givenCoursesExist_whenGetAllCourses_thenReturnListOfCourses() {
        Course course2 = new Course();
        course2.setId("course-456");
        course2.setTitle("Python Programming");
        course2.setDescription("Complete Python programming course");
        course2.setPrice(89.99);

        CourseResponseDTO courseResponseDTO2 = new CourseResponseDTO();
        courseResponseDTO2.setId("course-456");
        courseResponseDTO2.setTitle("Python Programming");
        courseResponseDTO2.setDescription("Complete Python programming course");
        courseResponseDTO2.setPrice(89.99);

        List<Course> courses = Arrays.asList(course, course2);
        List<CourseResponseDTO> courseResponseDTOs = Arrays.asList(courseResponseDTO, courseResponseDTO2);

        when(courseRepository.findAll()).thenReturn(courses);
        when(courseMapper.toResponseDtoList(courses)).thenReturn(courseResponseDTOs);

        List<CourseResponseDTO> result = courseService.getAllCourses();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("course-123");
        assertThat(result.get(0).getTitle()).isEqualTo("Java Programming");
        assertThat(result.get(1).getId()).isEqualTo("course-456");
        assertThat(result.get(1).getTitle()).isEqualTo("Python Programming");

        verify(courseRepository, times(1)).findAll();
        verify(courseMapper, times(1)).toResponseDtoList(courses);
    }

    @Test
    @DisplayName("Should get paginated courses successfully")
    void givenPageableRequest_whenGetAllCourses_thenReturnPageOfCourses() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Course> courses = Arrays.asList(course);
        Page<Course> coursePage = new PageImpl<>(courses, pageable, 1);

        when(courseRepository.findAll(pageable)).thenReturn(coursePage);
        when(courseMapper.toResponseDto(course)).thenReturn(courseResponseDTO);

        Page<CourseResponseDTO> result = courseService.getAllCourses(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getContent().get(0).getId()).isEqualTo("course-123");
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Java Programming");

        verify(courseRepository, times(1)).findAll(pageable);
        verify(courseMapper, times(1)).toResponseDto(course);
    }

    @Test
    @DisplayName("Should delete course successfully")
    void givenValidCourseId_whenDeleteCourse_thenCourseIsDeleted() {
        String courseId = "course-123";
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        doNothing().when(courseRepository).deleteById(courseId);

        courseService.deleteCourse(courseId);

        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, times(1)).deleteById(courseId);
    }

    @Test
    @DisplayName("Should delete course with lecturer successfully")
    void givenCourseWithLecturer_whenDeleteCourse_thenCourseAndLecturerRelationshipRemoved() {
        String courseId = "course-123";
        course.setLecturer(lecturer);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        doNothing().when(courseRepository).deleteById(courseId);

        courseService.deleteCourse(courseId);

        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, times(1)).deleteById(courseId);
    }


    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent course")
    void givenNonExistentCourseId_whenDeleteCourse_thenThrowResourceNotFoundException() {
        String courseId = "non-existent-course";
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.deleteCourse(courseId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Course not found with id: " + courseId);

        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, never()).deleteById(any());
    }


}

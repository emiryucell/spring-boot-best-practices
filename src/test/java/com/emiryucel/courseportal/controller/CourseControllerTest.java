package com.emiryucel.courseportal.controller;

import com.emiryucel.courseportal.dto.CourseDTO;
import com.emiryucel.courseportal.dto.CourseResponseDTO;
import com.emiryucel.courseportal.exception.ResourceNotFoundException;
import com.emiryucel.courseportal.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
@DisplayName("Course Controller Tests")
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseService courseService;

    @Autowired
    private ObjectMapper objectMapper;

    private CourseDTO courseDTO;
    private CourseResponseDTO courseResponseDTO;

    @BeforeEach
    void setUp() {
        courseDTO = new CourseDTO();
        courseDTO.setTitle("Java Programming");
        courseDTO.setDescription("Complete Java programming course for beginners");
        courseDTO.setPrice(99.99);

        courseResponseDTO = new CourseResponseDTO();
        courseResponseDTO.setId("course-123");
        courseResponseDTO.setTitle("Java Programming");
        courseResponseDTO.setDescription("Complete Java programming course for beginners");
        courseResponseDTO.setPrice(99.99);
    }

    @Test
    @DisplayName("Should create course successfully")
    void givenValidCourseDTO_whenCreateCourse_thenReturnCreatedCourse() throws Exception {
        when(courseService.createCourse(any(CourseDTO.class))).thenReturn(courseResponseDTO);
        mockMvc.perform(post("/course")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("course-123"))
                .andExpect(jsonPath("$.title").value("Java Programming"))
                .andExpect(jsonPath("$.description").value("Complete Java programming course for beginners"))
                .andExpect(jsonPath("$.price").value(99.99));

        verify(courseService, times(1)).createCourse(any(CourseDTO.class));
    }

    @Test
    @DisplayName("Should return validation error when creating course with invalid data")
    void givenInvalidCourseDTO_whenCreateCourse_thenReturnValidationError() throws Exception {
        CourseDTO invalidCourseDTO = new CourseDTO();
        invalidCourseDTO.setTitle("");
        invalidCourseDTO.setDescription("Short");
        invalidCourseDTO.setPrice(-10.0);

        mockMvc.perform(post("/course")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCourseDTO)))
                .andExpect(status().isBadRequest());

        verify(courseService, never()).createCourse(any(CourseDTO.class));
    }

    @Test
    @DisplayName("Should update course successfully")
    void givenValidCourseDTO_whenUpdateCourse_thenReturnUpdatedCourse() throws Exception {
        String courseId = "course-123";
        CourseResponseDTO updatedCourse = new CourseResponseDTO();
        updatedCourse.setId(courseId);
        updatedCourse.setTitle("Advanced Java Programming");
        updatedCourse.setDescription("Advanced Java programming course");
        updatedCourse.setPrice(149.99);

        when(courseService.updateCourse(eq(courseId), any(CourseDTO.class))).thenReturn(updatedCourse);

        CourseDTO updateDTO = new CourseDTO();
        updateDTO.setTitle("Advanced Java Programming");
        updateDTO.setDescription("Advanced Java programming course");
        updateDTO.setPrice(149.99);

        mockMvc.perform(put("/course/{id}", courseId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(courseId))
                .andExpect(jsonPath("$.title").value("Advanced Java Programming"))
                .andExpect(jsonPath("$.description").value("Advanced Java programming course"))
                .andExpect(jsonPath("$.price").value(149.99));

        verify(courseService, times(1)).updateCourse(eq(courseId), any(CourseDTO.class));
    }

    @Test
    @DisplayName("Should return not found when updating non-existent course")
    void givenNonExistentCourseId_whenUpdateCourse_thenReturnNotFound() throws Exception {
        String courseId = "non-existent-course";
        when(courseService.updateCourse(eq(courseId), any(CourseDTO.class)))
                .thenThrow(new ResourceNotFoundException("Course not found with id: " + courseId));

        mockMvc.perform(put("/course/{id}", courseId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDTO)))
                .andExpect(status().isNotFound());

        verify(courseService, times(1)).updateCourse(eq(courseId), any(CourseDTO.class));
    }

    @Test
    @DisplayName("Should get course by id successfully")
    void givenValidCourseId_whenGetCourseById_thenReturnCourse() throws Exception {
        String courseId = "course-123";
        when(courseService.getCourseById(courseId)).thenReturn(courseResponseDTO);

        mockMvc.perform(get("/course/{id}", courseId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("course-123"))
                .andExpect(jsonPath("$.title").value("Java Programming"))
                .andExpect(jsonPath("$.description").value("Complete Java programming course for beginners"))
                .andExpect(jsonPath("$.price").value(99.99));

        verify(courseService, times(1)).getCourseById(courseId);
    }

    @Test
    @DisplayName("Should return not found when getting non-existent course")
    void givenNonExistentCourseId_whenGetCourseById_thenReturnNotFound() throws Exception {
        String courseId = "non-existent-course";
        when(courseService.getCourseById(courseId))
                .thenThrow(new ResourceNotFoundException("Course not found with id: " + courseId));

        mockMvc.perform(get("/course/{id}", courseId))
                .andExpect(status().isNotFound());

        verify(courseService, times(1)).getCourseById(courseId);
    }

    @Test
    @DisplayName("Should get all courses successfully")
    void givenCoursesExist_whenGetAllCourses_thenReturnAllCourses() throws Exception {
        CourseResponseDTO course2 = new CourseResponseDTO();
        course2.setId("course-456");
        course2.setTitle("Python Programming");
        course2.setDescription("Complete Python programming course");
        course2.setPrice(89.99);

        List<CourseResponseDTO> courses = Arrays.asList(courseResponseDTO, course2);
        when(courseService.getAllCourses()).thenReturn(courses);

        mockMvc.perform(get("/course"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("course-123"))
                .andExpect(jsonPath("$[0].title").value("Java Programming"))
                .andExpect(jsonPath("$[1].id").value("course-456"))
                .andExpect(jsonPath("$[1].title").value("Python Programming"));

        verify(courseService, times(1)).getAllCourses();
    }

    @Test
    @DisplayName("Should get paginated courses successfully")
    void givenCoursesExist_whenGetPaginatedCourses_thenReturnPagedCourses() throws Exception {
        List<CourseResponseDTO> courses = Arrays.asList(courseResponseDTO);
        Page<CourseResponseDTO> coursePage = new PageImpl<>(courses, PageRequest.of(0, 10), 1);
        when(courseService.getAllCourses(any(Pageable.class))).thenReturn(coursePage);

        mockMvc.perform(get("/course/paginated")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value("course-123"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));

        verify(courseService, times(1)).getAllCourses(any(Pageable.class));
    }

    @Test
    @DisplayName("Should delete course successfully")
    void givenValidCourseId_whenDeleteCourse_thenCourseDeleted() throws Exception {
        String courseId = "course-123";
        doNothing().when(courseService).deleteCourse(courseId);

        mockMvc.perform(delete("/course/{id}", courseId))
                .andExpect(status().isNoContent());

        verify(courseService, times(1)).deleteCourse(courseId);
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent course")
    void givenNonExistentCourseId_whenDeleteCourse_thenReturnNotFound() throws Exception {
        String courseId = "non-existent-course";
        doThrow(new ResourceNotFoundException("Course not found with id: " + courseId))
                .when(courseService).deleteCourse(courseId);

        mockMvc.perform(delete("/course/{id}", courseId))
                .andExpect(status().isNotFound());

        verify(courseService, times(1)).deleteCourse(courseId);
    }


}

package com.emiryucel.courseportal.controller;

import com.emiryucel.courseportal.dto.LecturerDTO;
import com.emiryucel.courseportal.dto.LecturerResponseDTO;
import com.emiryucel.courseportal.exception.DuplicateResourceException;
import com.emiryucel.courseportal.exception.ResourceNotFoundException;
import com.emiryucel.courseportal.service.LecturerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LecturerController.class)
@DisplayName("Lecturer Controller Tests")
class LecturerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LecturerService lecturerService;

    @Autowired
    private ObjectMapper objectMapper;

    private LecturerDTO lecturerDTO;
    private LecturerResponseDTO lecturerResponseDTO;

    @BeforeEach
    void setUp() {
        lecturerDTO = new LecturerDTO();
        lecturerDTO.setFirstName("John");
        lecturerDTO.setLastName("Doe");
        lecturerDTO.setEmail("john.doe@university.edu");
        lecturerDTO.setDepartment("Computer Science");
        lecturerDTO.setBio("Experienced professor with 10 years of teaching experience");

        lecturerResponseDTO = new LecturerResponseDTO();
        lecturerResponseDTO.setFirstName("John");
        lecturerResponseDTO.setLastName("Doe");
        lecturerResponseDTO.setEmail("john.doe@university.edu");
        lecturerResponseDTO.setDepartment("Computer Science");
        lecturerResponseDTO.setBio("Experienced professor with 10 years of teaching experience");
    }

    @Test
    @DisplayName("Should create lecturer successfully")
    void givenValidLecturerDTO_whenCreateLecturer_thenReturnCreatedLecturer() throws Exception {
        when(lecturerService.createLecturer(any(LecturerDTO.class))).thenReturn(lecturerResponseDTO);

        mockMvc.perform(post("/lecturer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lecturerDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@university.edu"))
                .andExpect(jsonPath("$.department").value("Computer Science"))
                .andExpect(jsonPath("$.bio").value("Experienced professor with 10 years of teaching experience"));

        verify(lecturerService, times(1)).createLecturer(any(LecturerDTO.class));
    }

    @Test
    @DisplayName("Should return validation error when creating lecturer with invalid data")
    void givenInvalidLecturerDTO_whenCreateLecturer_thenReturnValidationError() throws Exception {
        LecturerDTO invalidLecturerDTO = new LecturerDTO();
        invalidLecturerDTO.setFirstName("");
        invalidLecturerDTO.setLastName("D");
        invalidLecturerDTO.setEmail("invalid-email");
        invalidLecturerDTO.setDepartment("");

        mockMvc.perform(post("/lecturer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLecturerDTO)))
                .andExpect(status().isBadRequest());

        verify(lecturerService, never()).createLecturer(any(LecturerDTO.class));
    }

    @Test
    @DisplayName("Should return conflict when creating lecturer with duplicate email")
    void givenDuplicateEmail_whenCreateLecturer_thenReturnConflict() throws Exception {
        when(lecturerService.createLecturer(any(LecturerDTO.class)))
                .thenThrow(new DuplicateResourceException("Email already exists: " + lecturerDTO.getEmail()));

        mockMvc.perform(post("/lecturer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lecturerDTO)))
                .andExpect(status().isConflict());

        verify(lecturerService, times(1)).createLecturer(any(LecturerDTO.class));
    }

    @Test
    @DisplayName("Should update lecturer successfully")
    void givenValidLecturerDTO_whenUpdateLecturer_thenReturnUpdatedLecturer() throws Exception {
        String lecturerId = "lecturer-123";
        LecturerResponseDTO updatedLecturer = new LecturerResponseDTO();
        updatedLecturer.setFirstName("Jane");
        updatedLecturer.setLastName("Smith");
        updatedLecturer.setEmail("jane.smith@university.edu");
        updatedLecturer.setDepartment("Mathematics");
        updatedLecturer.setBio("Senior professor with expertise in advanced mathematics");

        when(lecturerService.updateLecturer(eq(lecturerId), any(LecturerDTO.class))).thenReturn(updatedLecturer);

        LecturerDTO updateDTO = new LecturerDTO();
        updateDTO.setFirstName("Jane");
        updateDTO.setLastName("Smith");
        updateDTO.setEmail("jane.smith@university.edu");
        updateDTO.setDepartment("Mathematics");
        updateDTO.setBio("Senior professor with expertise in advanced mathematics");

        mockMvc.perform(put("/lecturer/{id}", lecturerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.email").value("jane.smith@university.edu"))
                .andExpect(jsonPath("$.department").value("Mathematics"))
                .andExpect(jsonPath("$.bio").value("Senior professor with expertise in advanced mathematics"));

        verify(lecturerService, times(1)).updateLecturer(eq(lecturerId), any(LecturerDTO.class));
    }

    @Test
    @DisplayName("Should return not found when updating non-existent lecturer")
    void givenNonExistentLecturerId_whenUpdateLecturer_thenReturnNotFound() throws Exception {
        String lecturerId = "non-existent-lecturer";
        when(lecturerService.updateLecturer(eq(lecturerId), any(LecturerDTO.class)))
                .thenThrow(new ResourceNotFoundException("Lecturer not found with id: " + lecturerId));

        mockMvc.perform(put("/lecturer/{id}", lecturerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lecturerDTO)))
                .andExpect(status().isNotFound());

        verify(lecturerService, times(1)).updateLecturer(eq(lecturerId), any(LecturerDTO.class));
    }

    @Test
    @DisplayName("Should return conflict when updating lecturer with duplicate email")
    void givenDuplicateEmail_whenUpdateLecturer_thenReturnConflict() throws Exception {
        String lecturerId = "lecturer-123";
        when(lecturerService.updateLecturer(eq(lecturerId), any(LecturerDTO.class)))
                .thenThrow(new DuplicateResourceException("Email already exists: " + lecturerDTO.getEmail()));

        mockMvc.perform(put("/lecturer/{id}", lecturerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lecturerDTO)))
                .andExpect(status().isConflict());

        verify(lecturerService, times(1)).updateLecturer(eq(lecturerId), any(LecturerDTO.class));
    }

    @Test
    @DisplayName("Should get lecturer by id successfully")
    void givenValidLecturerId_whenGetLecturerById_thenReturnLecturer() throws Exception {
        String lecturerId = "lecturer-123";
        when(lecturerService.getLecturerById(lecturerId)).thenReturn(lecturerResponseDTO);

        mockMvc.perform(get("/lecturer/{id}", lecturerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@university.edu"))
                .andExpect(jsonPath("$.department").value("Computer Science"))
                .andExpect(jsonPath("$.bio").value("Experienced professor with 10 years of teaching experience"));

        verify(lecturerService, times(1)).getLecturerById(lecturerId);
    }

    @Test
    @DisplayName("Should return not found when getting non-existent lecturer")
    void givenNonExistentLecturerId_whenGetLecturerById_thenReturnNotFound() throws Exception {
        String lecturerId = "non-existent-lecturer";
        when(lecturerService.getLecturerById(lecturerId))
                .thenThrow(new ResourceNotFoundException("Lecturer not found with id: " + lecturerId));

        mockMvc.perform(get("/lecturer/{id}", lecturerId))
                .andExpect(status().isNotFound());

        verify(lecturerService, times(1)).getLecturerById(lecturerId);
    }

    @Test
    @DisplayName("Should get all lecturers successfully")
    void givenLecturersExist_whenGetAllLecturers_thenReturnAllLecturers() throws Exception {
        LecturerResponseDTO lecturer2 = new LecturerResponseDTO();
        lecturer2.setFirstName("Alice");
        lecturer2.setLastName("Johnson");
        lecturer2.setEmail("alice.johnson@university.edu");
        lecturer2.setDepartment("Physics");
        lecturer2.setBio("Physics professor specializing in quantum mechanics");

        List<LecturerResponseDTO> lecturers = Arrays.asList(lecturerResponseDTO, lecturer2);
        when(lecturerService.getAllLecturers()).thenReturn(lecturers);

        mockMvc.perform(get("/lecturer"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].firstName").value("Alice"))
                .andExpect(jsonPath("$[1].lastName").value("Johnson"));

        verify(lecturerService, times(1)).getAllLecturers();
    }

    @Test
    @DisplayName("Should delete lecturer successfully")
    void givenValidLecturerId_whenDeleteLecturer_thenLecturerDeleted() throws Exception {
        String lecturerId = "lecturer-123";
        doNothing().when(lecturerService).deleteLecturer(lecturerId);

        mockMvc.perform(delete("/lecturer/{id}", lecturerId))
                .andExpect(status().isNoContent());

        verify(lecturerService, times(1)).deleteLecturer(lecturerId);
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent lecturer")
    void givenNonExistentLecturerId_whenDeleteLecturer_thenReturnNotFound() throws Exception {
        String lecturerId = "non-existent-lecturer";
        doThrow(new ResourceNotFoundException("Lecturer not found with id: " + lecturerId))
                .when(lecturerService).deleteLecturer(lecturerId);

        mockMvc.perform(delete("/lecturer/{id}", lecturerId))
                .andExpect(status().isNotFound());

        verify(lecturerService, times(1)).deleteLecturer(lecturerId);
    }

    @Test
    @DisplayName("Should assign course to lecturer successfully")
    void givenValidIds_whenAssignCourseToLecturer_thenReturnUpdatedLecturer() throws Exception {
        String lecturerId = "lecturer-123";
        String courseId = "course-456";
        when(lecturerService.assignCourse(lecturerId, courseId)).thenReturn(lecturerResponseDTO);

        mockMvc.perform(post("/lecturer/{lecturerId}/courses/{courseId}", lecturerId, courseId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(lecturerService, times(1)).assignCourse(lecturerId, courseId);
    }

    @Test
    @DisplayName("Should return not found when assigning course to non-existent lecturer")
    void givenNonExistentLecturerId_whenAssignCourse_thenReturnNotFound() throws Exception {
        String lecturerId = "non-existent-lecturer";
        String courseId = "course-456";
        when(lecturerService.assignCourse(lecturerId, courseId))
                .thenThrow(new ResourceNotFoundException("Lecturer not found with id: " + lecturerId));

        mockMvc.perform(post("/lecturer/{lecturerId}/courses/{courseId}", lecturerId, courseId))
                .andExpect(status().isNotFound());

        verify(lecturerService, times(1)).assignCourse(lecturerId, courseId);
    }

    @Test
    @DisplayName("Should return not found when assigning non-existent course to lecturer")
    void givenNonExistentCourseId_whenAssignCourse_thenReturnNotFound() throws Exception {
        String lecturerId = "lecturer-123";
        String courseId = "non-existent-course";
        when(lecturerService.assignCourse(lecturerId, courseId))
                .thenThrow(new ResourceNotFoundException("Course not found with id: " + courseId));

        mockMvc.perform(post("/lecturer/{lecturerId}/courses/{courseId}", lecturerId, courseId))
                .andExpect(status().isNotFound());

        verify(lecturerService, times(1)).assignCourse(lecturerId, courseId);
    }

    @Test
    @DisplayName("Should remove course from lecturer successfully")
    void givenValidIds_whenRemoveCourseFromLecturer_thenReturnUpdatedLecturer() throws Exception {
        String lecturerId = "lecturer-123";
        String courseId = "course-456";
        when(lecturerService.removeCourse(lecturerId, courseId)).thenReturn(lecturerResponseDTO);

        mockMvc.perform(delete("/lecturer/{lecturerId}/courses/{courseId}", lecturerId, courseId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(lecturerService, times(1)).removeCourse(lecturerId, courseId);
    }

    @Test
    @DisplayName("Should return not found when removing course from non-existent lecturer")
    void givenNonExistentLecturerId_whenRemoveCourse_thenReturnNotFound() throws Exception {
        String lecturerId = "non-existent-lecturer";
        String courseId = "course-456";
        when(lecturerService.removeCourse(lecturerId, courseId))
                .thenThrow(new ResourceNotFoundException("Lecturer not found with id: " + lecturerId));

        mockMvc.perform(delete("/lecturer/{lecturerId}/courses/{courseId}", lecturerId, courseId))
                .andExpect(status().isNotFound());

        verify(lecturerService, times(1)).removeCourse(lecturerId, courseId);
    }

    @Test
    @DisplayName("Should return not found when removing non-existent course from lecturer")
    void givenNonExistentCourseId_whenRemoveCourse_thenReturnNotFound() throws Exception {
        String lecturerId = "lecturer-123";
        String courseId = "non-existent-course";
        when(lecturerService.removeCourse(lecturerId, courseId))
                .thenThrow(new ResourceNotFoundException("Course not found with id: " + courseId));

        mockMvc.perform(delete("/lecturer/{lecturerId}/courses/{courseId}", lecturerId, courseId))
                .andExpect(status().isNotFound());

        verify(lecturerService, times(1)).removeCourse(lecturerId, courseId);
    }



}

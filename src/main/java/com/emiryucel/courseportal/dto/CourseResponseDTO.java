package com.emiryucel.courseportal.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class CourseResponseDTO {

    private String title;
    private String description;
    private Double price;
    private LocalDateTime createdAt;
    private LecturerResponseDTO lecturer;
}

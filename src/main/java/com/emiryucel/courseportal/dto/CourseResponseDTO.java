package com.emiryucel.courseportal.dto;

import lombok.Data;


@Data
public class CourseResponseDTO {

    private String id;
    private String title;
    private String description;
    private Double price;
}

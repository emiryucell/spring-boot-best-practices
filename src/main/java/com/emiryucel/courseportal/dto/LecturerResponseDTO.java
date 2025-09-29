package com.emiryucel.courseportal.dto;

import lombok.Data;


@Data
public class LecturerResponseDTO {
    
    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private String bio;
}

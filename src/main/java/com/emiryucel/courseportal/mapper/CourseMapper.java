package com.emiryucel.courseportal.mapper;

import com.emiryucel.courseportal.dto.CourseDTO;
import com.emiryucel.courseportal.dto.CourseResponseDTO;
import com.emiryucel.courseportal.model.Course;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;


@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
@Component
public interface CourseMapper {

    CourseResponseDTO toResponseDto(Course course);

    Course toEntity(CourseDTO courseDTO);

    void updateEntityFromDto(CourseDTO courseDTO, @MappingTarget Course course);

    java.util.List<CourseResponseDTO> toResponseDtoList(java.util.List<Course> courses);

}

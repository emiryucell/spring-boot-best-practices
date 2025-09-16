package com.emiryucel.courseportal.mapper;

import com.emiryucel.courseportal.dto.LecturerDTO;
import com.emiryucel.courseportal.dto.LecturerResponseDTO;
import com.emiryucel.courseportal.model.Lecturer;
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
public interface LecturerMapper {

    LecturerResponseDTO toResponseDto(Lecturer lecturer);

    Lecturer toEntity(LecturerDTO lecturerDTO);

    void updateEntityFromDto(LecturerDTO lecturerDTO, @MappingTarget Lecturer lecturer);

    java.util.List<LecturerResponseDTO> toResponseDtoList(java.util.List<Lecturer> lecturers);

}

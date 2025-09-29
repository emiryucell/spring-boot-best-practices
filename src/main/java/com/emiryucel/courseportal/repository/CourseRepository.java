package com.emiryucel.courseportal.repository;

import com.emiryucel.courseportal.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    Page<Course> findAll(Pageable pageable);
} 
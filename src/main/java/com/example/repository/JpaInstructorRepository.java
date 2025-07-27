package com.example.repository;

import com.example.model.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaInstructorRepository extends JpaRepository<Instructor, Long> {
}
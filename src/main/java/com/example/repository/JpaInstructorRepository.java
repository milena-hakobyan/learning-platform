package com.example.repository;

import com.example.model.Instructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaInstructorRepository extends JpaRepository<Instructor, Long> {
    Page<Instructor> findAll(Pageable pageable);
}
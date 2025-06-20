package com.example.repository;

import com.example.model.Instructor;

public interface InstructorRepository extends CrudRepository<Instructor, Integer> {
    void ensureInstructorExists(Integer instructorId);
}

package com.example.Repository;

import com.example.Model.Assignment;
import com.example.Model.Grade;
import com.example.Model.Student;
import com.example.Utils.DatabaseConnection;

import java.util.Map;
import java.util.Optional;

public interface StudentRepository extends CrudRepository<Student, Integer>{
}

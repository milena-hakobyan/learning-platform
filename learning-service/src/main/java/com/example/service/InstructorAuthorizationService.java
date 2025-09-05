package com.example.service;

import com.example.model.Course;

public interface InstructorAuthorizationService {
    Course ensureAuthorizedCourseAccess(Long instructorId, Long courseId);
}



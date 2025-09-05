-- task #6 Cover all your use-cases implemented in OOD module using pure Java


-- USER SERVICE

-- 1. Get user by ID (assuming ID is auto-generated or known)
SELECT * FROM users WHERE user_name = 'student1';

-- 2. Get user by email
SELECT * FROM users WHERE email = 'emma@example.com';

-- 3. Get user by username
SELECT * FROM users WHERE user_name = 'instructor3';

-- 4. Get users by role
SELECT * FROM users WHERE user_role = 'ADMIN';

-- 5. Register user (Insert)
INSERT INTO users (user_name, first_name, last_name, email, user_role, password_hash, last_login)
VALUES ('student11', 'George', 'Anderson', 'george@example.com', 'STUDENT', 'hash21', NOW());

-- 6. Login user (validate credentials)
SELECT * FROM users WHERE email = 'bob@example.com' AND password_hash = 'hash11';

-- 7. Update user
UPDATE users
SET first_name = 'Robert',
    last_name = 'Smith Jr.',
    user_name = 'instructor1_updated',
    email = 'bobsmith@example.com',
    user_role = 'ADMIN',
    password_hash = 'newhash11'
WHERE user_name = 'instructor1';

-- 8. Delete user
DELETE FROM enrollments WHERE user_id = 10;

DELETE FROM users WHERE user_name = 'student11';





-- INSTRUCTOR SERVICE

-- 1. Get all courses created by instructor
SELECT * FROM courses WHERE instructor_id = 15;

-- 2. Get all lessons created by instructor (via course)
SELECT l.*
FROM lessons l
	JOIN courses c ON l.course_id = c.id
WHERE c.instructor_id = 14;

-- 3. Get all assignments created by instructor (via course)
SELECT a.*
FROM assignments a
	JOIN courses c ON a.course_id = c.id
WHERE c.instructor_id = 13;

-- 4. Inserting a course, assingment, lesson, material already demonstrated in the fill-tables.sql file

-- 5. Insert material
INSERT INTO materials (title, content_type, category, url, instructor_id)
VALUES ('mock material', 'PDF', 'Java',  'https://example.com/uml.pdf', 12);

-- 6. Delete material by ID
DELETE FROM materials WHERE title = 'mock material';


-- 7. Add an assignment
INSERT INTO assignments (title, description, due_date, max_score, course_id, material_id)
VALUES ('Mock Assignment', 'This is a test assignment.', CURRENT_DATE + INTERVAL '5 days', 100, 1, 1);

-- Get the assignment_id of the inserted assignment (if serial, you may need to fetch it via RETURNING or a select)
-- Assuming assignment_id = 11 for example purposes

-- 8. Insert a submission for student_id = 2 (matching your student ids)
INSERT INTO submissions (assignment_id, student_id, content)
VALUES (11, 2, 'My mock assignment submission.');

-- 9. Give feedback on the submission
INSERT INTO grades (submission_id, score, feedback)
VALUES (11, 100, 'good jobbbb');

-- 10. Delete the grade record
DELETE FROM grades WHERE submission_id = 21;


-- 11. Post an announcement
INSERT INTO announcements (title, content, instructor_id, course_id)
VALUES ('Project Deadline', 'Final project due on June 30.', 11, 9);




-- STUDENT SERVICE

-- 1. Get all courses a student is enrolled in
SELECT c.*
FROM courses c
	JOIN enrollments e ON c.id = e.course_id
WHERE e.user_id = 1;


-- 2. Enroll a student in a course
INSERT INTO enrollments (user_id, course_id, enrollment_date)
VALUES (1, 3, CURRENT_TIMESTAMP);


-- 3. Drop a student from a course
DELETE FROM enrollments
WHERE user_id = 1 AND course_id = 3;


-- 4. Get all submissions made by a student
SELECT s.*
FROM submissions s
WHERE s.student_id = 1;


-- 5. Browse all available courses
SELECT *
FROM courses;


-- 6. Access lessons/materials for a student's course
SELECT l.*
FROM lessons l
	JOIN courses c ON l.course_id = c.id
	JOIN enrollments e ON e.course_id = c.id
WHERE e.user_id = 1 AND c.id = 1;


-- 7. Submit an assignment
INSERT INTO submissions (assignment_id, student_id, content)
VALUES (11, 1, 'mock submission');


-- 8. View grades grouped by course and assignment for a student
SELECT
  c.id AS course_id,
  c.title AS course_title,
  a.id AS assignment_id,
  a.title AS assignment_title,
  g.score,
  g.feedback
FROM courses c
	JOIN assignments a ON a.course_id = c.id
	JOIN submissions s ON s.assignment_id = a.id
	LEFT JOIN grades g ON g.submission_id = s.id
WHERE s.student_id = 1;






--COURSE SERVICE

-- 1. Create a course
INSERT INTO courses (title, description, url, instructor_id)
VALUES ('Mock Java Basics', 'sth@example.com', 'Programming', 12);


-- 2. Update a course
UPDATE courses
SET title = 'Updated Java Basics',
    description = 'Updated introduction to Java programming.',
    url = 'https://example.com/updated-java-basics',
    instructor_id = 12
WHERE id = 14;


-- 3. Add an assignment to a course
INSERT INTO assignments (title, description, due_date, max_score, course_id, material_id)
VALUES('Variables and Data Types', 'Understand Java types', CURRENT_DATE + INTERVAL '3 days', 100, 10, 10);


-- 4. Delete an assignment from a course
DELETE FROM assignments WHERE course_id = 14;


-- 5. Add a lesson to a course
INSERT INTO lessons (title, content_description, course_id)
VALUES ('Mock lesson', 'Mock content', 14);


-- 6. Delete a lesson from a course
DELETE FROM lessons WHERE course_id = 14;


-- 7. Get courses by instructor
SELECT *
FROM courses
WHERE instructor_id = 14;
select * from courses;


-- 8. Get course by id
SELECT * FROM courses WHERE id = 1;

-- 9. Get course by title
SELECT * FROM courses WHERE title = 'Java Basics';

-- 10. Get course by category
SELECT * FROM courses WHERE category = 'Frontend';

-- 11. Get all courses
SELECT * FROM courses;

-- 12. Delete the course
DELETE FROM courses WHERE id = 14;

-- Create a script with SQL requests that cover your domain business logic. It should include:


-- task #1 At least one full CRUD set of queries for your domain entity

-- Create
INSERT INTO lessons (title, content_description, course_id)
VALUES (
    'Introduction to Pandas',
    'This lesson covers data manipulation using the Pandas library in Python.',
    3  -- Assuming course with ID 3 is "Data analysis with Python"
);

INSERT INTO courses (title, description, url, instructor_id)
VALUES 
('Java Collections', 'Comprehensive guide to Java Collections Framework', 'http://example.com/java-collections', 11),
('Responsive Design', 'Make web apps responsive with CSS and media queries', 'http://example.com/responsive-css', 12);


-- Read
SELECT *
FROM courses;


SELECT *
FROM students
WHERE progress_percentage >= 85;

-- Update
UPDATE courses
SET description = 'Master data analysis techniques using Python libraries such as Pandas, NumPy, and Matplotlib.'
WHERE id = 3;


-- Delete 
DELETE FROM lessons
WHERE id = 11;






-- task #2 Search query with dynamic filters, pagination and sorting
SELECT *
FROM announcements
WHERE 
    (title ILIKE ANY (ARRAY['%exam%', '%midterm%', '%quiz%', '%homework%'])
     OR content ILIKE ANY (ARRAY['%exam%', '%midterm%', '%quiz%', '%homework%']))
ORDER BY posted_date 
LIMIT 10 OFFSET 5;






-- task #3
SELECT 
    a.id AS announcement_id,
    a.title AS announcement_title,
    a.content,
    a.posted_date,
    u.first_name || ' ' || u.last_name AS instructor_full_name,
    c.title AS course_title
FROM announcements a
	JOIN instructors i ON a.instructor_id = i.user_id
	JOIN users u ON i.user_id = u.user_id
	JOIN courses c ON a.course_id = c.id;




SELECT c.id AS course_id, c.title AS course_title,
    COUNT(DISTINCT e.user_id) AS enrolled_students,
    AVG(s.progress_percentage) AS avg_progress_percentage
FROM courses c
	LEFT JOIN enrollments e ON c.id = e.course_id
	LEFT JOIN students s ON e.user_id = s.user_id
GROUP BY c.id, c.title
ORDER BY enrolled_students DESC, avg_progress_percentage DESC;





-- task #4 Statistic query; can be not related to your use-cases (for example return authors and number of books they wrote)

-- retrieves all student records joined with their user details, and sorts the results in descending order by the number of completed courses and then by progress percentage.
SELECT *
FROM students s 
	JOIN users u USING(user_id)
ORDER BY completed_courses DESC, progress_percentage DESC;

-- calculate the average grade of assignments for all courses
SELECT c.id, c.title, AVG(g.score) avg_grade
FROM courses c
	JOIN assignments a ON c.id = a.course_id
	JOIN submissions s ON a.id = s.assignment_id
	JOIN grades g ON s.grade_id = g.id
GROUP BY c.id, c.title;




-- task #5 Top-something query (for example return authors and number of books they wrote ordered by books count)

-- The query below lists instructors with how many courses they created, sorted by most to least
SELECT u.user_id, u.user_name, u.first_name, u.last_name, count(*) course_count
FROM courses c 
	 JOIN instructors i ON i.user_id = c.instructor_id
	 JOIN users u ON u.user_id = i.user_id
GROUP BY u.user_id, u.user_name, u.first_name, u.last_name
ORDER BY course_count DESC;

select * from enrollments;

-- Example: link each material to the first lesson of the course it belongs to
INSERT INTO lesson_material (lesson_id, material_id)
SELECT l.id AS lesson_id, m.id AS material_id
FROM materials m
JOIN lessons l ON m.lesson_id = l.id;  -- assuming materials have lesson_id


-- task #6 Cover all your use-cases implemented in OOD module using pure Java
-- Please find the queries inside the user-case-task6.sql.
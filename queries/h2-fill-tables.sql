INSERT INTO users (user_name, first_name, last_name, email, user_role, password_hash, last_login)
VALUES
('student1', 'Alice', 'Walker', 'alice@example.com', 'STUDENT', 'hash1', NOW()),
('student2', 'Ethan', 'Brown', 'ethan@example.com', 'STUDENT', 'hash2', NOW()),
('student3', 'Liam', 'Smith', 'liam@example.com', 'STUDENT', 'hash3', NOW()),
('student4', 'Olivia', 'Johnson', 'olivia@example.com', 'STUDENT', 'hash4', NOW()),
('student5', 'Noah', 'Williams', 'noah@example.com', 'STUDENT', 'hash5', NOW()),
('student6', 'Emma', 'Jones', 'emma@example.com', 'STUDENT', 'hash6', NOW()),
('student7', 'Ava', 'Brown', 'ava@example.com', 'STUDENT', 'hash7', NOW()),
('student8', 'Sophia', 'Davis', 'sophia@example.com', 'STUDENT', 'hash8', NOW()),
('student9', 'Isabella', 'Miller', 'isabella@example.com', 'STUDENT', 'hash9', NOW()),
('student10', 'Mia', 'Wilson', 'mia@example.com', 'STUDENT', 'hash10', NOW()),
('instructor1', 'Bob', 'Smith', 'bob@example.com', 'INSTRUCTOR', 'hash11', NOW()),
('instructor2', 'Diana', 'Lee', 'diana@example.com', 'INSTRUCTOR', 'hash12', NOW()),
('instructor3', 'Johnny', 'Depp', 'johnny@example.com', 'INSTRUCTOR', 'hash13', NOW()),
('instructor4', 'Jane', 'Doe', 'jane@example.com', 'INSTRUCTOR', 'hash14', NOW()),
('instructor5', 'Mark', 'Taylor', 'mark@example.com', 'INSTRUCTOR', 'hash15', NOW()),
('admin1', 'Carol', 'Morris', 'carol@example.com', 'ADMIN', 'hash16', NOW()),
('admin2', 'Steve', 'Clark', 'steve@example.com', 'ADMIN', 'hash17', NOW()),
('admin3', 'Laura', 'Lewis', 'laura@example.com', 'ADMIN', 'hash18', NOW()),
('admin4', 'Kevin', 'Walker', 'kevin@example.com', 'ADMIN', 'hash19', NOW()),
('admin5', 'Rachel', 'Hall', 'rachel@example.com', 'ADMIN', 'hash20', NOW());


INSERT INTO students (user_id, progress_percentage, completed_courses, current_courses)
VALUES
(1, 80.00, 4, 2),
(2, 60.50, 2, 3),
(3, 70.00, 3, 2),
(4, 85.00, 5, 1),
(5, 90.00, 6, 0),
(6, 75.00, 4, 2),
(7, 65.00, 3, 3),
(8, 55.00, 2, 4),
(9, 95.00, 7, 0),
(10, 50.00, 1, 5);


INSERT INTO instructors (user_id, bio, expertise, total_courses_created, rating, is_verified)
VALUES
(11, 'Java and Spring expert.', 'Java, Spring', 3, 4.8, TRUE),
(12, 'Frontend developer', 'HTML, CSS, React', 2, 4.5, FALSE),
(13, 'Data Scientist', 'Python, Machine Learning', 4, 4.9, TRUE),
(14, 'DevOps Engineer', 'Docker, Kubernetes', 5, 4.7, TRUE),
(15, 'Mobile App Developer', 'Flutter, Dart', 2, 4.6, FALSE);


INSERT INTO admins (user_id, access_level, privileges, is_super_admin)
VALUES
(16, 5, 'user_mgmt, course_approval', TRUE),
(17, 4, 'user_mgmt', FALSE),
(18, 3, 'course_approval', FALSE),
(19, 2, 'reporting', FALSE),
(20, 1, 'support', FALSE);


INSERT INTO courses (title, description, category, url, instructor_id)
VALUES
('Java Basics', 'Learn core Java concepts', 'Programming', 'http://example.com/java', 11),
('React Fundamentals', 'Intro to React.js', 'Frontend', 'http://example.com/react', 12),
('Python for Data Science', 'Data analysis with Python', 'Data Science', 'http://example.com/python', 13),
('Docker Essentials', 'Containerization with Docker', 'DevOps', 'http://example.com/docker', 14),
('Flutter Development', 'Build mobile apps with Flutter', 'Mobile Development', 'http://example.com/flutter', 15),
('Advanced Java', 'Deep dive into Java', 'Programming', 'http://example.com/adv-java', 11),
('CSS Mastery', 'Advanced CSS techniques', 'Frontend', 'http://example.com/css', 12),
('Machine Learning', 'Introduction to ML concepts', 'Data Science', 'http://example.com/ml', 13),
('Kubernetes Basics', 'Orchestrate containers', 'DevOps', 'http://example.com/k8s', 14),
('Dart Programming', 'Learn Dart language', 'Programming', 'http://example.com/dart', 15);



INSERT INTO materials (title, content_type, category, url, instructor_id)
VALUES
('Java Intro Slides', 'PDF', 'Java', 'http://example.com/java_intro.pdf', 11),
('React Cheat Sheet', 'PDF', 'React', 'http://example.com/react_cheat.pdf', 12),
('Python Data Analysis', 'Video', 'Python', 'http://example.com/python_data.mp4', 13),
('Docker Commands', 'PDF', 'Docker', 'http://example.com/docker_cmds.pdf', 14),
('Flutter Widgets', 'Video', 'Flutter', 'http://example.com/flutter_widgets.mp4', 15),
('Advanced Java Slides', 'PDF', 'Java', 'http://example.com/adv_java.pdf', 11),
('CSS Grid Tutorial', 'Video', 'CSS', 'http://example.com/css_grid.mp4', 12),
('ML Algorithms', 'PDF', 'Machine Learning', 'http://example.com/ml_algorithms.pdf', 13),
('Kubernetes Deployment', 'Video', 'Kubernetes', 'http://example.com/k8s_deploy.mp4', 14),
('Dart Basics', 'PDF', 'Dart', 'http://example.com/dart_basics.pdf', 15);


INSERT INTO lessons (title, content_description, course_id)
VALUES
('Java Variables', 'Understanding Java variable types', 1),
('React Components', 'Functional vs Class Components', 2),
('Python Libraries', 'Overview of Pandas and NumPy', 3),
('Docker Images', 'Creating and managing images', 4),
('Flutter Layouts', 'Designing responsive layouts', 5),
('Java Streams', 'Working with Java Streams', 6),
('CSS Flexbox', 'Layout with Flexbox', 7),
('Supervised Learning', 'Introduction to supervised ML', 8),
('Kubernetes Services', 'Managing services in K8s', 9),
('Dart Functions', 'Defining and using functions', 10);



INSERT INTO assignments (title, description, due_date, max_score, course_id, material_id)
VALUES
('Java Assignment', 'Write a simple Java class', CURRENT_DATE + 5, 100, 1, 1),
('React Assignment', 'Create a React component', CURRENT_DATE + 7, 100, 2, 2),
('Python Assignment', 'Analyze data with Pandas', CURRENT_DATE + 6, 100, 3, 3),
('Docker Assignment', 'Build a Docker image', CURRENT_DATE + 8, 100, 4, 4),
('Flutter Assignment', 'Design a Flutter UI', CURRENT_DATE + 9, 100, 5, 5),
('Advanced Java Assignment', 'Implement Java Streams', CURRENT_DATE + 10, 100, 6, 6),
('CSS Assignment', 'Create a Flexbox layout', CURRENT_DATE + 4, 100, 7, 7),
('ML Assignment', 'Train a simple model', CURRENT_DATE + 11, 100, 8, 8),
('Kubernetes Assignment', 'Deploy an app on K8s', CURRENT_DATE + 12, 100, 9, 9),
('Dart Assignment', 'Write Dart functions', CURRENT_DATE + 3, 100, 10, 10);





INSERT INTO submissions (assignment_id, student_id, content)
VALUES
(1, 1, 'My Java class implementation.'),
(2, 2, 'My React component.'),
(3, 3, 'Data analysis with Pandas.'),
(4, 4, 'Docker image built.'),
(5, 5, 'Flutter UI designed.'),
(6, 6, 'Java Streams implemented.'),
(7, 7, 'Flexbox layout created.'),
(8, 8, 'Trained a simple model.'),
(9, 9, 'App deployed on K8s.'),
(10, 10, 'Dart functions written.'),
(1, 4, 'Java classes with design patterns.'),
(2, 5, 'React app with Redux integration.'),
(3, 6, 'Pandas advanced data manipulation.'),
(4, 7, 'Docker compose setup created.'),
(5, 8, 'Flutter app with Firebase backend.'),
(6, 9, 'Java Streams with collectors.'),
(7, 10, 'Flexbox responsive design.'),
(8, 1, 'Machine learning model training.'),
(9, 2, 'Kubernetes cluster setup.'),
(10, 3, 'Dart Flutter widgets customization.');


INSERT INTO activity_logs (user_id, action, timestamp) VALUES
(1, 'Logged in', '2025-05-20 08:00:00'),
(2, 'Viewed course "Java Basics"', '2025-05-20 08:15:00'),
(3, 'Submitted assignment 1', '2025-05-20 09:00:00'),
(4, 'Posted comment on course "React Fundamentals"', '2025-05-20 09:30:00'),
(5, 'Completed lesson 3 in "Python for Data Science"', '2025-05-20 10:00:00'),
(6, 'Logged out', '2025-05-20 10:15:00'),
(7, 'Viewed grades for assignment 2', '2025-05-20 10:45:00'),
(8, 'Enrolled in "Docker Essentials"', '2025-05-20 11:00:00'),
(9, 'Started quiz in "Flutter Development"', '2025-05-20 11:30:00'),
(10, 'Updated profile information', '2025-05-20 12:00:00');


INSERT INTO enrollments (user_id, course_id, enrollment_date)
SELECT DISTINCT s.student_id, a.course_id, CURRENT_DATE
FROM submissions s
JOIN assignments a ON s.assignment_id = a.id
WHERE NOT EXISTS (
    SELECT 1 FROM enrollments e
    WHERE e.user_id = s.student_id AND e.course_id = a.course_id
);


INSERT INTO grades (submission_id, score, feedback)
VALUES
(1, 95.0, 'Excellent work!'),
(2, 88.5, 'Well done!'),
(3, 92.0, 'Great analysis!'),
(4, 85.0, 'Good job!'),
(5, 78.0, 'Satisfactory effort.'),
(6, 89.0, 'Impressive thinking!'),
(7, 91.5, 'Very detailed response.'),
(8, 83.0, 'Keep improving.'),
(9, 87.0, 'Nice structure.'),
(10, 90.0, 'Clear and concise.'),
(11, 90.0, 'Well structured and clear code.'),
(12, 85.5, 'Good use of state management.'),
(13, 88.0, 'Great data manipulation skills.'),
(14, 92.0, 'Excellent Docker setup.'),
(15, 89.5, 'Nice integration with backend.'),
(16, 91.0, 'Good use of Streams API.'),
(17, 87.0, 'Responsive and clean design.'),
(18, 93.5, 'Strong ML model training.'),
(19, 90.5, 'Kubernetes configuration is solid.'),
(20, 88.0, 'Custom widgets implemented well.');


UPDATE submissions SET grade_id = id WHERE id BETWEEN 1 AND 20;

UPDATE submissions SET status = 'graded' WHERE id BETWEEN 1 AND 20;

INSERT INTO announcements (title, content, instructor_id, course_id) VALUES
('Welcome to Java Basics', 'Welcome students to the Java Basics course! Get ready to code.', 11, 1),
('React Fundamentals Update', 'New React hooks section added. Check it out!', 12, 2),
('Python Data Science Tips', 'Remember to explore Pandas and NumPy for your assignments.', 13, 3),
('Docker Essentials Webinar', 'Join our live Docker webinar next week.', 14, 4),
('Java Midterm Exam', 'Midterm exam will cover all Java basics and OOP concepts.', 11, 1),
('React Midterm Notice', 'React midterm exam scheduled for next Wednesday.', 12, 2),
('Python Final Exam', 'Final exam for Python for Data Science is approaching.', 13, 3),
('Docker Midterm Homework', 'Complete the Docker midterm homework assignment.', 14, 4),
('Flutter Exam Prep', 'Review materials for the upcoming Flutter exam.', 15, 5),
('Advanced Java Midterm', 'Advanced Java midterm will test on streams and concurrency.', 11, 6),
('CSS Midterm Announcement', 'Prepare for the CSS midterm exam next week.', 12, 7),
('Machine Learning Exam', 'ML exam will include supervised and unsupervised learning topics.', 13, 8),
('Kubernetes Midterm', 'Midterm quiz and exam for Kubernetes basics.', 14, 9),
('Dart Programming Exam', 'Dart midterm exam date finalized.', 15, 10),
('Flutter UI Challenges', 'Weekly Flutter challenges posted.', 15, 5),
('Advanced Java Resources', 'Check out these new advanced Java resources.', 11, 6),
('CSS Mastery Q&A', 'Q&A session scheduled for Friday.', 12, 7),
('ML Model Improvements', 'Updated ML models available in course materials.', 13, 8),
('Kubernetes Cluster Setup', 'Guide on setting up clusters has been updated.', 14, 9),
('Dart Programming Tips', 'Useful Dart tricks added to lessons.', 15, 10),
('Java Basics Quiz', 'Quiz 1 will cover Java fundamentals. Prepare well!', 11, 1),
('React Homework Reminder', 'Don’t forget to submit your React homework by Friday.', 12, 2),
('Python Data Science Quiz', 'Quiz on data manipulation with Pandas is scheduled next Monday.', 13, 3),
('Docker Homework Posted', 'Docker container setup homework is now available.', 14, 4),
('Flutter Quiz Announcement', 'Quiz on Flutter widgets will be next week.', 15, 5),
('Advanced Java Homework', 'Complete the advanced Java homework on streams and lambdas.', 11, 6),
('CSS Mastery Quiz', 'Prepare for the CSS selectors quiz this Thursday.', 12, 7),
('ML Homework Update', 'New machine learning homework has been assigned.', 13, 8),
('Kubernetes Quiz Alert', 'Kubernetes concepts quiz will be held next session.', 14, 9),
('Dart Homework Deadline', 'Submit your Dart programming homework by Sunday.', 15, 10);


INSERT INTO lesson_assignment (lesson_id, assignment_id)
VALUES
(1, 1),  -- Java Variables lesson linked to Java Variables Quiz
(2, 2),  -- React Components lesson linked to React Components Homework
(3, 3),  -- Python Libraries lesson linked to Python Libraries Exercise
(4, 4),  -- Docker Images lesson linked to Docker Images Task
(5, 5),  -- Flutter Layouts lesson linked to Flutter Layouts Project
(6, 6),  -- Java Streams lesson linked to Java Streams Assignment
(7, 7),  -- CSS Flexbox lesson linked to CSS Flexbox Challenge
(8, 8),  -- Supervised Learning lesson linked to Supervised Learning Quiz
(9, 9),  -- Kubernetes Services lesson linked to Kubernetes Services Lab
(10, 10); -- Dart Functions lesson linked to Dart Functions Test

INSERT INTO lesson_material (lesson_id, material_id)
VALUES
(1, 1),  -- Java Variables linked to Java Intro Slides
(2, 2),  -- React Components linked to React Cheat Sheet
(3, 3),  -- Python Libraries linked to Python Data Analysis
(4, 4),  -- Docker Images linked to Docker Commands
(5, 5),  -- Flutter Layouts linked to Flutter Widgets
(6, 6),  -- Java Streams linked to Advanced Java Slides
(7, 7),  -- CSS Flexbox linked to CSS Grid Tutorial
(8, 8),  -- Supervised Learning linked to ML Algorithms
(9, 9),  -- Kubernetes Services linked to Kubernetes Deployment
(10, 10); -- Dart Functions linked to Dart Basics



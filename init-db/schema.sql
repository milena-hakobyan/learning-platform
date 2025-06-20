-- ENUM type for user roles
CREATE TYPE user_role AS ENUM ('STUDENT', 'INSTRUCTOR', 'ADMIN');

-- Base users table
CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    user_name VARCHAR(50),
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100) UNIQUE NOT NULL,
    user_role user_role,
    password_hash VARCHAR(255),
    last_login TIMESTAMP,
    last_action TEXT,
    is_active BOOLEAN DEFAULT TRUE
);

-- Student-specific fields
CREATE TABLE IF NOT EXISTS students (
    user_id INTEGER PRIMARY KEY,
    progress_percentage DECIMAL(5,2),
    completed_courses INT,
    current_courses INT,

    CONSTRAINT fk_student_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);

-- Instructor-specific fields
CREATE TABLE IF NOT EXISTS instructors (
    user_id INTEGER PRIMARY KEY,
    bio TEXT,
    expertise TEXT[],
    total_courses_created INT,
    rating DECIMAL(3,2),
    is_verified BOOLEAN,

    CONSTRAINT fk_instructor_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);

-- Admin-specific fields
CREATE TABLE IF NOT EXISTS admins (
    user_id INTEGER PRIMARY KEY,
    access_level INTEGER DEFAULT 1,
    privileges TEXT[],
    is_super_admin BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_admin_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);

-- Courses
CREATE TABLE IF NOT EXISTS courses (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    url TEXT,
    instructor_id INT NOT NULL,

    CONSTRAINT fk_course_instructor
        FOREIGN KEY (instructor_id)
        REFERENCES instructors(user_id)
        ON DELETE CASCADE
);

-- Materials
CREATE TABLE IF NOT EXISTS materials (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content_type VARCHAR(50) NOT NULL,
    category VARCHAR(100),
    url TEXT NOT NULL,
    instructor_id INTEGER,

    uploaded_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_material_instructor
        FOREIGN KEY (instructor_id)
        REFERENCES instructors(user_id)
        ON DELETE SET NULL
);

-- Lessons
CREATE TABLE IF NOT EXISTS lessons (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content_description TEXT,
    course_id INTEGER NOT NULL,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_lesson_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id)
        ON DELETE CASCADE
);

-- Assignments
CREATE TABLE IF NOT EXISTS assignments (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    due_date DATE NOT NULL,
    max_score INT NOT NULL,
    course_id INT NOT NULL,
    material_id INT,

    CONSTRAINT fk_assignment_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_assignment_material
        FOREIGN KEY (material_id)
        REFERENCES materials(id)
        ON DELETE SET NULL
);

-- Submissions (1 per assignment per student)
CREATE TABLE IF NOT EXISTS submissions (
    id SERIAL PRIMARY KEY,
    assignment_id INTEGER NOT NULL,
    student_id INTEGER NOT NULL,
    content TEXT NOT NULL,
    grade_id INTEGER,
    status VARCHAR(50) DEFAULT 'submitted',

    CONSTRAINT fk_submission_assignment
        FOREIGN KEY (assignment_id)
        REFERENCES assignments(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_submission_student
        FOREIGN KEY (student_id)
        REFERENCES students(user_id)
        ON DELETE CASCADE
);

-- Grades
CREATE TABLE IF NOT EXISTS grades (
    id SERIAL PRIMARY KEY,
    submission_id INTEGER UNIQUE NOT NULL,
    score DECIMAL(5,2) NOT NULL CHECK (score >= 0),
    feedback TEXT,

    CONSTRAINT fk_grade_submission
        FOREIGN KEY (submission_id)
        REFERENCES submissions(id)
        ON DELETE CASCADE
);

-- Link submission to grade
ALTER TABLE submissions
ADD CONSTRAINT fk_submission_grade
FOREIGN KEY (grade_id)
REFERENCES grades(id)
ON DELETE SET NULL;

-- Activity logs
CREATE TABLE IF NOT EXISTS activity_logs (
    log_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    action TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_activity_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);

-- Announcements
CREATE TABLE IF NOT EXISTS announcements (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    posted_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    instructor_id INTEGER NOT NULL,
    course_id INTEGER NOT NULL,

    CONSTRAINT fk_announcement_instructor
        FOREIGN KEY (instructor_id)
        REFERENCES instructors(user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_announcement_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id)
        ON DELETE CASCADE
);


CREATE TABLE enrollments (
     user_id INTEGER NOT NULL,
     course_id INTEGER NOT NULL,
     enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     PRIMARY KEY (user_id, course_id),
     FOREIGN KEY (user_id) REFERENCES students(user_id),
     FOREIGN KEY (course_id) REFERENCES courses(id)
 );



 CREATE TABLE IF NOT EXISTS activity_logs (
     log_id SERIAL PRIMARY KEY,
     user_id INTEGER NOT NULL,
     action TEXT NOT NULL,
     timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     CONSTRAINT fk_activity_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
 );


 CREATE TABLE IF NOT EXISTS lesson_material (
     lesson_id INTEGER NOT NULL,
     material_id INTEGER NOT NULL,
     PRIMARY KEY (lesson_id, material_id),
     CONSTRAINT fk_lesson_material_lesson
         FOREIGN KEY (lesson_id)
         REFERENCES lessons(id)
         ON DELETE CASCADE,
     CONSTRAINT fk_lesson_material_material
         FOREIGN KEY (material_id)
         REFERENCES materials(id)
         ON DELETE CASCADE
 );

 -- lesson_assignment table: link lessons to assignments (one-to-many or many-to-many)
 CREATE TABLE IF NOT EXISTS lesson_assignment (
     lesson_id INTEGER NOT NULL,
     assignment_id INTEGER NOT NULL,
     PRIMARY KEY (lesson_id, assignment_id),
     CONSTRAINT fk_lesson_assignment_lesson
         FOREIGN KEY (lesson_id)
         REFERENCES lessons(id)
         ON DELETE CASCADE,
     CONSTRAINT fk_lesson_assignment_assignment
         FOREIGN KEY (assignment_id)
         REFERENCES assignments(id)
         ON DELETE CASCADE
 );
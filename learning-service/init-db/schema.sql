-- ENUM type for user roles
CREATE TYPE user_role AS ENUM ('STUDENT', 'INSTRUCTOR', 'ADMIN');

-- Base users table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    user_name VARCHAR(50) UNIQUE NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100) UNIQUE NOT NULL,
    user_role user_role NOT NULL,
    password_hash VARCHAR(255),
    last_login TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Student-specific fields
CREATE TABLE IF NOT EXISTS students (
    user_id INTEGER PRIMARY KEY,
    progress_percentage DECIMAL(5,2),
    completed_courses INT,
    current_courses INT,
    CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Instructor-specific fields
CREATE TABLE IF NOT EXISTS instructors (
    user_id INTEGER PRIMARY KEY,
    bio TEXT,
    expertise TEXT,
    total_courses_created INT,
    rating DECIMAL(3,2),
    is_verified BOOLEAN,
    CONSTRAINT fk_instructor_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Admin-specific fields
CREATE TABLE IF NOT EXISTS admins (
    user_id INTEGER PRIMARY KEY,
    access_level INTEGER DEFAULT 1,
    privileges TEXT,
    is_super_admin BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_admin_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Courses
CREATE TABLE IF NOT EXISTS courses (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    url TEXT,
    instructor_id INTEGER NOT NULL,
    CONSTRAINT fk_course_instructor FOREIGN KEY (instructor_id) REFERENCES instructors(user_id) ON DELETE CASCADE
);

-- Materials
CREATE TABLE IF NOT EXISTS materials (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content_type VARCHAR(50) NOT NULL,
    category VARCHAR(100),
    url TEXT NOT NULL,
    instructor_id INTEGER,
    upload_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_material_instructor FOREIGN KEY (instructor_id) REFERENCES instructors(user_id) ON DELETE SET NULL
);

-- Lessons
CREATE TABLE IF NOT EXISTS lessons (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content_description TEXT,
    course_id INTEGER NOT NULL,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lesson_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Assignments
CREATE TABLE IF NOT EXISTS assignments (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    due_date DATE NOT NULL,
    max_score INT NOT NULL,
    course_id INTEGER NOT NULL,
    CONSTRAINT fk_assignment_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Submissions (1 per assignment per student)
CREATE TABLE IF NOT EXISTS submissions (
    id SERIAL PRIMARY KEY,
    assignment_id INTEGER NOT NULL,
    student_id INTEGER NOT NULL,
    content_link TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'submitted',
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_submission_assignment FOREIGN KEY (assignment_id) REFERENCES assignments(id) ON DELETE CASCADE,
    CONSTRAINT fk_submission_student FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
    CONSTRAINT unique_submission_per_student_assignment UNIQUE (assignment_id, student_id)
);

-- Grades
CREATE TABLE IF NOT EXISTS grades (
    id SERIAL PRIMARY KEY,
    submission_id INTEGER UNIQUE NOT NULL,
    score DECIMAL(5,2) NOT NULL CHECK (score >= 0),
    feedback TEXT,
    graded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_grade_submission FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE CASCADE
);

-- Activity logs
CREATE TABLE IF NOT EXISTS activity_logs (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    action TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_activity_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Announcements
CREATE TABLE IF NOT EXISTS announcements (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    posted_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    instructor_id INTEGER NOT NULL,
    course_id INTEGER NOT NULL,
    CONSTRAINT fk_announcement_instructor FOREIGN KEY (instructor_id) REFERENCES instructors(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_announcement_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Enrollments
CREATE TABLE IF NOT EXISTS enrollments (
    student_id INTEGER NOT NULL,
    course_id INTEGER NOT NULL,
    enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES students(user_id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- Lesson-Material relationship
CREATE TABLE IF NOT EXISTS lesson_materials (
    lesson_id INTEGER NOT NULL,
    material_id INTEGER NOT NULL,
    PRIMARY KEY (lesson_id, material_id),
    CONSTRAINT fk_lesson_material_lesson FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE,
    CONSTRAINT fk_lesson_material_material FOREIGN KEY (material_id) REFERENCES materials(id) ON DELETE CASCADE
);

-- Assignment-Material relationship
CREATE TABLE IF NOT EXISTS assignment_materials (
    assignment_id INTEGER NOT NULL,
    material_id INTEGER NOT NULL,
    PRIMARY KEY (assignment_id, material_id),
    CONSTRAINT fk_assignment_material_assignment FOREIGN KEY (assignment_id) REFERENCES assignments(id) ON DELETE CASCADE,
    CONSTRAINT fk_assignment_material_material FOREIGN KEY (material_id) REFERENCES materials(id) ON DELETE CASCADE
);

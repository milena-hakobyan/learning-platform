CREATE TYPE user_role AS ENUM ('STUDENT', 'INSTRUCTOR', 'ADMIN');

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

CREATE TABLE IF NOT EXISTS students (
    user_id INTEGER PRIMARY KEY,
    progress_percentage DECIMAL(5,2),
    completed_courses INT,
    current_courses INT
) INHERITS (users);

CREATE TABLE IF NOT EXISTS instructors (
    user_id INTEGER PRIMARY KEY,
    bio TEXT,
    expertise TEXT[],
    total_courses_created INT,
    rating DECIMAL(3,2),
    is_verified BOOLEAN
) INHERITS (users);

CREATE TABLE IF NOT EXISTS admins (
    user_id INTEGER PRIMARY KEY,
    access_level INTEGER DEFAULT 1,
    privileges TEXT[],
    is_super_admin BOOLEAN DEFAULT FALSE
) INHERITS (users);


CREATE TABLE IF NOT EXISTS courses (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    url TEXT,
    instructor_id INT NOT NULL,
    
    CONSTRAINT fk_instructor
        FOREIGN KEY (instructor_id)
        REFERENCES instructors(user_id)
        ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS materials (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content_type VARCHAR(50) NOT NULL,           -- e.g., 'video', 'reading', etc.
    category VARCHAR(100),               -- e.g., 'math', 'history', etc.
    url TEXT NOT NULL,
    instructor_id INTEGER NOT NULL,      -- foreign key to instructors (or users table)
    uploaded_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_instructor
        FOREIGN KEY (instructor_id) 
        REFERENCES instructors(user_id)
		ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS lessons (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content_description TEXT,
    course_id INTEGER NOT NULL, -- assuming you'll have a courses table later
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

	CONSTRAINT fk_course
        FOREIGN KEY (course_id) 
        REFERENCES courses(id)
		ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS assignments (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    due_date DATE NOT NULL,
    max_score INT NOT NULL,
    course_id INT NOT NULL,
    material_id INT,

    CONSTRAINT fk_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_material
        FOREIGN KEY (material_id)
        REFERENCES materials(id)
        ON DELETE SET NULL
);

-- Step 1: Create submissions without fk_grade
CREATE TABLE IF NOT EXISTS submissions (
    id SERIAL PRIMARY KEY,
    assignment_id INTEGER NOT NULL,
    student_id INTEGER NOT NULL,
    content TEXT NOT NULL,
    grade_id INTEGER,  -- We'll add FK later
    status VARCHAR(50) DEFAULT 'submitted',

    CONSTRAINT fk_assignment
        FOREIGN KEY (assignment_id)
        REFERENCES assignments(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_student
        FOREIGN KEY (student_id)
        REFERENCES students(user_id)
        ON DELETE CASCADE
);

-- Step 2: Create grades with a one-to-one FK to submissions
CREATE TABLE IF NOT EXISTS grades (
    id SERIAL PRIMARY KEY,
    submission_id INTEGER UNIQUE NOT NULL,  -- enforce one-to-one
    score DECIMAL(5,2) NOT NULL CHECK (score >= 0),
    feedback TEXT,

    CONSTRAINT fk_submission
        FOREIGN KEY (submission_id)
        REFERENCES submissions(id)
        ON DELETE CASCADE
);

-- Step 3: Add the FK from submissions.grade_id → grades.id
ALTER TABLE submissions
ADD CONSTRAINT fk_grade
FOREIGN KEY (grade_id)
REFERENCES grades(id)
ON DELETE SET NULL;



CREATE TABLE IF NOT EXISTS activity_logs (
    log_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    action TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);

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


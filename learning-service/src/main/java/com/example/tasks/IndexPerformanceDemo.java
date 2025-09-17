package com.example.tasks;

import com.example.utils.DatabaseConnection;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;

public class IndexPerformanceDemo {
    public static void main(String[] args) throws SQLException {
        // Load environment variables with DB connection info
        Dotenv dotenv = Dotenv.load();

        String host = dotenv.get("DB_HOST");
        String port = dotenv.get("DB_PORT");
        String dbName = dotenv.get("DB_NAME");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);

        // Setup HikariCP connection pool
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(user);
        ds.setPassword(password);

        DatabaseConnection dbConnection = new DatabaseConnection(ds);

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);

            System.out.println("Inserting bulk data...");

                  runSql(conn, bulkInsertUsers());
            runSql(conn, bulkInsertStudents());
            runSql(conn, bulkInsertInstructors());
            runSql(conn, bulkInsertCourses());
            runSql(conn, bulkInsertAssignments());
            runSql(conn, bulkInsertSubmissions());

            conn.commit();

            // Task 1: Test query performance before and after creating a single-column index on courses.instructor_id
            // Goal: Demonstrate how indexing dramatically speeds up queries filtering by instructor_id
            System.out.println("\nQuery before index on courses.instructor_id:");
            explainAnalyze(conn, "SELECT * FROM courses WHERE instructor_id = 500");

            System.out.println("\nCreating index on courses.instructor_id...");
            runSql(conn, "CREATE INDEX IF NOT EXISTS idx_courses_instructor ON courses(instructor_id);");
            conn.commit();

            System.out.println("\nQuery after index on courses.instructor_id:");
            explainAnalyze(conn, "SELECT * FROM courses WHERE instructor_id = 500");


            // Task 2: Demonstrate use of a compound (multi-column) index on submissions(student_id, status)
            // Compare query plans and execution times before and after the compound index creation
            // Use queries filtering by full index columns (student_id and status) to observe index usage
            System.out.println("\nQuery before compound index on submissions(student_id, status):");
            explainAnalyze(conn,
                    "SELECT * FROM submissions WHERE student_id = 123 AND status = 'submitted'");

            System.out.println("\nCreating compound index on submissions(student_id, status)...");
            runSql(conn,
                    "CREATE INDEX IF NOT EXISTS idx_submissions_student_status ON submissions(student_id, status);");
            conn.commit();

            System.out.println("\nQuery after compound index on submissions(student_id, status):");
            explainAnalyze(conn,
                    "SELECT * FROM submissions WHERE student_id = 123 AND status = 'submitted'");
        }
    }

    /**
     * Utility method to run SQL statements.
     */
    private static void runSql(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * Runs EXPLAIN ANALYZE on a query and prints the query plan and timing info.
     * Useful for comparing performance before and after index creation.
     */
    private static void explainAnalyze(Connection conn, String query) throws SQLException {
        String explainQuery = "EXPLAIN ANALYZE " + query;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(explainQuery)) {

            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        }
    }

    /**
     * Bulk insert users: generates 1 million users with roles STUDENT or INSTRUCTOR
     */
    private static String bulkInsertUsers() {
        return
                "INSERT INTO users (user_name, first_name, last_name, email, user_role, password_hash) " +
                        "SELECT 'user' || gs, 'First' || gs, 'Last' || gs, 'user' || gs || '@example.com', " +
                        "(CASE WHEN gs % 10 = 0 THEN 'INSTRUCTOR' ELSE 'STUDENT' END)::user_role, " +
                        "md5(random()::text) " +
                        "FROM generate_series(1, 1000000) gs;";
    }

    /**
     * Bulk insert students referencing users with STUDENT role
     */
    private static String bulkInsertStudents() {
        return
                "INSERT INTO students (user_id, progress_percentage, completed_courses, current_courses) " +
                        "SELECT user_id, random() * 100, (random() * 10)::int, (random() * 5)::int " +
                        "FROM users WHERE user_role = 'STUDENT';";
    }

    /**
     * Bulk insert instructors referencing users with INSTRUCTOR role
     */
    private static String bulkInsertInstructors() {
        return
                "INSERT INTO instructors (user_id, bio, expertise, total_courses_created, rating, is_verified) " +
                        "SELECT user_id, 'Bio for instructor ' || user_id, 'Expertise area ' || (user_id % 20), " +
                        "(random()*100)::int, round((random()*5)::numeric, 2), (random() > 0.5) " +
                        "FROM users WHERE user_role = 'INSTRUCTOR';";
    }

    /**
     * Bulk insert courses with 2 million rows, assigning instructors in a distributed manner
     */
    private static String bulkInsertCourses() {
        return """
            INSERT INTO courses (title, description, category, url, instructor_id)
            SELECT
                'Course ' || gs,
                'Description for course ' || gs,
                'Category ' || ((gs % 50) + 1),
                'http://example.com/course/' || gs,
                i.user_id
            FROM generate_series(1, 2000000) gs
            JOIN (
                SELECT user_id FROM instructors
            ) AS i
            ON (gs % (SELECT COUNT(*) FROM instructors)) = (i.user_id % (SELECT COUNT(*) FROM instructors));
        """;
    }

    /**
     * Bulk insert assignments, linking them to courses with a limit of 100k rows
     */
    private static String bulkInsertAssignments() {
        return
                "INSERT INTO assignments (title, description, due_date, max_score, course_id)\n" +
                        "SELECT\n" +
                        "    'Assignment ' || gs,\n" +
                        "    'Description for assignment ' || gs,\n" +
                        "    CURRENT_DATE + (gs % 30),\n" +
                        "    100,\n" +
                        "    c.id\n" +
                        "FROM generate_series(1, 100000) gs\n" +
                        "JOIN courses c ON (gs % (SELECT COUNT(*) FROM courses)) = (c.id % (SELECT COUNT(*) FROM courses));";
    }

    /**
     * Bulk insert submissions linking students to assignments, limited to 100k rows for performance
     */
    private static String bulkInsertSubmissions() {
        return
                "INSERT INTO submissions (assignment_id, student_id, content_link, status)\n" +
                        "SELECT DISTINCT\n" +
                        "    a.id AS assignment_id,\n" +
                        "    s.user_id AS student_id,\n" +
                        "    'http://example.com/submission/' || a.id || '/' || s.user_id AS content_link,\n" +
                        "    'submitted'\n" +
                        "FROM assignments a\n" +
                        "JOIN students s ON (s.user_id % 1000) = (a.course_id % 1000)\n" +
                        "LIMIT 100000; -- limit to reasonable number of submissions;";
    }
}

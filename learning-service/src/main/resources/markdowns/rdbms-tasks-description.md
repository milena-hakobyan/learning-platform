## Demo #1: With vs Without Transactions

For the demo, I used two tables from my database:

- `enrollments` — stores which user is enrolled in which course
- `activity_logs` — records user actions (e.g., "enrolled in course")

In each simulation, two actions were performed:

1. Inserting an enrollment into the `enrollments` table (should succeed)
2. Inserting a faulty activity log with a `null` action (will fail due to a NOT NULL constraint in the database)

---

### Without Transactions

Each database statement runs independently and is immediately committed.  
If the second insert fails, the first insert still remains in the database.

This can leave inconsistent data.  
**In our case — the user appears enrolled, but there's no corresponding activity log.**

---

### With Transactions

All operations are run within a single transaction block.  
If any operation fails, everything is rolled back — nothing is saved to the database.

This ensures data consistency: either all changes are applied, or none are.  
**So the user is not enrolled if an error occurs during the insertion of the corresponding activity log.** <br><br><br><br><br><br>






## Demo #2: Lost Updates demo

For the demo, I used the `students` table, focusing on the `completed_courses` field for a specific student.

Two threads perform these actions concurrently:

1. Read the current value of `completed_courses`
2. Update `completed_courses` by adding a different number

---

### Using Default Isolation Level (READ COMMITTED)

Both threads read the same original value (e.g., 5).  
They each calculate their update independently and write back.

The transaction that commits later overwrites the earlier update, causing a **lost update**.

---

### Using REPEATABLE READ or SERIALIZABLE Isolation Level

The database detects the conflict and rolls back one of the transactions  
(e.g., via a deadlock or serialization error).

This prevents lost updates but requires the application to catch exceptions and retry. <br><br><br><br><br><br>


## Demo #3: Query Optimization Using Indexes

For this demo, I used the `courses` table, which includes a foreign key `instructor_id` pointing to the `instructors` table.

I inserted **2 million rows** into `courses` with various `instructor_id` values.

The goal was to compare the performance of a query **with and without an index** on the `instructor_id` column.

---

### Query Performance Analysis Using EXPLAIN ANALYZE

Before creating the index, PostgreSQL used a **parallel sequential scan** on the `courses` table and the execution time was about **76ms**

This means it scanned many rows to filter by `instructor_id = 500`, which is slow for large tables.

After creating the index on `instructor_id`, PostgreSQL used a **bitmap index scan** with **0.038ms**.
This allows fast row retrieval using the index, making the query almost 2000 times faster.



**Sooo...**  
Without an index, queries on large tables can be very slow. Creating an index on a commonly filtered column dramatically improves query speed by enabling efficient index scans. <br><br><br><br><br><br>


## Demo #4: Query Optimization Using Compound Index on Submissions

For this demo, I used the `submissions` table and tested queries filtering by `student_id` and `status` (e.g., `'submitted'`).

---

### Query Performance Analysis Using EXPLAIN ANALYZE

Before creating the compound index on `(student_id, status)`, PostgreSQL performed a **sequential scan** on the `submissions` table.

- Execution time was about **6.6 ms**, scanning all 100,000 rows to filter matching submissions.

After creating the compound index on `(student_id, status)`, PostgreSQL used a **bitmap index scan**:

- Execution time dropped to **0.022 ms**, with a fast index lookup filtering exactly matching rows.

---

**Conclusion:**  
Adding a compound index on multiple frequently filtered columns can drastically improve query performance, especially when filtering by all columns in the index. This reduces full table scans and leverages efficient bitmap index scans to quickly locate relevant rows.

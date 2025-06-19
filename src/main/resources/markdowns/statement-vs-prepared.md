# Difference Between Statement and PreparedStatement

## Statement

- `Statement` is an interface in the `java.sql` library used to execute SQL queries.
- The queries must be constructed as complete strings before execution, which means both the SQL logic and input values are combined into one plain-text command.
- This approach is simple but exposes the application to significant risks.

## PreparedStatement

- `PreparedStatement` extends `Statement` and supports **parameterized queries**.
- Instead of embedding values directly into the SQL string, placeholders (`?`) are used.
- Values are supplied at runtime using setter methods like `setInt()`, `setString()`, etc.
- This allows the same query structure to be reused with different values without recompiling the SQL statement each time, improving efficiency.

## Security Considerations

### SQL Injection Risk with Statement

- Using `Statement` introduces a high risk of **SQL injection**, as user input is concatenated directly into the SQL query.
- A hacker can exploit this to:
    - Inject malicious SQL code,
    - Gain unauthorized access to sensitive data,
    - Bypass authentication,
    - Modify or delete database contents.

### Protection with PreparedStatement

- `PreparedStatement` mitigates SQL injection risks by **separating SQL logic from user input**.
- The query is precompiled with placeholders (`?`), and actual values are bound securely using setter methods.
- The underlying JDBC driver handles **sanitizing** parameter values to ensure inputs are treated as data, not executable code.
- This makes `PreparedStatement` a **safer and more efficient** choice for executing SQL queries in Java applications.

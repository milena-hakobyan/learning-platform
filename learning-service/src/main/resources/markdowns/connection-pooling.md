# Connection Pooling in Java

Connection pooling is a technique used to manage and reuse database connections efficiently. 
Instead of opening and closing a connection for every database operation, a pool of pre-established connections 
is maintained and reused across requests.

Creating a database connection takes time because it involves talking to the database over the network and 
setting up a session. 
Without pooling, opening and closing connections often can slow down your app and overload the database.

Connection pooling addresses this by:
- Reusing connections
- Limiting the maximum number of concurrent connections
- Reducing response time

## Advantages

- **Improved performance**: Avoids repeated connection setup, using multiple connections at a time gives a speed-up.
- **Resource efficiency**: Limits total DB connections.
- **Scalability**: Handles high concurrency better.
- **Configurable**: Timeout, idle time, validation, and retry settings.

## Disadvantages

-  **Configuration complexity**: Needs tuning for max size, timeouts, etc.
-  **Improper usage risks leaks**: Forgetting to close a connection returns it to the pool *in use*.
-  **Overhead for simple apps**: Might be overkill for small, low-traffic programs.

## Common Java Connection Pools
- [HikariCP] which is a default in Spring Boot
- [Apache DBCP]
- [C3P0] 


## Example

In our test, running with a single connection data source took about 14 seconds. Using connection pooling made it roughly 3 times faster, reducing the time to around 4–5 seconds.
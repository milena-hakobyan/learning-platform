# Use OpenJDK 21
FROM openjdk:21

# Working directory
WORKDIR /app

# Copy jar
COPY target/naming-server-1.0-SNAPSHOT.jar app.jar

# Run the jar
CMD ["java", "-jar", "app.jar"]

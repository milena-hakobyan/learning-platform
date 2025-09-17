# Use a lightweight OpenJDK 21 base image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR from the target folder to the container
COPY target/learning-service-1.0-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot app will run on
EXPOSE 8082

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
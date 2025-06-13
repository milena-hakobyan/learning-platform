# Using openjdk 21 as the base image
FROM openjdk:21

# Creating and new app directory for my application files and setting it up as my working directory
WORKDIR /app

# Copy the app files from my machine to image files
# used maven to package the app into a jar file, then copied it inside the image
COPY target/learning-platform-service-1.0-SNAPSHOT.jar app.jar

# Run the JAR file
CMD ["java", "-jar", "app.jar"]
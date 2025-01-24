# Use an OpenJDK base image
FROM openjdk:21-jdk-alpine

# Set the working directory inside the container
WORKDIR /src

# Copy the Spring Boot application JAR file into the container
COPY target/app.jar /app/app.jar

# Expose the application port (default for Spring Boot is 8080)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

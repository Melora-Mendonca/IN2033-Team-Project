# Use Java runtime
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy your JAR file (use the exact name you see)
COPY IN2033_Team_Project.jar app.jar

# Expose the port for REST API
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
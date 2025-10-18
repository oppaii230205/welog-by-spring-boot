# Use Java 21 runtime
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Create uploads directory structure with proper permissions
RUN mkdir -p /app/uploads/img/users && \
    mkdir -p /app/uploads/img/posts && \
    chmod -R 755 /app/uploads

# Copy the JAR file from your build context into the container
COPY build/libs/*.jar app.jar

# Expose port 8080 (Spring Boot default)
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]

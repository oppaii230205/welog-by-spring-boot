# Multi-stage build: Stage 1 - Build the application
FROM eclipse-temurin:21-jdk AS builder

# Set working directory
WORKDIR /build

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Copy source code
COPY src src

# Make gradlew executable
RUN chmod +x ./gradlew

# Build the application (skip tests for faster builds)
RUN ./gradlew clean bootJar -x test

# Stage 2 - Run the application
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy the JAR file from builder stage
COPY --from=builder /build/build/libs/*.jar app.jar

# Expose port 8080 (Spring Boot default)
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]

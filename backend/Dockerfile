# Multi-stage build for Military Asset Management System (MAMS) Backend

# Stage 1: Build stage with Maven and OpenJDK 17
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build production jar skipping test execution
RUN mvn clean package -DskipTests

# Stage 2: Minimal, secure runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Run as a non-privileged system user for container security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser:appgroup

# Copy executable jar from builder stage
COPY --from=builder --chown=appuser:appgroup /app/target/*.jar app.jar

# Render default port fallback (Render assigns dynamic PORT at runtime)
ENV PORT=10000
EXPOSE 10000

# Execute Spring Boot application bound to 0.0.0.0 and dynamic PORT
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-10000} -Dserver.address=0.0.0.0 -Djava.security.egd=file:/dev/./urandom -jar app.jar"]

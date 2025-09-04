# Multi-stage build
FROM maven:3.9-eclipse-temurin-17 AS builder

# Set working directory
WORKDIR /app

# First, build nexhub-common library
COPY nexhub-common/pom.xml nexhub-common/
COPY nexhub-common/.mvn nexhub-common/.mvn/
COPY nexhub-common/mvnw* nexhub-common/
COPY nexhub-common/src nexhub-common/src/
RUN cd nexhub-common && mvn clean install -DskipTests

# Copy pom.xml and download dependencies (for better caching)
COPY blog-service/pom.xml .
RUN mvn dependency:resolve

# Copy source code
COPY blog-service/src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Create a non-root user
RUN addgroup --system --gid 1001 spring && \
    adduser --system --uid 1001 --gid 1001 spring

# Change ownership of the app directory
RUN chown -R spring:spring /app
USER spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
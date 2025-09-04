# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY discovery-service/pom.xml .
COPY discovery-service/src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy the jar file from build stage
COPY --from=build /app/target/discovery-service-*.jar app.jar

# Expose port
EXPOSE 8761

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
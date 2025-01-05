# Stage 1: Build Stage
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Set working directory inside the container
WORKDIR /app

# Copy only pom.xml for dependency caching
COPY pom.xml .

# Download dependencies to cache them
RUN mvn dependency:go-offline -B

# Run tests (unit, integration) to ensure build passes
CMD ["mvn", "test"]
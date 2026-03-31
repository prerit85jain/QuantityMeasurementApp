# ── Stage 1: Build ──────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml first so Docker caches the dependency layer
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build the JAR (skip tests for faster deploys)
COPY src ./src
RUN mvn clean package -DskipTests -q

# ── Stage 2: Run ─────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy only the built JAR from Stage 1
COPY --from=build /app/target/quantity-measurement-app-1.0.0.jar app.jar

# Render injects PORT env variable (default 8080)
EXPOSE 8080

# Run with the 'prod' profile so application-prod.properties is loaded
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]

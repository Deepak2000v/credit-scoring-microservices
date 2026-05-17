# ─── Stage 1: Build the JAR using Maven ───────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ─── Stage 2: Run the JAR in a slim JRE image ─────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy built JAR from Stage 1
COPY --from=builder /app/target/CreditScoringServiceMS-0.0.1-SNAPSHOT.jar app.jar

# Expose the port this service listens on
EXPOSE 8082

# Start the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
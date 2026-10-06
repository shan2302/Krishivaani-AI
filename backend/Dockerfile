# ----------Stage 1: Build the application ---------
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy Maven wrapper and pom.xml first ( layer cache - only re-downloads deps if pom changes )
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline -q

# Copy source and build ( skip tests - tests run in CI, not in Docker build )
COPY src ./src
RUN ./mvnw package -DskipTests -q

# ----------Stage 2: Run the application ---------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy only the final JAR from the build stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

#Run the application - env vars [DB_URL, DB_USERNAME, DB_PASSWORD] are passed at runtime
ENTRYPOINT ["java", "-jar", "app.jar"]
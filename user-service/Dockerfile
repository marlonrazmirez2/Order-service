# Dockerfile for User Service

# Build stage
FROM eclipse-temurin:17-jdk as build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar /app/user-service.jar
EXPOSE 8081
CMD ["java", "-jar", "/app/user-service.jar"]
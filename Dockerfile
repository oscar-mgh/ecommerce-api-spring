FROM maven:3-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Create the final, lightweight image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy the generated JAR file from the 'builder' stage
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

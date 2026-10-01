# Build Stage
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY music-shared-kernel/pom.xml music-shared-kernel/
COPY music-domain/pom.xml music-domain/
COPY music-application/pom.xml music-application/
COPY music-infrastructure/pom.xml music-infrastructure/
COPY music-persistence/pom.xml music-persistence/
COPY music-web/pom.xml music-web/

# Go offline to cache dependencies
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY . .
RUN mvn clean package -DskipTests

# Run Stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/music-web/target/*.war app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war"]

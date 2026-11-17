# Build da aplicação Spring Boot com Java 17, conforme definido no pom.xml.
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package

# Imagem de execução sem Maven e sem código-fonte.
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/CodeCamp-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Фаза сборки
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY . ./
RUN mvn clean package -DskipTests

# Фаза запуска
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/QAPostgresPro-1.0-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
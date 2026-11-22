#FROM openjdk:21
#EXPOSE 8080
#ADD /target/spring-shopping-cart-0.0.1-SNAPSHOT.jar spring-shopping-cart.jar
#ENTRYPOINT ["java", "-jar", "spring-shopping-cart.jar"]

# Stage 1: Build JAR
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Production Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Activate the prod profile automatically inside the container
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080
ENTRYPOINT ["java", "-Xmx350m", "-Xss512k", "-XX:+UseContainerSupport", "-jar", "app.jar"]
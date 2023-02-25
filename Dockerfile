FROM maven:3.8.7-eclipse-temurin-11 as builder

COPY src /app/src
COPY pom.xml /app

RUN mvn -f /app/pom.xml clean package

FROM openjdk:11
VOLUME /tmp

COPY --from=builder /app/target/f1livetiming.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
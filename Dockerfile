FROM openjdk:17-bullseye

WORKDIR /app
COPY ./gradle ./gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN ./gradlew jar

ENV ROUTER=http
CMD ["java", "-jar", "build/libs/oop-sandbox-app-1.0-SNAPSHOT.jar", "index", "list"]

FROM gradle:7-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM openjdk:17
EXPOSE 5001:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/
ENTRYPOINT ["java", "-jar", "/app/chessclub-api-all.jar"]

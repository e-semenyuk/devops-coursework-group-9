# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B clean verify

FROM eclipse-temurin:17-jre
WORKDIR /app

RUN useradd --system --uid 10001 appuser
COPY --from=build /workspace/target/population-reporting-system.jar /app/app.jar

USER appuser
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

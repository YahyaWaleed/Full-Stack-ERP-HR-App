# syntax=docker/dockerfile:1
# build (the Maven repository is a BuildKit cache, so rebuilds don't download everything again)
FROM eclipse-temurin:25-jdk AS build
WORKDIR /src
COPY .mvn .mvn
COPY mvnw pom.xml checkstyle.xml ./
COPY src src
# formatting/style checks run in development and CI (./mvnw verify), not in the image build
RUN --mount=type=cache,target=/root/.m2 \
    chmod +x mvnw && ./mvnw -q -B -DskipTests -Dspotless.check.skip=true -Dcheckstyle.skip=true package \
    && cp target/*.jar app.jar

# run (JRE only, non-root)
FROM eclipse-temurin:25-jre
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/* \
    && useradd --system --uid 10001 app
WORKDIR /app
COPY --from=build /src/app.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]

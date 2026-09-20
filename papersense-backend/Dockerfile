FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /workspace

COPY . .
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew && ./gradlew bootJar --no-daemon -Dorg.gradle.jvmargs="-Xmx512m" -x test

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /workspace/build/libs/papersense-backend.jar /app/papersense-backend.jar
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN sed -i 's/\r$//' /docker-entrypoint.sh && chmod +x /docker-entrypoint.sh

EXPOSE 8080
ENTRYPOINT ["/docker-entrypoint.sh"]

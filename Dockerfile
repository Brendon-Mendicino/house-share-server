FROM gradle:8.14.3-jdk24 AS builder
WORKDIR /home/gradle/project
COPY . .
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:24-jre-noble
WORKDIR /app
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

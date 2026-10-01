FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests && \
    cp $(ls target/*.jar | grep -v 'original' | head -n 1) target/app.jar

FROM gcr.io/distroless/java17-debian12:nonroot
WORKDIR /app
COPY --from=build /app/target/app.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
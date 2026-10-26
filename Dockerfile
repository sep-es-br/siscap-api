FROM eclipse-temurin:17-jdk-jammy
COPY . .
ARG APP_VERSION=Desenvolvimento
ENV APP_VERSION=${APP_VERSION}
ARG PROFILE
ENV PROFILE=${PROFILE}
RUN chmod +x ./mvnw
RUN ./mvnw clean install -DskipTests
ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}" , "-jar", "target/siscap-0.0.1-SNAPSHOT.jar"]
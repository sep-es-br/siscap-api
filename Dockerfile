FROM eclipse-temurin:17-jdk-jammy
COPY . .
ARG PROFILE
ENV PROFILE=${PROFILE}
RUN chmod +x ./mvnw
RUN ./mvnw clean install -DskipTests
ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}" , "-jar", "target/siscap-0.0.1-SNAPSHOT.jar"]
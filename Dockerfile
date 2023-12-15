FROM eclipse-temurin:17-jdk-jammy
COPY build/libs/validation-server-0.0.1-SNAPSHOT.jar validation-server-1.0.0.jar
ENTRYPOINT ["java","-jar","/validation-server-1.0.0.jar"]
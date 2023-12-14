FROM openjdk:8-jdk-alpine
COPY build/libs/demo-0.0.1-SNAPSHOT.jar validation-server-1.0.0.jar
ENTRYPOINT ["java","-jar","/validation-server-1.0.0.jar"]
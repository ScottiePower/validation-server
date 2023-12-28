FROM eclipse-temurin:21-jdk-jammy
COPY build/libs/validation-server-20232812.032720.jar validation-server-20232812.032720.jar
ENTRYPOINT ["java","-jar","/validation-server-20232812.032720.jar"]
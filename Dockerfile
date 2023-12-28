FROM eclipse-temurin:21-jdk-jammy
COPY build/libs/validation-server-20232712.043825.jar validation-server-20232712.043825.jar
ENTRYPOINT ["java","-jar","/validation-server-20232712.043825.jar"]
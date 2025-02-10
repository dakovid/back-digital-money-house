FROM openjdk:11-jdk-slim

WORKDIR /app

COPY target/back-digital-money-house-0.0.1.jar .

EXPOSE 8080

ENTRYPOINT ["java","-jar","back-digital-money-house-0.0.1.jar"]


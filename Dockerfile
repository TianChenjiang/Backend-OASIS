#FROM openjdk:8-jdk-alpine
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]

FROM maven:3-alpine

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

COPY init.sh ./init.sh
COPY mongo/mongo_papers.json ./mongo_papers.json

RUN apk update
RUN apk add mongodb-tools

ENTRYPOINT ["sh", "init.sh"]
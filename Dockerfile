#FROM openjdk:17.0.2-slim-buster AS build
FROM alpine:latest
RUN apk update && apk add --no-cache openjdk17-jre-headless maven

WORKDIR usr/src/app
COPY . ./

RUN mvn clean dependency:resolve
RUN mvn package

# ARG JAR_NAME="spring-boot-docker-complete-0.0.1-SNAPSHOT"

EXPOSE 8080

#COPY /usr/src/app/target/${JAR_NAME}.jar ./app.jar
ENTRYPOINT ["java","-jar", "/usr/src/app/target/paceservices-0.0.5.jar"]
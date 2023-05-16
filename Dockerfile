# Build stage
#FROM maven:3.6.3-jdk-17-slim AS build
#COPY src /home/app/src
#COPY pom.xml /home/app
#RUN mvn -f /home/app/pom.xml clean test package
#
## Package stage
#FROM openjdk:17-jdk-alpine
#COPY --from=build /home/app/target/*.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java","-jar","app.jar"]
FROM amazoncorretto:17-alpine-jdk
#VOLUME /tmp
#EXPOSE 8080 3306
MAINTAINER joshy56
ADD target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
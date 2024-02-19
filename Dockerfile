#FROM amazoncorretto:17-alpine-jre
FROM eclipse-temurin:17-jdk-focal
MAINTAINER fgmjunior
LABEL authors="fernando"
COPY target/rinha-spring-demo-0.0.1-SNAPSHOT.jar rinha-spring-demo-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar", "/rinha-spring-demo-0.0.1-SNAPSHOT.jar"]

#COPY /home/fernando/glowroot/glowroot.jar glowroot.jar
#ENTRYPOINT ["java"," -javaagent:/glowroot.jar", "-jar","/rinha-spring-demo-0.0.1-SNAPSHOT.jar"]


#https://www.docker.com/blog/kickstart-your-spring-boot-application-development/
#docker build --platform linux/amd64 -t rinha-spring-demo:latest .
#FROM eclipse-temurin:17-jdk-focal
#WORKDIR /app
#COPY .mvn/ .mvn
#COPY mvnw pom.xml ./
#RUN ./mvnw dependency:go-offline

#COPY src ./src

#CMD ["./mvnw", "spring-boot:run"]
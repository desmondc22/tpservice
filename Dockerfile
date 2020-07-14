FROM gradle:6.4.1-jdk8 AS build
COPY . /home/source/java
WORKDIR /home/source/java
USER root
RUN chown -R gradle /home/source/java
USER gradle
RUN gradle clean build


FROM openjdk:8-jdk-alpine
WORKDIR /home/application/java
COPY --from=build "/home/source/java/build/libs/tpservice-0.1.0.jar" .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/home/application/java/tpservice-0.1.0.jar"]
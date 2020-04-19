FROM openjdk:11-jre
RUN mkdir app
ARG JAR_FILE
ADD /target/dynamic.command-0.0.1-SNAPSHOT.jar /app/dynamic.jar
WORKDIR /app
ENTRYPOINT java -jar dynamic.jar
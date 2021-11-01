FROM adoptopenjdk/openjdk11:jdk-11.0.2.9-slim
LABEL maintainer="basiljereh@gmail.com"
ENV PORT 9090
COPY target/*.jar /opt/app.jar
WORKDIR /opt
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar

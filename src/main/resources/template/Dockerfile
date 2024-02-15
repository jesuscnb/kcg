FROM openjdk:17-jdk-alpine

ENV WORKSPACE /opt

EXPOSE 3000

WORKDIR $WORKSPACE

RUN mkdir -p $WORKSPACE

COPY target/*-jar-with-dependencies.jar $WORKSPACE/app.jar

ENTRYPOINT ["java","-Dfile.encoding=UTF-8","-jar","app.jar"]

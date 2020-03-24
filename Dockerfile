FROM openjdk:8-jdk-alpine
ENV MONGO="mongodb://47.101.33.219:27017,101.37.175.237:27017,116.62.23.105:27017/?replicaSet=rs0/oasis"
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

#FROM registry.cn-hangzhou.aliyuncs.com/acs/maven:3-jdk-8
#
#ENV HOME=/home/usr/app
#
#RUN mkdir -p $HOME
#
#WORKDIR $HOME
#
## 1. add pom.xml only here
#
#ADD pom.xml $HOME
#
## 2. start downloading dependencies
#
#RUN ["/usr/local/bin/mvn-entrypoint.sh", "mvn", "verify", "clean", "--fail-never"]
#
## 3. add all source code and start compiling
#
#ADD . $HOME
#
#RUN mvn -B -DskipTests clean package
#
#CMD ["java", "-jar", "target/app.jar"]
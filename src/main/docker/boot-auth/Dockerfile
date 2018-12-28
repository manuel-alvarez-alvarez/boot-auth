FROM openjdk:8-jdk-alpine
EXPOSE 8080
ADD *.jar boot-auth.jar
ENTRYPOINT java $JAVA_OPTS $JWPD_OPTS -jar /boot-auth.jar

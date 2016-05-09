FROM java:openjdk-8-jdk
ADD docker-client-1.0-SNAPSHOT.jar client.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/client.jar"]

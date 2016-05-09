FROM java:8
ADD docker-client-mvn-1.0-SNAPSHOT.jar client.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/client.jar"]

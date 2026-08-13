FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/config-server-0.0.1-SNAPSHOT.jar app.jar
COPY ../config-repo /config-repo
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
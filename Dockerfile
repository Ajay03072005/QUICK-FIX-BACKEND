FROM eclipse-temurin:17-jre

WORKDIR /app
COPY target/Quick-fix-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8082} -jar /app/app.jar"]

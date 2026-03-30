FROM eclipse-temurin:21-jre

# Copy app
COPY target/*.jar app.jar

# Copy OTel agent
COPY opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar


ENTRYPOINT ["java", "-javaagent:/otel/opentelemetry-javaagent.jar", "-jar", "/app.jar"]


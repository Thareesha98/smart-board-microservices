FROM eclipse-temurin:21-jre

# Copy app
COPY target/*.jar app.jar

# Copy OTel agent
COPY opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar

ENV OTEL_SERVICE_NAME=sbms-notification-service
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4317
ENV OTEL_EXPORTER_OTLP_PROTOCOL=grpc
ENV OTEL_RESOURCE_ATTRIBUTES=service.namespace=sbms,service.version=1.0

ENTRYPOINT ["java", "-javaagent:/otel/opentelemetry-javaagent.jar", "-jar", "/app.jar"]


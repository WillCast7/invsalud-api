# Stage 1: Build
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN apt-get update && apt-get install -y dos2unix && dos2unix mvnw
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copiamos el jar generado (ajusta si tu jar tiene un nombre específico)
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
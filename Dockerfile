# Imagen base para compilar (con JDK)
FROM eclipse-temurin:23-jdk AS builder

WORKDIR /app

COPY . .

RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:23-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

# Copiar el archivo .env
COPY .env .env

# Instalar utilidades, convertir .env a formato Unix y habilitar TLS 1.0
RUN apt-get update && apt-get install -y dos2unix && \
    dos2unix .env && \
    sed -i 's/TLSv1, TLSv1.1,//g' $JAVA_HOME/conf/security/java.security && \
    rm -rf /var/lib/apt/lists/*

EXPOSE 9090

# Cargar variables y ejecutar con TLS 1.0 habilitado
ENTRYPOINT ["sh", "-c", "export $(grep -v '^#' .env | xargs) && java \
    -Djdk.tls.client.protocols=TLSv1 \
    -Dhttps.protocols=TLSv1 \
    -jar app.jar"]

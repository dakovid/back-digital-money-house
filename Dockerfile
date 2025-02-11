# Etapa de compilación con Maven y Java 17
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copiar archivos del proyecto
COPY . .

# Compilar el proyecto sin ejecutar pruebas
RUN mvn clean package -DskipTests

# Etapa de ejecución con Java 17
FROM eclipse-temurin:17-jdk-focal

WORKDIR /app

# Copiar el JAR generado
COPY --from=builder /app/target/back-digital-money-house-0.0.1.jar app.jar

# Exponer el puerto de la aplicación
EXPOSE 8080

# Variables de entorno para mejorar rendimiento
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Usar tini para mejor manejo de procesos en Docker
RUN apt-get update && apt-get install -y tini && rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["/usr/bin/tini", "--"]

CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

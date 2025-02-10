
# Back Digital Money House

## Descripción
Back Digital Money House es una API REST desarrollada en **Java + Spring Boot** para gestionar transacciones digitales de manera eficiente y segura.

---

## 🚀 Cómo ejecutar localmente

### 1️⃣ Requisitos previos
- Java 17 o superior
- Maven 3+
- Docker (opcional, para ejecutar en contenedores)

### 2️⃣ Configuración del archivo `.env`
Antes de ejecutar la aplicación, configura el archivo `.env` basado en `.env.example`:
```ini
DATABASE_URL=jdbc:mysql://localhost:3306/digitalmoneyhouse
DATABASE_USER=root
DATABASE_PASSWORD=secret
JWT_SECRET=tu_secreto_seguro
PORT=8085
```

---

## 🔧 Comandos Maven
Ejecutar la aplicación:
```bash
mvn spring-boot:run
```
Compilar el proyecto:
```bash
mvn clean package
```
Ejecutar pruebas:
```bash
mvn test
```

---

## 🐳 Comandos Docker
Construir la imagen:
```bash
docker build -t digitalmoneyhouse .
```
Ejecutar el contenedor:
```bash
docker run -p 8085:8080 --env-file .env digitalmoneyhouse
```
Ejecutar con `docker-compose`:
```bash
docker-compose up -d
```

Nota: La aplicación estará disponible en el puerto 8085 del host (mapeando el 8080 del contenedor).

---

## 📜 Swagger (Documentación API)
Una vez que la aplicación esté corriendo, accede a Swagger en:
```
http://localhost:8085/swagger-ui/
```

---

## 🧪 Pruebas Unitarias
Ejecutar pruebas unitarias con Maven:
```bash
mvn test
```

---

✅ Proyecto listo para desarrollo y despliegue en Docker o ambiente local.


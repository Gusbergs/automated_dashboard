# === React build ===
FROM node:16 AS frontend
WORKDIR /app
COPY frontend/ ./frontend
WORKDIR /app/frontend
RUN npm install && npm run build

# === Spring Boot build ===
FROM eclipse-temurin:21 AS backend
WORKDIR /app
COPY backend/ ./backend
COPY --from=frontend /app/frontend/build/ ./backend/src/main/resources/static/
WORKDIR /app/backend
RUN ./gradlew build -x test

# === Run container ===
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=backend /app/backend/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

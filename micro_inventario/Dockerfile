# Imagen base con Java 17
FROM eclipse-temurin:17-jdk

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el JAR generado por Maven
COPY target/micro_inventario-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto del microservicio
EXPOSE 8082

# Comando de arranque
ENTRYPOINT ["java","-jar","app.jar"]




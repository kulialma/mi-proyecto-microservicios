#!/bin/bash

# Script rush para compilar, actualizar y arrancar todo

echo "🐳 Levantando Docker y PostgreSQL..."
docker compose up -d postgres

echo "⏳ Esperando 10 segundos para que Postgres arranque..."
sleep 10

echo "🧹 Limpiando compilaciones anteriores..."
rm -rf micro_productos/target micro_inventario/target

echo "🛠️ Compilando micro_productos..."
cd micro_productos
mvn clean package -DskipTests

echo "▶️ Arrancando micro_productos..."
nohup java -jar target/micro_productos-0.0.1-SNAPSHOT.jar > ../micro_productos.log 2>&1 &

cd ..

echo "🛠️ Compilando micro_inventario..."
cd micro_inventario
mvn clean package -DskipTests

echo "▶️ Arrancando micro_inventario..."
nohup java -jar target/micro_inventario-0.0.1-SNAPSHOT.jar > ../micro_inventario.log 2>&1 &

cd ..

echo "✅ Rush completado:"
echo "   - micro_productos corriendo en http://localhost:8082/productos/public"
echo "   - micro_inventario corriendo en http://localhost:8081/inventario"
echo "   - PostgreSQL levantado en Docker"
echo "   - Logs en micro_productos.log y micro_inventario.log"

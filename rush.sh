#!/bin/bash

# Script rush para compilar, actualizar y arrancar todo

set -e  # detener en caso de error

echo "🐳 Levantando Docker y PostgreSQL..."
docker compose up -d postgres

echo "⏳ Esperando 10 segundos para que Postgres arranque..."
sleep 10

echo "🧹 Limpiando compilaciones anteriores..."
rm -rf micro_productos/target micro_inventario/target

echo "🛠️ Compilando micro_productos..."
cd micro_productos
mvn clean package -DskipTests
cd ..

echo "▶️ Arrancando micro_productos en puerto 8081..."
nohup java -jar micro_productos/target/micro_productos-0.0.1-SNAPSHOT.jar \
    --server.port=8081 > micro_productos.log 2>&1 &

echo "🛠️ Compilando micro_inventario..."
cd micro_inventario
mvn clean package -DskipTests
cd ..

echo "▶️ Arrancando micro_inventario en puerto 8082..."
nohup java -jar micro_inventario/target/micro_inventario-0.0.1-SNAPSHOT.jar \
    --server.port=8082 > micro_inventario.log 2>&1 &

echo "✅ Rush completado:"
echo "   - micro_productos corriendo en http://localhost:8081/productos"
echo "   - micro_inventario corriendo en http://localhost:8082/inventario"
echo "   - PostgreSQL levantado en Docker (localhost:5433, DB: microservicios_test)"
echo "   - Logs en micro_productos.log y micro_inventario.log"


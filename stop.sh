#!/bin/bash

# Detener cualquier proceso en 8081 y 8082
for port in 8081 8082; do
  pid=$(lsof -t -i :$port)
  if [ -n "$pid" ]; then
    echo "Matando proceso en puerto $port (PID $pid)..."
    kill -9 $pid
  else
    echo "No hay proceso corriendo en puerto $port"
  fi
done

echo "✅ Microservicios detenidos."


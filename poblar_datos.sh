#!/bin/bash
# ==============================================================
# Script de inicialización de datos para CentroEducativo
# Proyecto: NOL (no12526)
# ==============================================================

echo "Iniciando carga de datos en CentroEducativo (localhost:9090)..."

# 1. Login como administrador para conseguir la KEY de sesión
# Se guardan las cookies en 'sesion.txt' y extraemos la key usando 'tr' para quitar comillas
KEY=$(curl -s --data '{"dni":"111111111","password":"654321"}' \
  -X POST -H "content-type: application/json" \
  http://localhost:9090/CentroEducativo/login -c sesion.txt | tr -d '"')

echo "Login exitoso. KEY obtenida: $KEY"
echo "--------------------------------------------------------"

# 2. Añadir un Profesor
echo "Añadiendo profesor (Valderas)..."
curl -s --data '{"apellidos": "Valderas", "dni": "10293756L", "nombre": "Pedro", "password": "123"}' \
  -X POST -H "content-type: application/json" \
  "http://localhost:9090/CentroEducativo/profesores?key=$KEY" -b sesion.txt
echo -e "\n"

# 3. Añadir una Asignatura
echo "Añadiendo asignatura (Desarrollo Web)..."
curl -s --data '{"acronimo": "DEW", "nombre": "Desarrollo Web", "curso": 3, "cuatrimestre": "B", "creditos": 4.5}' \
  -X POST -H "content-type: application/json" \
  "http://localhost:9090/CentroEducativo/asignaturas?key=$KEY" -b sesion.txt
echo -e "\n"

# 4. Asignar el Profesor a la Asignatura
# Endpoint de la captura: POST /asignaturas/{acronimo}/profesores
echo "Asignando profesor Valderas a DEW..."
curl -s --data '{"dni":"10293756L"}' \
  -X POST -H "content-type: application/json" \
  "http://localhost:9090/CentroEducativo/asignaturas/DEW/profesores?key=$KEY" -b sesion.txt
echo -e "\n"

# 5. Añadir un Alumno
echo "Añadiendo alumno (Pepe Garcia)..."
curl -s --data '{"apellidos": "Garcia Sanchez", "dni": "12345678W", "nombre": "Pepe", "password": "123"}' \
  -X POST -H "content-type: application/json" \
  "http://localhost:9090/CentroEducativo/alumnos?key=$KEY" -b sesion.txt
echo -e "\n"

# 6. Matricular al alumno en la asignatura
# Endpoint de la captura: POST /alumnos/{dni}/asignaturas
echo "Matriculando a Pepe en DEW..."
curl -s --data '{"acronimo":"DEW"}' \
  -X POST -H "content-type: application/json" \
  "http://localhost:9090/CentroEducativo/alumnos/12345678W/asignaturas?key=$KEY" -b sesion.txt
echo -e "\n"

echo "--------------------------------------------------------"
echo "Carga de datos finalizada. ¡CentroEducativo está listo!"
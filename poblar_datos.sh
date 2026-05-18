#!/bin/bash

BASE_URL="http://localhost:9090/CentroEducativo"

echo "================================================="
echo "1. Haciendo login para obtener credenciales..."
echo "================================================="
# Hacemos login y guardamos la cookie en cookies.txt
KEY=$(curl -s -X POST "$BASE_URL/login" \
     -H "Content-Type: application/json" \
     -d '{"dni":"111111111", "password":"123"}' \
     -c cookies.txt | tr -d '"')
     
echo "Login exitoso. KEY obtenida: $KEY"
echo ""

echo "================================================="
echo "2. Creando profesor Valderas..."
echo "================================================="
curl -X POST "$BASE_URL/profesores?key=$KEY" -b cookies.txt \
     -H "Content-Type: application/json" \
     -d '{"dni": "10293756L", "nombre": "Pedro", "apellidos": "Valderas", "password": "123"}'
echo -e "\n"

echo "================================================="
echo "3. Creando asignatura DEW..."
echo "================================================="
curl -X POST "$BASE_URL/asignaturas?key=$KEY" -b cookies.txt \
     -H "Content-Type: application/json" \
     -d '{"acronimo": "DEW", "nombre": "Desarrollo Web", "curso": 3, "cuatrimestre": "B", "creditos": 4.5}'
echo -e "\n"

echo "================================================="
echo "4. Asignando profesor a la asignatura DEW..."
echo "================================================="
# Probamos enviándolo como texto plano (text/plain), que es lo que suelen 
# pedir estas APIs cuando Swagger espera solo un String (DNI)
curl -X POST "$BASE_URL/asignaturas/DEW/profesores?key=$KEY" -b cookies.txt \
     -H "Content-Type: text/plain" \
     -d '10293756L'
echo -e "\n"

echo "================================================="
echo "5. Creando alumno Pepe..."
echo "================================================="
curl -X POST "$BASE_URL/alumnos?key=$KEY" -b cookies.txt \
     -H "Content-Type: application/json" \
     -d '{"dni": "12345678W", "nombre": "Pepe", "apellidos": "Garcia Sanchez", "password": "123"}'
echo -e "\n"

echo "================================================="
echo "6. Matriculando alumno Pepe en DEW..."
echo "================================================="
# Probamos enviando solo el acrónimo como texto plano
curl -X POST "$BASE_URL/alumnos/12345678W/asignaturas?key=$KEY" -b cookies.txt \
     -H "Content-Type: text/plain" \
     -d 'DEW'
echo -e "\n"

echo "================================================="
echo "¡Carga de datos finalizada! Revisa las respuestas."
echo "================================================="
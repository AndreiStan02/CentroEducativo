<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Notas OnLine - Mis Asignaturas</title>
    <!-- Bootstrap 5 CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

    <!-- Cabecera principal -->
    <header class="bg-dark text-white p-5 mb-4 shadow-sm">
        <div class="container">
            <!-- Nota para backend: Este nombre tendrÃ¡ que inyectarse dinÃ¡micamente desde el Servlet -->
            <h1 class="display-5 fw-bold">Notas OnLine. Asignaturas del/la alumn@ Pepe GarcÃ­a SÃ¡nchez</h1>
            <p class="lead">En esta pÃ¡gina se muestran las asignaturas en las que estÃ¡s matriculad@. Al pulsar en una podrÃ¡s acceder a tu calificaciÃ³n.</p>
        </div>
    </header>

    <!-- Contenido Principal -->
    <main class="container">
        <div class="row g-4">
            
            <!-- Columna Izquierda: Lista de asignaturas -->
            <div class="col-md-8">
                <!-- Usamos un list-group de Bootstrap para que parezcan botones grandes -->
                <div class="list-group shadow-sm">
                    
                    <!-- FÃ­jate en el href: Apunta al futuro Servlet que mostrarÃ¡ los detalles de esa materia -->
                    <a href="DetallesAlumnoServlet?asignatura=DEW" class="list-group-item list-group-item-action p-4">
                        <h2 class="h5 mb-0 text-primary">Desarrollo Web</h2>
                    </a>
                    
                    <a href="DetallesAlumnoServlet?asignatura=IAP" class="list-group-item list-group-item-action p-4">
                        <h2 class="h5 mb-0 text-primary">IntegraciÃ³n de Aplicaciones</h2>
                    </a>
                    
                    <a href="DetallesAlumnoServlet?asignatura=DCU" class="list-group-item list-group-item-action p-4">
                        <h2 class="h5 mb-0 text-primary">Desarrollo Centrado en el Usuario</h2>
                    </a>
                    
                </div>
            </div>

            <!-- Columna Derecha: InformaciÃ³n del Grupo (Mismo bloque que en el index) -->
            <div class="col-md-4">
                <div class="card bg-white border-0 shadow-sm">
                    <div class="card-body">
                        <h3 class="card-title h5 border-bottom pb-2">Grupo 13_lab_Miercoles</h3>
                        <ol class="small text-muted mt-3">
                            <li>Salmane Elhaouzi</li>
                            <li>Alaa Ghabi</li>
                            <li>Barat Arriaza Alvaro</li>
                            <li>Stan Dima Vlad Andrei</li>
                            <li>Valles Galan Alejandra</li>
                            <li>Villar Melis Pablo</li>
                        </ol>
                    </div>
                </div>
            </div>

        </div>
    </main>

    <!-- Pie de pÃ¡gina -->
    <footer class="container mt-5 border-top pt-3 text-center small text-muted">
        <p>Trabajo en grupo realizado para la asignatura Desarrollo Web. Curso 2025-2026</p>
    </footer>

    <!-- Scripts de Bootstrap -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
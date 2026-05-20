<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Notas OnLine - Inicio</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/estilos.css" rel="stylesheet">
</head>
<body class="bg-light">

    <nav class="navbar navbar-dark bg-dark">
        <div class="container">
            <span class="navbar-brand fw-bold">📋 Notas OnLine</span>
        </div>
    </nav>

    <header class="bg-dark text-white p-5 mb-4 shadow-sm border-top border-secondary">
        <div class="container">
            <h1 class="display-4 fw-bold">Bienvenid@ a Notas OnLine</h1>
            <p class="lead">Una aplicación que cuesta más de lo que parece para conseguir menos de lo que creías... ¿¡Qué más se puede pedir!?</p>
        </div>
    </header>

    <main class="container py-4">
        <div class="row g-4">
            
            <div class="col-md-8">
                <div class="card mb-4 border-0 shadow-sm p-3">
                    <div class="card-body text-center text-md-start">
                        <div class="display-6 mb-3">🎓</div>
                        <h2 class="card-title h4 fw-bold">Si eres alumn@...</h2>
                        <p class="card-text text-muted mb-4">Podrás consultar tus calificaciones... Debes contar con tus datos identificativos para acceder.</p>
                        <!-- Se cambia el href para apuntar al recurso protegido directamente -->
                        <a href="alumno/asignaturas" class="btn btn-primary btn-lg">Consultar calificaciones</a>
                    </div>
                </div>

                <div class="card border-0 shadow-sm p-3">
                    <div class="card-body text-center text-md-start">
                        <div class="display-6 mb-3">👨‍🏫</div>
                        <h2 class="card-title h4 fw-bold">Si eres profesor@...</h2>
                        <p class="card-text text-muted mb-4">Podrás consultar o modificar las calificaciones en tus asignaturas... Debes contar con tus datos identificativos para acceder.</p>
                        <!-- Se cambia el href para apuntar al recurso protegido directamente -->
                        <a href="profesor/asignaturas" class="btn btn-outline-primary btn-lg">Consultar o modificar</a>
                    </div>
                </div>
            </div>

            <div class="col-md-4">
                <div class="card bg-white border-0 shadow-sm">
                    <div class="card-body">
                        <h3 class="card-title h5 border-bottom pb-2 fw-bold">Grupo 13_lab_Miercoles</h3>
                        <ol class="small text-muted mt-3 mb-0" style="line-height: 1.8;">
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

    <footer class="bg-dark text-white text-center py-4 mt-5 small">
        <div class="container">
            <span class="fw-bold">Grupo 13_lab_Miercoles</span> |
            Salmane Elhaouzi · Alaa Ghabi · Barat Arriaza Alvaro · Stan Dima Vlad Andrei · Valles Galan Alejandra · Villar Melis Pablo
            <br><br>
            Trabajo en grupo para la asignatura <em>Desarrollo de Entornos Web</em> — Curso 2025/2026
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

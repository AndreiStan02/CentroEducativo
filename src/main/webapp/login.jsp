<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Notas OnLine — Identificación</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/estilos.css" rel="stylesheet">
</head>
<body class="d-flex flex-column min-vh-100">

<nav class="navbar navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand fw-bold" href="index.html">Notas OnLine</a>
    </div>
</nav>

<main class="d-flex align-items-center justify-content-center flex-grow-1 py-5">
    <div class="login-card w-100" style="max-width: 400px;">
        <div class="card shadow">
            <div class="brand-header text-center pt-4">
                <h2>Notas OnLine</h2>
                <p class="text-muted">Introduce tus credenciales para acceder</p>
            </div>
            <div class="card-body p-4">
                
                <div id="error-alert" class="alert alert-danger d-none animate-fade" role="alert"></div>

                <!-- Modificado: action ahora apunta a tu servlet "acceso" -->
                <form method="POST" action="acceso">
                    <div class="mb-3">
                        <label for="username" class="form-label fw-semibold">DNI</label>
                        <input type="text" class="form-control" id="username" name="username"
                               placeholder="Ej: 12345678W" required autofocus>
                    </div>
                    <div class="mb-4">
                        <label for="password" class="form-label fw-semibold">Contraseña</label>
                        <input type="password" class="form-control" id="password" name="password"
                               placeholder="Tu contraseña" required>
                    </div>
                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary btn-lg">Acceder</button>
                    </div>
                </form>
            </div>
            <div class="card-footer text-center text-muted small py-3">
                ¿No sabes tu contraseña? Contacta con tu centro educativo.
            </div>
        </div>
        <div class="text-center mt-3">
            <a href="index.html" class="text-decoration-none text-secondary">← Volver a la página de inicio</a>
        </div>
    </div>
</main>

<footer class="bg-dark text-white text-center py-4 small mt-auto">
    <div class="container">
        <span class="fw-bold">Grupo 13_lab_Miercoles</span> |
        Salmane Elhaouzi · Alaa Ghabi · Barat Arriaza Alvaro · Stan Dima Vlad Andrei · Valles Galan Alejandra · Villar Melis Pablo
        <br>
        Trabajo en grupo realizado para la asignatura <em>Desarrollo Web</em>. Curso 2025-2026
    </div>
</footer>

<script>
    const urlParams = new URLSearchParams(window.location.search);
    const errorParam = urlParams.get('error');
    const alertEl = document.getElementById('error-alert');

    if (errorParam === '1' || errorParam === 'invalid') {
        alertEl.textContent = "Credenciales web incorrectas. Por favor, inténtalo de nuevo.";
        alertEl.classList.remove('d-none');
    } else if (errorParam === '2') {
        alertEl.textContent = "No tienes permisos suficientes para acceder a este recurso.";
        alertEl.classList.remove('d-none');
    } else if (errorParam === 'data_failed') {
        alertEl.textContent = "Error de sincronización con el nivel de datos central.";
        alertEl.classList.remove('d-none');
    }
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
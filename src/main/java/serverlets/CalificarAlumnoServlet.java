package serverlets;

import java.io.IOException;
import api.ApiClient;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class CalificarAlumnoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Validar seguridad de la sesión y rol de profesor
        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("key") == null || !"rolpro".equals(sesion.getAttribute("rol"))) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        String key = (String) sesion.getAttribute("key");
        
        // 2. Recoger los parámetros que envía el formulario del Front-End
        String dniAlumno = request.getParameter("dniAlumno");
        String acronimoAsig = request.getParameter("asignatura");
        String notaString = request.getParameter("nota");

        // Validación básica de parámetros
        if (dniAlumno == null || acronimoAsig == null || notaString == null || dniAlumno.isBlank() || acronimoAsig.isBlank() || notaString.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/profesor-asignaturas.html");
            return;
        }

        String baseUrl = getServletContext().getInitParameter("centroEducativoUrl");
        if (baseUrl == null) baseUrl = "http://localhost:9090/CentroEducativo";

        try {
            // 3. Construir el JSON con la estructura que pida vuestra API para las notas
            // Nota: Adapta los nombres de las claves ("dni", "asignatura", "nota") según vuestro Swagger
            String jsonBody = "{"
                + "\"dni\":\"" + dniAlumno.trim() + "\","
                + "\"asignatura\":\"" + acronimoAsig.trim() + "\","
                + "\"nota\":" + Double.parseDouble(notaString.trim())
                + "}";

            // 4. Construir la URL del endpoint para calificar (verifica la ruta exacta en tu Swagger)
            // Por ejemplo: POST /calificaciones?key=... o PUT /alumnos/{dni}/notas
            String urlFinal = baseUrl + "/calificaciones?key=" + key;
            
            // 5. Enviar el POST utilizando el método que añadimos en el ApiClient
            ApiClient.enviarDatosPost(urlFinal, jsonBody);
            
            // 6. Si todo va bien, refrescar la pantalla redirigiendo de vuelta al listado de alumnos de esa materia
            response.sendRedirect(request.getContextPath() + "/AlumnosAsignaturaServlet?asignatura=" + acronimoAsig);

        } catch (NumberFormatException e) {
            System.err.println("Error: La nota introducida no es un número válido.");
            response.sendRedirect(request.getContextPath() + "/login-error.html");
        } catch (Exception e) {
            System.err.println("Error al registrar la calificación en la API: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/login-error.html");
        }
    }
}
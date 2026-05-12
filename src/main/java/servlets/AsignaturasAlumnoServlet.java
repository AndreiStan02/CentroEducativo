package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// Importamos la librería GSON para manejar el JSON
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet encargado de recuperar y mostrar las asignaturas del alumno.
 * Desarrollado para el proyecto no12526 - Hito 1.
 */
public class AsignaturasAlumnoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Clase interna para representar la estructura de la asignatura que viene en el JSON
    private class Asignatura {
        String id;
        String nombre;
        String nota; // El enunciado menciona que se debe poder consultar la nota
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. GESTIÓN DE SESIÓN Y SEGURIDAD
        HttpSession session = request.getSession(false);
        String dni = request.getRemoteUser(); // Obtenemos el usuario autenticado en Tomcat
        
        // Recuperamos la 'key' de CentroEducativo guardada previamente en la sesión por el servlet Acceso
        String key = (session != null) ? (String) session.getAttribute("key") : null;

        // Si no hay autenticación o no tenemos la key de datos, redirigimos al login
        if (dni == null || key == null) {
            response.sendRedirect(request.getContextPath() + "/acceso");
            return;
        }

        // 2. OBTENCIÓN DE DATOS (CLIENTE REST)
        // Leemos la URL base del web.xml para no escribirla a fuego
        String baseUrl = getServletContext().getInitParameter("centroEducativoUrl");
        String urlFinal = baseUrl + "/alumnos/" + dni + "/asignaturas?key=" + key;

        List<Asignatura> listaAsignaturas = null;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest restRequest = HttpRequest.newBuilder()
                    .uri(URI.create(urlFinal))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> restResponse = client.send(restRequest, HttpResponse.BodyHandlers.ofString());
            
            if (restResponse.statusCode() == 200) {
                // USAMOS GSON: Convertimos el texto JSON en una lista de objetos Java 'Asignatura'
                Gson gson = new Gson();
                listaAsignaturas = gson.fromJson(restResponse.body(), new TypeToken<List<Asignatura>>(){}.getType());
            } else {
                throw new Exception("Error del servidor de datos: " + restResponse.statusCode());
            }
        } catch (Exception e) {
            enviarPantallaError(response, "No se ha podido conectar con el servidor de datos (CentroEducativo).");
            return;
        }

        // 3. GENERACIÓN DE LA INTERFAZ (HTML + BOOTSTRAP 5)
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='es'>");
        out.println("<head>");
        out.println("    <meta charset='UTF-8'>");
        out.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("    <title>Mis Asignaturas - NOL</title>");
        out.println("    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
        out.println("</head>");
        out.println("<body class='bg-light'>");

        // Barra de navegación
        out.println("<nav class='navbar navbar-expand-lg navbar-dark bg-dark shadow-sm mb-4'>");
        out.println("  <div class='container'>");
        out.println("    <a class='navbar-brand' href='#'>Notas Online (no12526)</a>");
        out.println("    <div class='navbar-text text-light'>Alumno: <strong>" + dni + "</strong></div>");
        out.println("  </div>");
        out.println("</nav>");

        out.println("<div class='container'>");
        out.println("    <div class='card shadow-sm p-4'>");
        out.println("        <h2 class='mb-4'>Expediente Académico</h2>");
        
        if (listaAsignaturas == null || listaAsignaturas.isEmpty()) {
            out.println("<div class='alert alert-warning'>No constas como matriculado en ninguna asignatura.</div>");
        } else {
            out.println("<table class='table table-hover align-middle'>");
            out.println("    <thead class='table-primary'><tr>");
            out.println("        <th>Código</th><th>Nombre de la Asignatura</th><th class='text-center'>Nota</th>");
            out.println("    </tr></thead>");
            out.println("    <tbody>");
            
            // Bucle para pintar cada asignatura que nos ha devuelto el JSON
            for (Asignatura asig : listaAsignaturas) {
                out.println("<tr>");
                out.println("    <td>" + asig.id + "</td>");
                out.println("    <td>" + asig.nombre + "</td>");
                out.println("    <td class='text-center fw-bold'>" + (asig.nota != null ? asig.nota : "N/P") + "</td>");
                out.println("</tr>");
            }
            
            out.println("    </tbody>");
            out.println("</table>");
        }

        out.println("        <div class='mt-4 d-flex justify-content-between'>");
        out.println("            <a href='#' class='btn btn-outline-secondary'>Generar Certificado</a>"); // Opción requerida
        out.println("            <a href='" + request.getContextPath() + "/logout' class='btn btn-danger'>Cerrar Sesión</a>");
        out.println("        </div>");
        out.println("    </div>");
        out.println("</div>");

        out.println("</body></html>");
    }

    /**
     * Método auxiliar para reportar errores visuales.
     */
    private void enviarPantallaError(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<html><head><link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'></head>");
        out.println("<body class='p-5'><div class='alert alert-danger mx-auto' style='max-width:500px;'>");
        out.println("<h4>Error de Sistema</h4><p>" + msg + "</p>");
        out.println("<hr><a href='javascript:history.back()' class='btn btn-danger'>Volver</a>");
        out.println("</div></body></html>");
    }
}
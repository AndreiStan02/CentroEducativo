package servlets;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// Usamos la misma librería HTTP que usan tus compañeros en Acceso.java
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

// Librería para parsear JSON
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet encargado de recuperar las asignaturas del alumno.
 * ACTÚA COMO CONTROLADOR PURO (BACKEND).
 */
public class AsignaturasAlumnoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Clase (POJO) para mapear los datos del JSON de CentroEducativo
    public class Asignatura {
        public String id;
        public String nombre;
        public String nota;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. GESTIÓN DE SESIÓN Y SEGURIDAD
        HttpSession session = request.getSession(false);
        String dni = request.getRemoteUser(); // Obtenemos el DNI autenticado en Tomcat
        
        // Recuperamos la 'key' guardada por tus compañeros en Acceso.java
        String key = (session != null) ? (String) session.getAttribute("key") : null;

        // Si no está logueado o falta la key, lo devolvemos a la pantalla de acceso
        if (dni == null || key == null) {
            response.sendRedirect(request.getContextPath() + "/acceso");
            return;
        }

        // 2. OBTENCIÓN DE DATOS (Con Apache HttpClient 5)
        String baseUrl = getServletContext().getInitParameter("centroEducativoUrl");
        String urlFinal = baseUrl + "/alumnos/" + dni + "/asignaturas?key=" + key;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            
            HttpGet getRequest = new HttpGet(urlFinal);
            getRequest.setHeader("Accept", "application/json");

            try (CloseableHttpResponse res = httpClient.execute(getRequest)) {
                
                if (res.getCode() == 200) {
                    // Extraemos el JSON crudo
                    String jsonResponse = EntityUtils.toString(res.getEntity(), "UTF-8");

                    // 3. PARSEAR EL JSON CON GSON
                    Gson gson = new Gson();
                    List<Asignatura> listaAsignaturas = gson.fromJson(jsonResponse, new TypeToken<List<Asignatura>>(){}.getType());

                    // 4. PASAR DATOS A LA VISTA (FRONTEND)
                    // Guardamos la lista en la petición con el nombre "listaAsignaturas"
                    request.setAttribute("listaAsignaturas", listaAsignaturas);

                    // Cedemos el control al archivo JSP que haya hecho el equipo de frontend
                    // IMPORTANTE: Asegúrate con tu equipo de frontend que el archivo se llame así
                    request.getRequestDispatcher("/vista_asignaturas.jsp").forward(request, response);

                } else {
                    // Manejo de error si CentroEducativo devuelve otro código (ej: 404, 500)
                    request.setAttribute("mensajeError", "Error del servidor de datos: " + res.getCode());
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                }
            }
        } catch (Exception e) {
            // Manejo de error si el servidor de CentroEducativo está caído
            request.setAttribute("mensajeError", "No se ha podido conectar con CentroEducativo.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}
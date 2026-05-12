package servlets;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import api.ApiClient;         // IMPORTANTE: Importamos tu nuevo ApiClient
import models.Asignatura;     // CORREGIDO: Tu paquete se llama 'models' según la foto

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AsignaturasAlumnoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Obtener la sesión y validar que el usuario ha hecho login
        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("key") == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }
        
        // Recuperamos la 'key' y el 'dni' que guardó el servlet Acceso.java al hacer login
        String key = (String) sesion.getAttribute("key");
        String dni = (String) sesion.getAttribute("dni");
        
        // Obtenemos la URL base configurada en el web.xml
        String baseUrl = getServletContext().getInitParameter("centroEducativoUrl");
        if (baseUrl == null) {
            baseUrl = "http://localhost:9090/CentroEducativo"; // Valor por defecto
        }
        
        try {
            // 2. Construimos la URL final (ej: http://.../alumnos/12345678W/asignaturas?key=...)
            String urlFinal = baseUrl + "/alumnos/" + dni + "/asignaturas?key=" + key;
            
            // 3. Pedimos el JSON crudo a la API usando tu nueva clase ApiClient
            String jsonCrudo = ApiClient.obtenerDatosGet(urlFinal);
            
            // 4. Magia de GSON: Convertimos el texto JSON a una Lista de objetos Java
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Asignatura>>(){}.getType();
            List<Asignatura> listaAsignaturas = gson.fromJson(jsonCrudo, listType);
            
            // 5. Pasamos la lista de objetos Java a la petición
            request.setAttribute("asignaturas", listaAsignaturas);
            
            // 6. Redirigimos al JSP del front que está en la raíz de webapp
            RequestDispatcher dispatcher = request.getRequestDispatcher("/asignaturas_alumno.jsp");
            dispatcher.forward(request, response);
            
        } catch (Exception e) {
            // Si la API falla, mandamos un error 500
            throw new ServletException("Error al cargar las asignaturas desde la API", e);
        }
    }
}
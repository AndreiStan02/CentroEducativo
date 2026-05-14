package serverlets; // ¡CAMBIO AQUÍ!

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import api.ApiClient;
import models.Asignatura;

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
        
        // 1. Obtener la sesión y validar que el usuario está logueado
        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("key") == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }
        
        String key = (String) sesion.getAttribute("key");
        String dni = (String) sesion.getAttribute("dni");
        
        String baseUrl = getServletContext().getInitParameter("centroEducativoUrl");
        if (baseUrl == null) {
            baseUrl = "http://localhost:9090/CentroEducativo";
        }
        
        try {
            // 2. Construir la URL
            String urlFinal = baseUrl + "/alumnos/" + dni + "/asignaturas?key=" + key;
            
            // 3. Obtener el JSON desde la API
            String jsonCrudo = ApiClient.obtenerDatosGet(urlFinal);
            
            // 4. Parsear con Gson
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Asignatura>>(){}.getType();
            List<Asignatura> listaAsignaturas = gson.fromJson(jsonCrudo, listType);
            
            // 5. Pasar los datos al JSP
            request.setAttribute("asignaturas", listaAsignaturas);
            
            // 6. Redirigir al JSP (que debe estar en la carpeta webapp)
            RequestDispatcher dispatcher = request.getRequestDispatcher("/asignaturas_alumno.jsp");
            dispatcher.forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Error obteniendo asignaturas: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/login-error.html");
        }
    }
}

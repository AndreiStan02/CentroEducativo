package serverlets;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import api.ApiClient;
import models.Alumno; // Asegúrate de tener este modelo creado

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AlumnosAsignaturaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession sesion = request.getSession(false);
        // Validamos la sesión y que sea profesor
        if (sesion == null || sesion.getAttribute("key") == null || !"rolpro".equals(sesion.getAttribute("rol"))) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        String key = (String) sesion.getAttribute("key");
        
        // Obtenemos el acrónimo o ID de la asignatura desde la URL (ej: ?asignatura=DEW)
        String acronimoAsig = request.getParameter("asignatura");
        
        if (acronimoAsig == null || acronimoAsig.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/profesor_asignaturas.jsp");
            return;
        }

        String baseUrl = getServletContext().getInitParameter("centroEducativoUrl");
        if (baseUrl == null) baseUrl = "http://localhost:9090/CentroEducativo";

        try {
            // ⚠️ ATENCIÓN: Debes verificar esta ruta exacta en tu Swagger
            // Aquí asumo un endpoint típico REST, pero podría variar según vuestra API
            String urlFinal = baseUrl + "/asignaturas/" + acronimoAsig + "/alumnos?key=" + key;
            
            String jsonCrudo = ApiClient.obtenerDatosGet(urlFinal);
            
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Alumno>>(){}.getType();
            List<Alumno> listaAlumnos = gson.fromJson(jsonCrudo, listType);
            
            // Pasamos la lista y el nombre de la asignatura a la vista
            request.setAttribute("alumnos", listaAlumnos);
            request.setAttribute("asignatura", acronimoAsig);
            
            RequestDispatcher dispatcher = request.getRequestDispatcher("/alumnos_asignatura.jsp");
            dispatcher.forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Error obteniendo alumnos de la asignatura: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/login-error.html");
        }
    }
}
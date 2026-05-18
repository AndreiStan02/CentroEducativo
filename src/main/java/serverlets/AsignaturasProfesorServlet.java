package serverlets;

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

public class AsignaturasProfesorServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession sesion = request.getSession(false);
        // Validamos que hay sesión y que el rol es de profesor
        if (sesion == null || sesion.getAttribute("key") == null || !"rolpro".equals(sesion.getAttribute("rol"))) {
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
            // Petición a la API para profesores
            String urlFinal = baseUrl + "/profesores/" + dni + "/asignaturas?key=" + key;
            String jsonCrudo = ApiClient.obtenerDatosGet(urlFinal);
            
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Asignatura>>(){}.getType();
            List<Asignatura> listaAsignaturas = gson.fromJson(jsonCrudo, listType);
            
            request.setAttribute("asignaturas", listaAsignaturas);
            
            // Redirigimos al JSP del profesor (asegúrate de que este archivo exista)
            RequestDispatcher dispatcher = request.getRequestDispatcher("/profesor_asignaturas.jsp");
            dispatcher.forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Error obteniendo asignaturas de profesor: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/login-error.html");
        }
    }
}
package dew.backend;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import dew.models.Asignatura;
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
            // Si no hay sesión, lo mandamos al login
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }
        
        String key = (String) sesion.getAttribute("key");
        
        try {
            // 2. Pedimos el JSON crudo a la API
            String jsonCrudo = ApiClient.obtenerAsignaturas(key);
            
            // 3. Magia de GSON: Convertimos el texto JSON a una Lista de objetos Java
            Gson gson = new Gson();
            // TypeToken es necesario en Gson cuando queremos convertir a una lista genérica (List<T>)
            Type listType = new TypeToken<List<Asignatura>>(){}.getType();
            List<Asignatura> listaAsignaturas = gson.fromJson(jsonCrudo, listType);
            
            // 4. Pasamos la lista de objetos Java a la petición
            request.setAttribute("asignaturas", listaAsignaturas);
            
            // 5. Redirigimos al JSP del front (Asegúrate de que han cambiado el .html a .jsp)
            RequestDispatcher dispatcher = request.getRequestDispatcher("/asignaturas_alumno.jsp");
            dispatcher.forward(request, response);
            
        } catch (Exception e) {
            // Si la API falla, mandamos un error 500
            throw new ServletException("Error al cargar las asignaturas desde la API", e);
        }
    }
}
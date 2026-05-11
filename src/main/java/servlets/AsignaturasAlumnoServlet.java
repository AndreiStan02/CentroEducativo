package dew;

import java.io.IOException;
import java.io.PrintWriter;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AsignaturasAlumnoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Configuramos el tipo de contenido de la respuesta a HTML
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        // 2. Generamos un HTML muy básico de prueba
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Mis Asignaturas</title></head>");
        out.println("<body>");
        out.println("<h1>Servlet de Asignaturas Funcional</h1>");
        out.println("<p>¡Si ves esto, el servlet y el web.xml están bien configurados!</p>");
        out.println("</body></html>");
    }
}
package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet Logout
 *
 * Cierra la sesión del usuario:
 *  1. Invalida la HttpSession (elimina key, dni, pass).
 *  2. Llama a request.logout() para que Tomcat limpie la autenticación.
 *  3. Redirige a la página de inicio.
 *
 * URL de mapeo (web.xml): /logout
 */
public class Logout extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cerrarSesion(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cerrarSesion(request, response);
    }

    private void cerrarSesion(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        // Invalidar la sesión HTTP (limpia atributos key, dni, pass, etc.)
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Cerrar sesión de Tomcat (limpia la autenticación FORM)
        request.logout();

        // Redirigir a la página de inicio
        response.sendRedirect(request.getContextPath() + "/index.html");
    }
}
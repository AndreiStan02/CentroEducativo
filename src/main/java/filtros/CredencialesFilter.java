package filtros;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Filtro CredencialesFilter
 * * Intercepta de forma segura el POST hacia /j_security_check.
 * Previene el error HTTP 408 forzando el mapeo interno de parámetros.
 */
public class CredencialesFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No requiere inicialización especial
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // ACCIÓN CRÍTICA: Forzamos a Tomcat a parsear el cuerpo del mensaje POST.
        // Al invocar getParameter, Tomcat lee los bytes de la red y guarda los parámetros
        // de login en caché. Evita que la cadena posterior encuentre un InputStream vacío.
        httpRequest.getParameter("j_password"); 

        // Recuperamos la contraseña ya almacenada de forma segura en memoria de la petición
        String password = httpRequest.getParameter("j_password");

        if (password != null && !password.isBlank()) {
            // Guardamos temporalmente la credencial para que el Servlet Acceso pueda consumirla
            httpRequest.getSession().setAttribute("pendingPass", password);
        }

        // Continuamos la cadena. Tomcat procesará /j_security_check usando su ParameterMap interno.
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // No requiere limpieza
    }
}
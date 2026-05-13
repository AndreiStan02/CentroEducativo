package filtros;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns = {
    "/asignaturas_alumno.html",
    "/detalles_alumno.html",
    "/profesor-asignaturas.html",
    "/profesor-evaluacion.html"
})
public class SesionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        // ✅ Usar "key", que es lo que guarda tu servlet Acceso
        boolean autenticado = (session != null && session.getAttribute("key") != null);

        if (autenticado) {
            chain.doFilter(request, response);
        } else {
            res.sendRedirect(req.getContextPath() + "/login.html");
        }
    }

    @Override
    public void destroy() {}
}
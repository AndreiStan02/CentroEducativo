package filtros;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class filtroLogs implements Filter {

    private String nombreArchivo;

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        // Obtenemos "nol2526.log" del web.xml
        this.nombreArchivo = fConfig.getServletContext().getInitParameter("archivoLog");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        
        // Datos requeridos por el Hito 1
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String ip = req.getRemoteAddr();
        String servidor = req.getRequestURI();
        String metodo = req.getMethod();
        
        // Obtener usuario de sesión
        HttpSession sesion = req.getSession(false);
        String usuario = (sesion != null && sesion.getAttribute("usuario") != null) 
                         ? (String) sesion.getAttribute("usuario") 
                         : "anonimo";

        // Escritura en el archivo
        if (this.nombreArchivo != null) {
            // El 'true' es fundamental para añadir líneas y no borrar el archivo
            try (FileWriter fw = new FileWriter(this.nombreArchivo, true);
                 PrintWriter out = new PrintWriter(fw)) {
                out.printf("%s %s %s %s %s%n", fecha, usuario, ip, servidor, metodo);
                
            } catch (IOException e) {
                // Si falla el log, solo avisamos por consola; la web NO se para
                System.err.println("Error en el log: " + e.getMessage());
            }
        }

        // DEJA PASAR LA PETICIÓN: Imprescindible para que la web funcione
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
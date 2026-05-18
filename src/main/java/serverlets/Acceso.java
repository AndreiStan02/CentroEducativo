package serverlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ContentType;

import java.io.IOException;

/**
 * Servlet "Acceso": Se encarga de compatibilizar y acoplar la sesión web de Tomcat 
 * con el nivel de datos (CentroEducativo) de forma transparente.
 */
public class Acceso extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private String centroEducativoUrl;

    @Override
    public void init() throws ServletException {
        centroEducativoUrl = getServletContext().getInitParameter("centroEducativoUrl");
        if (centroEducativoUrl == null || centroEducativoUrl.isBlank()) {
            centroEducativoUrl = "http://localhost:9090/CentroEducativo";
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesarAcoplamientoDeSesion(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesarAcoplamientoDeSesion(request, response);
    }

    private void procesarAcoplamientoDeSesion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Tomcat garantiza que si la ejecución llega aquí, ya existe un usuario autenticado
        String login = request.getRemoteUser(); // Equivalente a web.auth() del pseudocódigo
        
        if (login == null) {
            log("[Acceso] Error: El contenedor no ha propagado credenciales válidas.");
            response.sendRedirect(request.getContextPath() + "/login.html?error=1");
            return;
        }

        // Determinar el rol mediante seguridad programática
        String rol = null;
        if (request.isUserInRole("rolpro")) {
            rol = "rolpro";
        } else if (request.isUserInRole("rolalu")) {
            rol = "rolalu";
        }

        HttpSession session = request.getSession(true);

        // Si no disponemos de token de datos remoto ('key'), invocamos a CentroEducativo
        if (session.getAttribute("key") == null) {
            
            // Contraseña fija por defecto declarada en el enunciado para las cuentas de arranque
            String password = "123456"; 

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                log("[Acceso] Solicitando clave de datos para el DNI: " + login);
                String key = dataAuth(httpClient, login, password);

                if (key != null) {
                    // Almacenamos en sesión los datos exigidos para las llamadas posteriores
                    session.setAttribute("dni",  login);
                    session.setAttribute("pass", password);
                    session.setAttribute("key",  key);
                    session.setAttribute("rol",  rol);
                    log("[Acceso] Sesión unificada correctamente. Key asignada: " + key);
                } else {
                    log("[Acceso] Falló el login contra CentroEducativo API REST.");
                    response.sendRedirect(request.getContextPath() + "/login.html?error=data_failed");
                    return;
                }
            } catch (Exception e) {
                log("[Acceso] Excepción de conexión contra el nivel de datos: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/login.html?error=data_failed");
                return;
            }
        }

        // Redirección final controlada basándose en el rol del usuario autenticado
        redirigirUsuario(request, response, (String) session.getAttribute("rol"));
    }

    private void redirigirUsuario(HttpServletRequest request, HttpServletResponse response, String rol) 
            throws IOException {
        if ("rolpro".equals(rol)) {
            // Redirige directamente al HTML de profesores en la raíz de webapp
            response.sendRedirect(request.getContextPath() + "/profesor-asignaturas.html");
        } else {
            // Redirige directamente al HTML de alumnos en la raíz de webapp
            response.sendRedirect(request.getContextPath() + "/asignaturas_alumno.html");
        }
    }

    /**
     * Envía la petición POST /login a la API REST de CentroEducativo
     */
    private String dataAuth(CloseableHttpClient httpClient, String dni, String password) {
        String loginUrl = centroEducativoUrl + "/login";
        String jsonBody = "{\"dni\":\"" + dni + "\",\"password\":\"" + password + "\"}";

        try {
            HttpPost httpPost = new HttpPost(loginUrl);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "text/plain");
            httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse res = httpClient.execute(httpPost)) {
                int code = res.getCode();
                String body = EntityUtils.toString(res.getEntity()).trim();
                
                if (code == 200 && !body.isEmpty()) {
                    return body; // Retorna la 'key' alfanumérica devuelta por la API
                }
            }
        } catch (Exception e) {
            log("[Acceso] Excepción en método auxiliar dataAuth: " + e.getMessage());
        }
        return null;
    }
}
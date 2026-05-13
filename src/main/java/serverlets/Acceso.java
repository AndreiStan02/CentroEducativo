package serverlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ContentType;

import java.io.IOException;

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

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("key") != null) {
            redirigirSegunRol(request, response, (String) session.getAttribute("rol"));
            return;
        }
        response.sendRedirect(request.getContextPath() + "/login.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Si ya hay sesión activa, redirigir directamente
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("key") != null) {
            redirigirSegunRol(request, response, (String) session.getAttribute("rol"));
            return;
        }

        String dni      = request.getParameter("dni");
        String password = request.getParameter("password");

        if (dni == null || password == null || dni.isBlank() || password.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/login.html?error=1");
            return;
        }

        dni      = dni.trim();
        password = password.trim();

        // Un único cliente HTTP para que la cookie de sesión de CentroEducativo
        // se mantenga entre el POST /login y los GET posteriores
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            // --- 1. Login contra CentroEducativo ---
            String key = login(httpClient, dni, password);

            if (key == null) {
                log("[Acceso] Login fallido para DNI: " + dni);
                response.sendRedirect(request.getContextPath() + "/login.html?error=1");
                return;
            }

            log("[Acceso] Login OK. Key: " + key);

            // --- 2. Determinar rol usando el mismo cliente (misma cookie) ---
            String rol = obtenerRol(httpClient, dni, key);

            if (rol == null) {
                log("[Acceso] No se pudo determinar el rol para DNI: " + dni);
                response.sendRedirect(request.getContextPath() + "/login.html?error=1");
                return;
            }

            log("[Acceso] Rol: " + rol);

            // --- 3. Guardar en sesión HTTP de Tomcat ---
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("dni",  dni);
            newSession.setAttribute("pass", password);
            newSession.setAttribute("key",  key);
            newSession.setAttribute("rol",  rol);

            // --- 4. Redirigir ---
            redirigirSegunRol(request, response, rol);

        } catch (Exception e) {
            log("[Acceso] Error inesperado: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/login.html?error=1");
        }
    }

    // -------------------------------------------------------------------------
    // Métodos auxiliares
    // -------------------------------------------------------------------------

    private void redirigirSegunRol(HttpServletRequest request, HttpServletResponse response, String rol)
            throws IOException {
        if ("rolpro".equals(rol)) {
            response.sendRedirect(request.getContextPath() + "/profesor-asignaturas.html");
        } else {
            response.sendRedirect(request.getContextPath() + "/asignaturas_alumno.html");
        }
    }

    /**
     * Hace POST /login y devuelve la key si las credenciales son correctas, null si no.
     * Usa el cliente recibido para que la cookie de sesión quede guardada en él.
     */
    private String login(CloseableHttpClient httpClient, String dni, String password) {
        String loginUrl = centroEducativoUrl + "/login";
        String jsonBody = "{\"dni\":\"" + escapeJson(dni) + "\",\"password\":\"" + escapeJson(password) + "\"}";

        log("[Acceso] POST " + loginUrl + " | Body: " + jsonBody);

        try {
            HttpPost httpPost = new HttpPost(loginUrl);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "text/plain");
            httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse res = httpClient.execute(httpPost)) {
                int code = res.getCode();
                String body = EntityUtils.toString(res.getEntity()).trim();
                log("[Acceso] Login response: " + code + " | Body: " + body);
                if (code == 200) {
                    return body;
                }
            }
        } catch (Exception e) {
            log("[Acceso] Error en login: " + e.getMessage());
        }
        return null;
    }

    /**
     * Usa el mismo cliente (con la cookie de sesión ya establecida) para consultar
     * si el DNI corresponde a un alumno o a un profesor.
     */
    private String obtenerRol(CloseableHttpClient httpClient, String dni, String key) {
        try {
            // Probar si es alumno — key por query string, mismo cliente (tiene la cookie)
            String urlAlumno = centroEducativoUrl + "/alumnos/" + dni + "?key=" + key;
            log("[Acceso] GET " + urlAlumno);
            HttpGet getAlumno = new HttpGet(urlAlumno);
            getAlumno.setHeader("Accept", "application/json");

            try (CloseableHttpResponse res = httpClient.execute(getAlumno)) {
                int code = res.getCode();
                log("[Acceso] /alumnos/" + dni + " → " + code);
                EntityUtils.consume(res.getEntity());
                if (code == 200) return "rolalu";
            }

            // Probar si es profesor
            String urlProfesor = centroEducativoUrl + "/profesores/" + dni + "?key=" + key;
            log("[Acceso] GET " + urlProfesor);
            HttpGet getProfesor = new HttpGet(urlProfesor);
            getProfesor.setHeader("Accept", "application/json");

            try (CloseableHttpResponse res = httpClient.execute(getProfesor)) {
                int code = res.getCode();
                log("[Acceso] /profesores/" + dni + " → " + code);
                EntityUtils.consume(res.getEntity());
                if (code == 200) return "rolpro";
            }

        } catch (Exception e) {
            log("[Acceso] Error obteniendo rol: " + e.getMessage());
        }
        return null;
    }

    private String escapeJson(String valor) {
        if (valor == null) return "";
        return valor
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\b", "\\b")
            .replace("\f", "\\f")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}
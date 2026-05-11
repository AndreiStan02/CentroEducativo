package servlets;

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

/**
 * Servlet Acceso
 *
 * Gestiona el login de la aplicación NOL comunicándose con CentroEducativo.
 *
 * Flujo:
 *  1. Si ya hay sesión activa → redirige directamente según rol (sin repetir login)
 *  2. Recibe DNI y password del formulario login.html
 *  3. Hace POST /login a CentroEducativo
 *  4. Si responde 200 → recibe la key de sesión
 *  5. Consulta el rol del usuario en CentroEducativo (alumno o profesor)
 *  6. Guarda dni, pass, key y rol en la HttpSession de Tomcat
 *  7. Redirige según el rol
 *
 * URL de mapeo (web.xml): /acceso
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

    /**
     * GET: si alguien llega a /acceso directamente:
     *  - Si ya tiene sesión activa → redirige a su zona según rol
     *  - Si no → redirige al formulario de login
     */
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

    /**
     * POST: recibe el formulario de login.html con los campos "dni" y "password".
     *  - Si ya tiene sesión activa → redirige sin volver a hacer login
     *  - Si no → procesa el login contra CentroEducativo
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Comprobar si ya hay sesión activa
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("key") != null) {
            redirigirSegunRol(request, response, (String) session.getAttribute("rol"));
            return;
        }

        // --- 1. Leer los datos del formulario ---
        String dni      = request.getParameter("dni");
        String password = request.getParameter("password");

        if (dni == null || password == null || dni.isBlank() || password.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/login-error.html");
            return;
        }

        // --- 2. Autenticar contra CentroEducativo ---
        String key = autenticarEnCentroEducativo(dni, password);

        if (key == null) {
            // CentroEducativo rechazó las credenciales (401) o hubo un error de conexión
            response.sendRedirect(request.getContextPath() + "/login-error.html");
            return;
        }

        // --- 3. Consultar el rol del usuario ---
        String rol = obtenerRol(dni, key);

        if (rol == null) {
            response.sendRedirect(request.getContextPath() + "/login-error.html");
            return;
        }

        // --- 4. Guardar datos en la sesión HTTP ---
        // Todos los servlets del grupo accederán a estos atributos para
        // hacer peticiones autenticadas a CentroEducativo:
        //   String key = (String) session.getAttribute("key");
        //   String dni = (String) session.getAttribute("dni");
        //   String rol = (String) session.getAttribute("rol");
        HttpSession newSession = request.getSession(true);
        newSession.setAttribute("dni",  dni);
        newSession.setAttribute("pass", password);
        newSession.setAttribute("key",  key);
        newSession.setAttribute("rol",  rol);

        // --- 5. Redirigir según el rol ---
        redirigirSegunRol(request, response, rol);
    }

    // -------------------------------------------------------------------------
    // Métodos auxiliares
    // -------------------------------------------------------------------------

    /**
     * Redirige al usuario a su zona según el rol.
     * Extraído para no repetir la lógica en doGet y doPost.
     */
    private void redirigirSegunRol(HttpServletRequest request, HttpServletResponse response, String rol)
            throws IOException {
        if ("rolpro".equals(rol)) {
            response.sendRedirect(request.getContextPath() + "/profesor/asignaturas");
        } else {
            response.sendRedirect(request.getContextPath() + "/alumno/asignaturas");
        }
    }

    /**
     * Hace POST /login a CentroEducativo.
     *
     * @return La key de sesión si las credenciales son correctas, null si no.
     */
    private String autenticarEnCentroEducativo(String dni, String password) {
        String loginUrl = centroEducativoUrl + "/login";
        String jsonBody = String.format("{\"dni\":\"%s\",\"password\":\"%s\"}", dni, password);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            HttpPost httpPost = new HttpPost(loginUrl);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {
                int statusCode = httpResponse.getCode();
                if (statusCode == 200) {
                    return EntityUtils.toString(httpResponse.getEntity()).trim();
                } else {
                    return null;
                }
            }

        } catch (Exception e) {
            log("Error conectando con CentroEducativo: " + e.getMessage());
            return null;
        }
    }

    /**
     * Consulta el rol del usuario preguntando a CentroEducativo.
     *
     * Lógica:
     *  - GET /alumnos/{dni}    → si responde 200, es alumno  (rolalu)
     *  - GET /profesores/{dni} → si responde 200, es profesor (rolpro)
     *
     * @return "rolalu", "rolpro", o null si no se puede determinar.
     */
    private String obtenerRol(String dni, String key) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            // Comprobar si es alumno
            String urlAlumno = centroEducativoUrl + "/alumnos/" + dni + "?key=" + key;
            HttpGet getAlumno = new HttpGet(urlAlumno);
            getAlumno.setHeader("accept", "application/json");

            try (CloseableHttpResponse res = httpClient.execute(getAlumno)) {
                if (res.getCode() == 200) {
                    return "rolalu";
                }
            }

            // Comprobar si es profesor
            String urlProfesor = centroEducativoUrl + "/profesores/" + dni + "?key=" + key;
            HttpGet getProfesor = new HttpGet(urlProfesor);
            getProfesor.setHeader("accept", "application/json");

            try (CloseableHttpResponse res = httpClient.execute(getProfesor)) {
                if (res.getCode() == 200) {
                    return "rolpro";
                }
            }

        } catch (Exception e) {
            log("Error consultando rol en CentroEducativo: " + e.getMessage());
        }

        return null;
    }
}
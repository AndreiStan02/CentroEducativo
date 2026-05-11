
 * package filtros; import jakarta.servlet.*; import jakarta.servlet.http.*;
 * import java.io.*; import java.time.LocalDateTime; import
 * java.time.format.DateTimeFormatter;
 * 
 * public class FiltroLogs implements Filter{
 * 
 * private String rutaArchivo;
 * 
 * @Override public void init(FilterConfig fConfig) throws ServletException{
 * 
 * rutaArchivo = fConfig.getServletContext().getInitParameter("archivoLog");
 * 
 * if(rutaArchivo != null) { File archivo = new File(rutaArchivo); File carpeta
 * = archivo.getParentFile();
 * 
 * if(carpeta != null && !carpeta.exists()) { carpeta.mkdirs(); } } }
 * 
 * @Override public void doFilter(ServletRequest request, ServletResponse response,
 * FilterChain chain) throws IOException, ServletException{
 * 
 * HttpServletRequest req = (HttpServletRequest) request;
 * 
 * String fecha =
 * LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME); String
 * ipUsuario = req.getRemoteAddr(); String servlet = req.getRequestURI(); String
 * metodo = req.getMethod(); HttpSession sesion = req.getSession(false); String
 * usuario = "";
 * 
 * if(sesion != null && sesion.getAttribute("usuario") != null) { usuario =
 * (String) sesion.getAttribute("usuario"); }
 * 
 * try { PrintWriter out = new PrintWriter(new FileWriter(rutaArchivo, true));
 * out.printf("%s %s %s %s %s%n", fecha, usuario, ipUsuario, servlet, metodo);
 * out.close(); } catch (IOException e) {
 * System.err.println("Error escritura en el FIltro Log: " + e.getMessage()); }
 * 
 * chain.doFilter(request, response); }
 * 
 * @Override public void destroy() {}
 * 
 * }
 
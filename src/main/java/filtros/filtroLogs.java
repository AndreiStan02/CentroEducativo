/*import java.io.File;

@Override 
public void init(FilterConfig fConfig) throws ServletException {
    
    // 1. Leemos el parámetro del web.xml ("/logs/no12526.log")
    String rutaVirtual = fConfig.getServletContext().getInitParameter("archivoLog");
    
    // 2. Traducimos esa ruta a la ruta real en tu disco duro
    rutaArchivo = fConfig.getServletContext().getRealPath(rutaVirtual);
    
    // Si la ruta real no se pudo resolver (a veces pasa en ciertos servidores), 
    // le damos una ruta alternativa segura
    if (rutaArchivo == null) {
        rutaArchivo = System.getProperty("java.io.tmpdir") + File.separator + "no12526.log";
    }
    
    // 3. Creamos las carpetas si no existen
    File archivo = new File(rutaArchivo); 
    File carpeta = archivo.getParentFile();
    
    if(carpeta != null && !carpeta.exists()) { 
        carpeta.mkdirs(); 
    } 
    
    // (Opcional) Imprimir en consola para que sepas EXACTAMENTE dónde se ha guardado
    System.out.println("El log se está guardando en: " + rutaArchivo);
}
*/
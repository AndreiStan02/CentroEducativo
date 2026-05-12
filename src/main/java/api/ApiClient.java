package api;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

public class ApiClient {

    // Método genérico para hacer peticiones GET a la API
    public static String obtenerDatosGet(String urlFinal) throws Exception {
        
        // Usamos Apache HttpClient igual que en el archivo Acceso.java
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            
            HttpGet request = new HttpGet(urlFinal);
            request.setHeader("Accept", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getCode() == 200) {
                    // Devuelve el JSON en formato String
                    return EntityUtils.toString(response.getEntity());
                } else {
                    throw new Exception("Error del servidor de datos: " + response.getCode());
                }
            }
        }
    }
}
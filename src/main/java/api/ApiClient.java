package api;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

public class ApiClient {

    // Método reutilizable para hacer peticiones GET a CentroEducativo
    public static String obtenerDatosGet(String urlFinal) throws Exception {
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            
            HttpGet request = new HttpGet(urlFinal);
            request.setHeader("Accept", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getCode() == 200) {
                    return EntityUtils.toString(response.getEntity());
                } else {
                    throw new Exception("Error de CentroEducativo. Código: " + response.getCode());
                }
            }
        }
    }
}
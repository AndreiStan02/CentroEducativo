package java.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:9090/CentroEducativo";
    // OkHttp recomienda usar una sola instancia para toda la app
    private static final OkHttpClient client = new OkHttpClient();

    public static String obtenerAsignaturas(String key) throws IOException {
        // Construimos la petición GET hacia la API
        Request request = new Request.Builder()
                .url(BASE_URL + "/asignaturas?key=" + key)
                .get()
                .build();

        // Ejecutamos la petición y devolvemos el cuerpo (el JSON crudo)
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error inesperado en la API: " + response);
            }
            return response.body().string();
        }
    }
}
package team404.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Bukkit;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpClient {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void sendPostRequest(String urlString, Object request) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String requestBody = objectMapper.writeValueAsString(request);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Bukkit.getLogger().warning("Request was not send, response code: %s".formatted(responseCode));
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Request was not send");
        }
    }
}

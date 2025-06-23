package io.gabbloquet.functioncalling.meteo.infrastructure.sortie.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gabbloquet.functioncalling.meteo.domaine.MeteoPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class OpenWeatherMeteoAdapter implements MeteoPort {

    @Value("${openweather.api.key:VOTRE_CLE_API}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String recuperer(String ville) {
        String url = String.format(
            "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric&lang=fr",
            ville, apiKey
        );
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return "Erreur lors de la récupération de la météo : " + response.body();
            }
            JsonNode json = objectMapper.readTree(response.body());
            String description = json.path("weather").get(0).path("description").asText("");
            double temp = json.path("main").path("temp").asDouble(Double.NaN);

            System.out.printf("[Backend] : OpenWeather a répondu : '%s'.\n", description);

            return String.format("À %s : %s, %.1f°C", ville, description, temp);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Erreur lors de la récupération de la météo : " + e.getMessage();
        }
    }
}

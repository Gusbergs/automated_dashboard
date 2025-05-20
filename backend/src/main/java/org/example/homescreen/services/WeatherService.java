package org.example.homescreen.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.homescreen.model.slModels.SlRoot;
import org.example.homescreen.model.weather.Weather;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class WeatherService {


    private HttpClient httpClient = HttpClient.newHttpClient();

    public Weather getWeatherByCord() throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/17.8030/lat/59.2029/data.json"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            return null;
        }

        JsonNode root = mapper.readTree(response.body());
        JsonNode firstTimeSeries = root.path("timeSeries").get(0);
        JsonNode parameters = firstTimeSeries.path("parameters");

        double temperature = 0;
        int symbol = 0;

        for (JsonNode param : parameters) {
            String name = param.path("name").asText();
            JsonNode values = param.path("values");
            if (values.isArray() && values.size() > 0) {
                if (name.equals("t")) {
                    temperature = values.get(0).asDouble();
                } else if (name.equals("Wsymb2")) {
                    symbol = values.get(0).asInt();
                }
            }
        }

        return new Weather(temperature, symbol);
    }
}

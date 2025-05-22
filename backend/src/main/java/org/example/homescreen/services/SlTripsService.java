package org.example.homescreen.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.homescreen.model.slModels.SlRoot;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class SlTripsService {

    private HttpClient httpClient = HttpClient.newHttpClient();

    //7407 tumba bruksmuseum
    public SlRoot fetchSLTrips(int siteId) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://transport.integration.sl.se/v1/sites/" + siteId + "/departures"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return mapper.readValue(response.body(), SlRoot.class);
    }
}

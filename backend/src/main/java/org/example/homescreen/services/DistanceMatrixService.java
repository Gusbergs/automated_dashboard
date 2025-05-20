package org.example.homescreen.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class DistanceMatrixService {

    private final WebClient mapsWebClient;
    private final String apiKey;

    @Autowired
    public DistanceMatrixService(WebClient mapsWebClient, String googleMapsApiKey) {
        this.mapsWebClient = mapsWebClient;
        this.apiKey = googleMapsApiKey;
    }

    /**
     * Fetch travel time between two places with traffic (departure_time=now)
     * @param origin      e.g. "Stockholm+Sweden" or lat,lng
     * @param destination e.g. "Uppsala+Sweden" or lat,lng
     * @return Map with duration and duration_in_traffic
     */
    public Mono<Map<String, String>> getTravelTime(String origin, String destination) {
        return mapsWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("origins", origin)
                        .queryParam("destinations", destination)
                        .queryParam("departure_time", "now")
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    // Logga hela API-svaret för felsökning
                    System.out.println("Google svar " + response);

                    // 1) Kontrollera status
                    String status = (String) response.get("status");
                    if (!"OK".equals(status)) {
                        String errMsg = response.getOrDefault("error_message", "Okänt fel")
                                .toString();
                        return Mono.error(new IllegalStateException(
                                "Google API error: " + status + " – " + errMsg));
                    }

                    // 2) Läs origin- och destination‐adresser från API-svaret
                    List<String> originAddrs = (List<String>) response.get("origin_addresses");
                    List<String> destAddrs   = (List<String>) response.get("destination_addresses");
                    String originAddr  = (originAddrs  != null && !originAddrs.isEmpty())
                            ? originAddrs.get(0) : origin;
                    String destAddr    = (destAddrs    != null && !destAddrs.isEmpty())
                            ? destAddrs.get(0)   : destination;

                    // 3) Hämta rows och kontrollera innehåll
                    List<Map<String, Object>> rows =
                            (List<Map<String, Object>>) response.get("rows");
                    if (rows == null || rows.isEmpty()) {
                        return Mono.error(new IllegalStateException(
                                "API-svaret saknar 'rows' eller är tomt"));
                    }

                    // 4) Hämta elements i första raden
                    List<Map<String, Object>> elements =
                            (List<Map<String, Object>>) rows.get(0).get("elements");
                    if (elements == null || elements.isEmpty()) {
                        return Mono.error(new IllegalStateException(
                                "API-svaret saknar 'elements' i första raden"));
                    }

                    // 5) Plocka ut duration‐fälten
                    Map<String, Object> element        = elements.get(0);
                    Map<String, Object> duration       = (Map<String, Object>) element.get("duration");
                    Map<String, Object> durationInFlow = (Map<String, Object>) element.get("duration_in_traffic");

                    // 6) Returnera allt i en enkel Map
                    return Mono.just(Map.of(
                            "origin",               originAddr,
                            "destination",          destAddr,
                            "duration",             duration.get("text").toString(),
                            "durationInTraffic",    durationInFlow.get("text").toString()
                    ));
                });
    }
}
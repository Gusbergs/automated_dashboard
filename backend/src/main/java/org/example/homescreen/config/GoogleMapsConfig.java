package org.example.homescreen.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GoogleMapsConfig {

    private final Dotenv dotenv = Dotenv.configure()
            .directory("./")       // .env-filen ligger i projektroten
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();

    private final String apiKey = dotenv.get("GOOGLE_MAPS_API_KEY");

    @Bean
    public WebClient mapsWebClient() {
        return WebClient.builder()
                .baseUrl("https://maps.googleapis.com/maps/api/distancematrix/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    @Bean
    public String googleMapsApiKey() {
        return apiKey;
    }
}
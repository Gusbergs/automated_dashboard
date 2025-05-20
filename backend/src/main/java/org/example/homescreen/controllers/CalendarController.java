package org.example.homescreen.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@RestController
public class CalendarController {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://www.googleapis.com/calendar/v3")
            .build();

    @GetMapping("/api/calendar/events")
    public Mono<List<Map<String, Object>>> getEvents(
            @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
            @AuthenticationPrincipal OAuth2User principal
    ) {
        String token = authorizedClient.getAccessToken().getTokenValue();

        return webClient.get()
                .uri("/calendars/primary/events?maxResults=10&orderBy=startTime&singleEvents=true&timeMin={now}", Map.of("now", java.time.OffsetDateTime.now().toString()))
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(Map.class)
                .map(body -> (List<Map<String, Object>>) body.get("items"));
    }
}

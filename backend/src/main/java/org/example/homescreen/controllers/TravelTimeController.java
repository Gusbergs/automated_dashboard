package org.example.homescreen.controllers;

import org.example.homescreen.services.DistanceMatrixService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class TravelTimeController {

    private final DistanceMatrixService distanceMatrixService;

    public TravelTimeController(DistanceMatrixService distanceMatrixService) {
        this.distanceMatrixService = distanceMatrixService;
    }

    /**
     * GET /api/travel-time?origin=...&destination=...
     */
    @GetMapping("/api/travel-time")
    public Mono<Map<String,String>> getTravelTime(
            @RequestParam String origin,
            @RequestParam String destination) {
        return distanceMatrixService.getTravelTime(origin, destination);
    }
}
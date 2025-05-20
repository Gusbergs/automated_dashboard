package org.example.homescreen.controllers;

import org.example.homescreen.model.weather.Weather;
import org.example.homescreen.services.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class WeatherController {

    WeatherService weatherService = new WeatherService();

    @GetMapping("/weather")
    public Weather getWeather() throws IOException, InterruptedException {
       return weatherService.getWeatherByCord();
    }
}

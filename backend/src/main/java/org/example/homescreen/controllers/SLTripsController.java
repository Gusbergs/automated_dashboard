package org.example.homescreen.controllers;

import org.example.homescreen.services.SlTripsService;
import org.example.homescreen.model.slModels.SlRoot;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class SLTripsController {

    SlTripsService slTripsService = new SlTripsService();


    @GetMapping("/getSlSiteTrips")
    public SlRoot getSlSiteTrips(@RequestParam int siteId) throws IOException, InterruptedException {
        return slTripsService.fetchSLTrips(siteId);
   }

}

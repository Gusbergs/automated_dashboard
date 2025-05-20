package org.example.homescreen.model.slModels;

import java.util.ArrayList;
import java.util.Date;

public class Departure {
    public String destination;
    public int direction_code;
    public String direction;
    public String state;
    public String display;
    public Date scheduled;
    public Date expected;
    public Journey journey;
    public StopArea stop_area;
    public StopPoint stop_point;
    public Line line;
    public ArrayList<Object> deviations;
}

package com.example.fasttransithub.Util;


import java.util.ArrayList;

public class Route {
    public String name;
    public ArrayList<Stop> stops;

    public Route(String name, ArrayList<Stop> stops) {
        this.name = name;
        this.stops = stops;
    }
    public Route() {
        this.name = "";
        this.stops = new ArrayList<Stop>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Stop> getStops() {
        return stops;
    }

    public void setStops(ArrayList<Stop> stops) {
        this.stops = stops;
    }

    public static class Stop{
        String name;
        String time;

        public Stop(String name, String time) {
            this.name = name;
            this.time = time;
        }

        public Stop(String name) {
            this.name = name;
            this.time = "";
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}

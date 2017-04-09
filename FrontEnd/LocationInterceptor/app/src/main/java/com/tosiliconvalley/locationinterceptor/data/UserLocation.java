package com.tosiliconvalley.locationinterceptor.data;

/**
 * Created by equipo on 3/24/2017.
 */
public class UserLocation {

    private Double lat;
    private Double lon;
    private String name;

    @SuppressWarnings("unused")
    public UserLocation() {
    }

    @SuppressWarnings("unused")
    public UserLocation(Double lat, Double lon, String name) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
    }

    @SuppressWarnings("unused")
    public Double getLat() {
        return lat;
    }
    @SuppressWarnings("unused")
    public void setLat(Double lat) {
        this.lat = lat;
    }

    @SuppressWarnings("unused")
    public Double getLon() {
        return lon;
    }
    @SuppressWarnings("unused")
    public void setLon(Double lon) {
        this.lon = lon;
    }

    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }
    @SuppressWarnings("unused")
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        String result =
                "lat: " + getLat() + ", " + "lon: " + getLon() + ", " + "name: " + getName();
        return result;
    }
}

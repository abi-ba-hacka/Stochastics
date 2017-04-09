package com.tosiliconvalley.locationinterceptor.communication;

/**
 * Created by equipo on 3/11/2017.
 */
public class BarLocationResponse {

    private Float lat;
    private Float lon;
    private String name;

    @SuppressWarnings("unused")
    public Float getLat() {
        return lat;
    }
    @SuppressWarnings("unused")
    public void setLat(Float lat) {
        this.lat = lat;
    }

    @SuppressWarnings("unused")
    public Float getLon() {
        return lon;
    }
    @SuppressWarnings("unused")
    public void setLon(Float lon) {
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
}

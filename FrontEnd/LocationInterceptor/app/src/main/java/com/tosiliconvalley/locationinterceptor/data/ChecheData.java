package com.tosiliconvalley.locationinterceptor.data;

/**
 * Created by equipo on 4/1/2017.
 */
public class ChecheData {

    private String location;
    private String map_url;
    private Float lat;
    private Float lon;

    public ChecheData() {
    }

    public ChecheData(String location, String map_url, Float lat, Float lon) {
        this.location = location;
        this.map_url = map_url;
        this.lat = lat;
        this.lon = lon;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMap_url() {
        return map_url;
    }

    public void setMap_url(String map_url) {
        this.map_url = map_url;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLon() {
        return lon;
    }

    public void setLon(Float lon) {
        this.lon = lon;
    }
}

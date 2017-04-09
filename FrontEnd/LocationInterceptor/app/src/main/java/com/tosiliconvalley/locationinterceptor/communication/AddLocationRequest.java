package com.tosiliconvalley.locationinterceptor.communication;

public class AddLocationRequest extends ServerInteraction {

    private Float lat;      // -36.6006744
    private Float lon;      // -58.450274
    private Integer usr;    // 3
    private Integer group;  // 2

    @SuppressWarnings("unused")
    public AddLocationRequest(String protocol, String host, Integer port, String resorce,
                              String method, Float lat, Float lon, Integer usr, Integer group) {

        super(protocol, host, port, resorce);
        this.lat = lat;
        this.lon = lon;
        this.usr = usr;
        this.group = group;
    }

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
    public Integer getUsr() {
        return usr;
    }
    @SuppressWarnings("unused")
    public void setUsr(Integer usr) {
        this.usr = usr;
    }

    @SuppressWarnings("unused")
    public Integer getGroup() {
        return group;
    }
    @SuppressWarnings("unused")
    public void setGroup(Integer group) {
        this.group = group;
    }

    @Override
    public String getUrl() {
        return getProtocol() + "://" + getHost() + ":" + getPort() + "/" + getResource() + "?" +
                "lat=" + getLat() + "&" +
                "lon=" + getLon() + "&" +
                "usr=" + getUsr() + "&" +
                "group=" + getGroup() + "&";
    }
}
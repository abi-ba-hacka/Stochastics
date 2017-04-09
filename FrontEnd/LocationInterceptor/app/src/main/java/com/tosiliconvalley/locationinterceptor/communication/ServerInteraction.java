package com.tosiliconvalley.locationinterceptor.communication;

/**
 * Created by equipo on 3/14/2017.
 */
public abstract class ServerInteraction {

    private String protocol;    // http://
    private String host;        // 192.168.0.254
    private Integer port;       // 8080
    private String resource;    // abInBev

    public ServerInteraction() {
    }

    public ServerInteraction(String protocol, String host, Integer port, String resource) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.resource = resource;
    }

    // getters and setters
    @SuppressWarnings("unused")
    public String getProtocol() {
        return protocol;
    }
    @SuppressWarnings("unused")
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @SuppressWarnings("unused")
    public String getHost() {
        return host;
    }
    @SuppressWarnings("unused")
    public void setHost(String host) {
        this.host = host;
    }

    @SuppressWarnings("unused")
    public Integer getPort() {
        return port;
    }
    @SuppressWarnings("unused")
    public void setPort(Integer port) {
        this.port = port;
    }

    @SuppressWarnings("unused")
    public String getResource() {
        return resource;
    }
    @SuppressWarnings("unused")
    public void setResource(String resource) {
        this.resource = resource;
    }

    @SuppressWarnings("unused")
    public abstract String getUrl();
}

package com.tosiliconvalley.locationinterceptor.communication;

import android.app.Service;

import com.google.gson.Gson;
import com.tosiliconvalley.locationinterceptor.data.AllUsersLocations;

/**
 * Created by equipo on 3/24/2017.
 */
public class AllUsersLocationRequest extends ServerInteraction {

    private AllUsersLocations allUsersLocations;


    public AllUsersLocationRequest(String protocol, String ip, Integer port, String service) {
        super(protocol, ip, port, service);
    }

    public AllUsersLocationRequest(String protocol, String ip, Integer port, String service,
                                   AllUsersLocations allUsersLocations) {
        super(protocol, ip, port, service);
        this.allUsersLocations = allUsersLocations;
    }

    @Override
    public String getUrl() {
        return getProtocol() + "://" + getHost() + ":" + getPort() + "/" + getResource();
    }

    public String getJsonData() {
        return new Gson().toJson(allUsersLocations);
    }
}

package com.tosiliconvalley.locationinterceptor.data;

import com.tosiliconvalley.locationinterceptor.data.UserLocation;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by equipo on 3/24/2017.
 */
public class AllUsersLocations {

    //private static final String METHOD = "abInBev";

    private List<UserLocation> usersLocationList;
    private Integer group; // 2


    public AllUsersLocations(Integer group) {
        this.group = group;
        usersLocationList = new LinkedList<UserLocation>();
    }

    public void addUserLocation(UserLocation userLocation) {
        usersLocationList.add(userLocation);
    }

    public void setUsersLocatonList(List<UserLocation> userLocationList) {
        this.usersLocationList = userLocationList;
    }
}

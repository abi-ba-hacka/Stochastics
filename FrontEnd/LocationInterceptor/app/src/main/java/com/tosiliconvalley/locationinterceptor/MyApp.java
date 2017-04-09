package com.tosiliconvalley.locationinterceptor;

import android.app.Application;
import android.os.StrictMode;
import android.util.SparseArray;

import com.tosiliconvalley.locationinterceptor.data.UserLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by equipo on 11/7/2016.
 */
public class MyApp extends Application {

    private static SparseArray<LinkedList<UserLocation>> groupSparse;
    private static HashSet<String> processedUsersName;

    @Override
    public void onCreate() {
        super.onCreate();

        groupSparse = new SparseArray<LinkedList<UserLocation>>();
        processedUsersName = new HashSet<String>();
    }

    public static void addUserLocation(Integer group, UserLocation userLocation) {
        if(groupSparse.get(group) == null)
            groupSparse.append(group, new LinkedList<UserLocation>());

        if(wasProcessed(userLocation.getName())) {
            if (MyApp.getGroupData(2) != null) {
                for (UserLocation ul : MyApp.getGroupData(2)) {
                    if( ul.getName().equals(userLocation.getName()) ) {
                        ul.setLat(userLocation.getLat());
                        ul.setLon(userLocation.getLon());
                    }
                }
            }
        }
        else {
            groupSparse.get(group).add(userLocation);
            processedUsersName.add(userLocation.getName().toString());
        }
    }

    public static List<UserLocation> getGroupData(Integer group) {
        return groupSparse.get(group);
    }

    public static boolean wasProcessed(String userName) {
        return processedUsersName.contains(userName);
    }

    public static void cleanGroupData(Integer group) {
        groupSparse.get(group).clear();
        processedUsersName.clear();
    }
}


package com.tosiliconvalley.locationinterceptor.communication;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by equipo on 3/11/2017.
 */
public class Connectivity {

    public static boolean isConnected(Context context){
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
}

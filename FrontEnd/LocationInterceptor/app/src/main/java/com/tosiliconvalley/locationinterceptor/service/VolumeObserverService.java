package com.tosiliconvalley.locationinterceptor.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by equipo on 4/7/2017.
 */
public class VolumeObserverService extends Service {

    private static final String LOGTAG = "VolumeObserverService";
    private boolean isRunning  = false;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        Log.i(LOGTAG, "Service onCreate");
        //isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOGTAG, "Service onStartCommand");

        /*
        getApplicationContext().getContentResolver().registerContentObserver(
                android.provider.Settings.System.CONTENT_URI, true, new VolumeContentObserver());
        */

        //Creating new thread for my service
        /*
        new Thread(new Runnable() {

            @Override
            public void run() {

                while(isRunning) {
                }

                stopSelf();
            }
        }).start();
        */

        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {

        Log.i(LOGTAG, "Service onDestroy");
        //isRunning = false;
    }


    private class VolumeContentObserver extends ContentObserver {
        private AudioManager audioManager;

        public VolumeContentObserver(Context context, Handler handler) {
            super(handler);
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return false;
        }

        @Override
        public void onChange(boolean selfChange) {
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

            Log.d(LOGTAG, "Volume now " + currentVolume);
        }
    }
}

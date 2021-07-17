package me.ngarak.cita.ads;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;

public class MyApplication extends Application {
    private final String TAG = getClass().getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, initializationStatus -> Log.d(TAG, "onInitializationComplete() called with: initializationStatus = [" + initializationStatus + "]"));
        new AppOpenManager(this);
    }
}

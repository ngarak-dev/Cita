package me.ngarak.cita.ads;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.Arrays;
import java.util.List;

import me.ngarak.cita.PacksCatalog;
import me.ngarak.cita.QuotesCatalog;

public class MyApplication extends Application {
    private final String TAG = getClass().getSimpleName();

    /** Pixel 8a device hash from logcat — only used in debug builds for test ads. */
    private static final String PIXEL_8A_TEST_DEVICE = "A332BE010AA94DDDDBB7529107F9467C";

    @Override
    public void onCreate() {
        super.onCreate();
        QuotesCatalog.init(this);
        PacksCatalog.init(this);
        // Ensure starter save credits exist for new installs (Phase 1 share ritual).
        new me.ngarak.cita.QuoteCredits(this);

        boolean debuggable = (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        if (debuggable) {
            List<String> testDeviceIds = Arrays.asList(
                    AdRequest.DEVICE_ID_EMULATOR,
                    PIXEL_8A_TEST_DEVICE
            );
            RequestConfiguration config = new RequestConfiguration.Builder()
                    .setTestDeviceIds(testDeviceIds)
                    .build();
            MobileAds.setRequestConfiguration(config);
            // Debug builds also override ad unit strings via src/debug/res/values/ad_units.xml
            Log.i(TAG, "DEBUG ads: Google sample units + test device "
                    + PIXEL_8A_TEST_DEVICE);
            Log.i(TAG, "DEBUG banner unit=" + getString(me.ngarak.cita.R.string.SMART_BANNER_AD_UNIT));
            Toast.makeText(this, "DEBUG: Google sample ads enabled", Toast.LENGTH_LONG).show();
        }

        // Phase 1: app-open ads paused — they fight the share ritual and first impression.
        // Re-enable only after retention/share metrics stabilize (see CITA_PRODUCT_STRATEGY.md).
        MobileAds.initialize(this, initializationStatus ->
                Log.d(TAG, "onInitializationComplete() called with: initializationStatus = ["
                        + initializationStatus + "]"));
        // new AppOpenManager(this);
    }
}

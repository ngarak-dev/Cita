package me.ngarak.cita.ads;

import android.content.Context;

import me.ngarak.cita.CitaPlus;

/** Central gate for showing ads vs Remove Ads / Plus entitlements. */
public final class AdsPolicy {
    private AdsPolicy() {
    }

    public static boolean shouldShowAds(Context context) {
        return !new CitaPlus(context).adsRemoved();
    }
}

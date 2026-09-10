package me.ngarak.cita;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Entitlements for growth monetization (Phase 3+).
 * Remove Ads IAP is wired via {@code RemoveAdsBilling} behind BuildConfig.ENABLE_REMOVE_ADS_IAP.
 */
public final class CitaPlus {
    private static final String PREFS = "cita_plus";
    private static final String KEY_PLUS = "plus_active";
    private static final String KEY_REFERRAL_UNLOCK = "referral_template_unlock";
    private static final String KEY_HIDE_WATERMARK = "hide_watermark";
    private static final String KEY_REMOVE_ADS = "remove_ads";

    private final SharedPreferences prefs;

    public CitaPlus(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public boolean isPlus() {
        return prefs.getBoolean(KEY_PLUS, false);
    }

    /** Debug / creator seeding / future Plus subscription. */
    public void setPlus(boolean enabled) {
        SharedPreferences.Editor ed = prefs.edit().putBoolean(KEY_PLUS, enabled);
        if (enabled) {
            ed.putBoolean(KEY_HIDE_WATERMARK, true).putBoolean(KEY_REMOVE_ADS, true);
        }
        ed.apply();
    }

    public boolean hideWatermark() {
        return isPlus() || prefs.getBoolean(KEY_HIDE_WATERMARK, false);
    }

    /** True when Plus or one-time Remove Ads purchase is active. */
    public boolean adsRemoved() {
        return isPlus() || prefs.getBoolean(KEY_REMOVE_ADS, false);
    }

    public void setAdsRemoved(boolean enabled) {
        prefs.edit().putBoolean(KEY_REMOVE_ADS, enabled).apply();
    }

    public boolean hasReferralTemplateUnlock() {
        return prefs.getBoolean(KEY_REFERRAL_UNLOCK, false) || isPlus();
    }

    public void unlockReferralTemplate() {
        prefs.edit().putBoolean(KEY_REFERRAL_UNLOCK, true).apply();
    }
}

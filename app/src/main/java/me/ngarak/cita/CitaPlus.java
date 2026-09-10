package me.ngarak.cita;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Entitlements for growth monetization (Phase 3).
 * Billing wiring comes later — local flags unlock watermark removal and bonus templates.
 */
public final class CitaPlus {
    private static final String PREFS = "cita_plus";
    private static final String KEY_PLUS = "plus_active";
    private static final String KEY_REFERRAL_UNLOCK = "referral_template_unlock";
    private static final String KEY_HIDE_WATERMARK = "hide_watermark";

    private final SharedPreferences prefs;

    public CitaPlus(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public boolean isPlus() {
        return prefs.getBoolean(KEY_PLUS, false);
    }

    /** Debug / creator seeding / future IAP. */
    public void setPlus(boolean enabled) {
        prefs.edit().putBoolean(KEY_PLUS, enabled).apply();
        if (enabled) {
            prefs.edit().putBoolean(KEY_HIDE_WATERMARK, true).apply();
        }
    }

    public boolean hideWatermark() {
        return isPlus() || prefs.getBoolean(KEY_HIDE_WATERMARK, false);
    }

    public boolean hasReferralTemplateUnlock() {
        return prefs.getBoolean(KEY_REFERRAL_UNLOCK, false) || isPlus();
    }

    public void unlockReferralTemplate() {
        prefs.edit().putBoolean(KEY_REFERRAL_UNLOCK, true).apply();
    }
}

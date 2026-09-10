package me.ngarak.cita;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Ad-free save credits. Bootstraps a small free allowance so the first
 * shares are not blocked behind an ad (Phase 1 — Card Studio path).
 */
public final class QuoteCredits {
    private static final String PREFS = "quote_views";
    private static final String KEY_CREDITS = "quote_views";
    private static final String KEY_BOOTSTRAPPED = "credits_bootstrapped_v1";
    private static final int STARTER_CREDITS = 3;

    private final SharedPreferences prefs;

    public QuoteCredits(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        bootstrapIfNeeded();
    }

    private void bootstrapIfNeeded() {
        if (!prefs.getBoolean(KEY_BOOTSTRAPPED, false)) {
            int current = prefs.getInt(KEY_CREDITS, 0);
            prefs.edit()
                    .putInt(KEY_CREDITS, Math.max(current, STARTER_CREDITS))
                    .putBoolean(KEY_BOOTSTRAPPED, true)
                    .apply();
        }
    }

    public int get() {
        return Math.max(0, prefs.getInt(KEY_CREDITS, 0));
    }

    public boolean canSaveWithoutAd() {
        return get() > 0;
    }

    public void add(int amount) {
        if (amount <= 0) return;
        prefs.edit().putInt(KEY_CREDITS, get() + amount).apply();
    }

    /** Decrements by 1, never below zero. */
    public void consumeOne() {
        int next = Math.max(0, get() - 1);
        prefs.edit().putInt(KEY_CREDITS, next).apply();
    }
}

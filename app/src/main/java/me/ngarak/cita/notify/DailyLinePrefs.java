package me.ngarak.cita.notify;

import android.content.Context;
import android.content.SharedPreferences;

/** Opt-in prefs for Daily Drop “Today’s line” notifications. */
public final class DailyLinePrefs {
    private static final String PREFS = "cita_notify";
    private static final String KEY_ENABLED = "todays_line_enabled";
    private static final String KEY_HOUR = "todays_line_hour";
    private static final String KEY_MINUTE = "todays_line_minute";

    private final SharedPreferences prefs;

    public DailyLinePrefs(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public boolean isEnabled() {
        return prefs.getBoolean(KEY_ENABLED, false);
    }

    public void setEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_ENABLED, enabled).apply();
    }

    public int hour() {
        return prefs.getInt(KEY_HOUR, 9);
    }

    public int minute() {
        return prefs.getInt(KEY_MINUTE, 0);
    }

    public void setTime(int hour, int minute) {
        prefs.edit().putInt(KEY_HOUR, hour).putInt(KEY_MINUTE, minute).apply();
    }
}

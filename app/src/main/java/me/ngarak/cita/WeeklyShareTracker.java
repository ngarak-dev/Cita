package me.ngarak.cita;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Local Weekly Shared Cards (WSC) metric — distinct share actions this ISO week.
 * North star: users who share ≥1 card per week.
 */
public final class WeeklyShareTracker {
    private static final String PREFS = "cita_wsc";
    private static final String KEY_WEEK = "week_id";
    private static final String KEY_COUNT = "week_count";
    private static final String KEY_STREAK = "streak_weeks";
    private static final String KEY_LAST_ACTIVE_WEEK = "last_active_week";

    private final SharedPreferences prefs;

    public WeeklyShareTracker(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        rollWeekIfNeeded();
    }

    public synchronized int getSharesThisWeek() {
        rollWeekIfNeeded();
        return prefs.getInt(KEY_COUNT, 0);
    }

    public synchronized int getStreakWeeks() {
        rollWeekIfNeeded();
        return prefs.getInt(KEY_STREAK, 0);
    }

    public synchronized int recordShare() {
        rollWeekIfNeeded();
        String week = currentWeekId();
        int count = prefs.getInt(KEY_COUNT, 0) + 1;
        String lastActive = prefs.getString(KEY_LAST_ACTIVE_WEEK, "");
        int streak = prefs.getInt(KEY_STREAK, 0);
        if (!week.equals(lastActive)) {
            if (isPreviousWeek(lastActive, week)) {
                streak = streak + 1;
            } else if (lastActive == null || lastActive.isEmpty()) {
                streak = 1;
            } else {
                streak = 1;
            }
            lastActive = week;
        }
        prefs.edit()
                .putString(KEY_WEEK, week)
                .putInt(KEY_COUNT, count)
                .putString(KEY_LAST_ACTIVE_WEEK, lastActive)
                .putInt(KEY_STREAK, streak)
                .apply();
        return count;
    }

    public String currentWeekId() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setMinimalDaysInFirstWeek(4);
        int year = cal.get(Calendar.YEAR);
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        // ISO-ish: adjust year for week 1 in Dec / week 52+ in Jan edge cases
        if (cal.get(Calendar.MONTH) == Calendar.JANUARY && week >= 52) {
            year -= 1;
        } else if (cal.get(Calendar.MONTH) == Calendar.DECEMBER && week == 1) {
            year += 1;
        }
        return year + "-W" + String.format(Locale.US, "%02d", week);
    }

    private void rollWeekIfNeeded() {
        String now = currentWeekId();
        String stored = prefs.getString(KEY_WEEK, "");
        if (!now.equals(stored)) {
            prefs.edit().putString(KEY_WEEK, now).putInt(KEY_COUNT, 0).apply();
        }
    }

    private static boolean isPreviousWeek(String last, String current) {
        if (last == null || last.isEmpty() || current == null) return false;
        // Simple adjacency: same year and week-1, or year boundary W52/W53 → W01
        try {
            String[] a = last.split("-W");
            String[] b = current.split("-W");
            int y1 = Integer.parseInt(a[0]);
            int w1 = Integer.parseInt(a[1]);
            int y2 = Integer.parseInt(b[0]);
            int w2 = Integer.parseInt(b[1]);
            if (y1 == y2 && w2 == w1 + 1) return true;
            if (y2 == y1 + 1 && w2 == 1 && w1 >= 52) return true;
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}

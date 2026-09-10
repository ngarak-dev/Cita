package me.ngarak.cita;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/** Day streak for opening / engaging with Daily Drop — retention ritual. */
public final class DailyDropStreak {
    private static final String PREFS = "cita_daily_streak";
    private static final String KEY_LAST_DAY = "last_day";
    private static final String KEY_STREAK = "streak";

    private final SharedPreferences prefs;

    public DailyDropStreak(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public synchronized int getStreak() {
        return prefs.getInt(KEY_STREAK, 0);
    }

    /** Call when user opens Daily Drop. Returns current streak after update. */
    public synchronized int recordOpen() {
        String today = dayId();
        String last = prefs.getString(KEY_LAST_DAY, "");
        int streak = prefs.getInt(KEY_STREAK, 0);
        if (today.equals(last)) {
            return streak;
        }
        if (isYesterday(last, today)) {
            streak = Math.max(1, streak) + 1;
        } else {
            streak = 1;
        }
        prefs.edit().putString(KEY_LAST_DAY, today).putInt(KEY_STREAK, streak).apply();
        return streak;
    }

    private static String dayId() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.US);
        return String.format(Locale.US, "%04d-%02d-%02d",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH));
    }

    private static boolean isYesterday(String last, String today) {
        if (last == null || last.isEmpty()) return false;
        try {
            String[] a = last.split("-");
            String[] b = today.split("-");
            Calendar cLast = Calendar.getInstance(TimeZone.getDefault(), Locale.US);
            cLast.set(Integer.parseInt(a[0]), Integer.parseInt(a[1]) - 1, Integer.parseInt(a[2]));
            Calendar cToday = Calendar.getInstance(TimeZone.getDefault(), Locale.US);
            cToday.set(Integer.parseInt(b[0]), Integer.parseInt(b[1]) - 1, Integer.parseInt(b[2]));
            cLast.add(Calendar.DAY_OF_YEAR, 1);
            return cLast.get(Calendar.YEAR) == cToday.get(Calendar.YEAR)
                    && cLast.get(Calendar.DAY_OF_YEAR) == cToday.get(Calendar.DAY_OF_YEAR);
        } catch (Exception e) {
            return false;
        }
    }
}

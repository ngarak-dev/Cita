package me.ngarak.cita.notify;

import android.content.Context;

import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/** Schedules the next local “Today’s line” notification. */
public final class DailyLineScheduler {
    private static final String WORK_NAME = "cita_daily_line";

    private DailyLineScheduler() {
    }

    public static void apply(Context context, boolean enabled) {
        DailyLinePrefs prefs = new DailyLinePrefs(context);
        prefs.setEnabled(enabled);
        if (enabled) {
            scheduleNext(context);
        } else {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME);
        }
    }

    public static void scheduleNext(Context context) {
        DailyLinePrefs prefs = new DailyLinePrefs(context);
        if (!prefs.isEnabled()) return;

        long delayMs = millisUntilNext(prefs.hour(), prefs.minute());
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(DailyLineWorker.class)
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .build();
        WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME, ExistingWorkPolicy.REPLACE, request);
    }

    static long millisUntilNext(int hour, int minute) {
        Calendar now = Calendar.getInstance();
        Calendar next = (Calendar) now.clone();
        next.set(Calendar.HOUR_OF_DAY, hour);
        next.set(Calendar.MINUTE, minute);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        if (!next.after(now)) {
            next.add(Calendar.DAY_OF_YEAR, 1);
        }
        return next.getTimeInMillis() - now.getTimeInMillis();
    }
}

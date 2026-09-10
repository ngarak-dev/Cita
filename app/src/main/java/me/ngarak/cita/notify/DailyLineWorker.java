package me.ngarak.cita.notify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import me.ngarak.cita.DailyDrop;
import me.ngarak.cita.QuotesCatalog;
import me.ngarak.cita.R;
import me.ngarak.cita.UserTaste;
import me.ngarak.cita.models.QuoteResponse;
import me.ngarak.cita.ui.SplashActivity;

public class DailyLineWorker extends Worker {
    public static final String CHANNEL_ID = "cita_daily_line";
    private static final int NOTIFICATION_ID = 4101;

    public DailyLineWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context ctx = getApplicationContext();
        DailyLinePrefs prefs = new DailyLinePrefs(ctx);
        if (!prefs.isEnabled()) {
            return Result.success();
        }

        QuotesCatalog.init(ctx);
        UserTaste taste = new UserTaste(ctx);
        QuoteResponse quote = DailyDrop.today(ctx, taste.lastMood(), taste.favoriteAnime());
        ensureChannel(ctx);

        Intent launch = new Intent(ctx, SplashActivity.class);
        launch.putExtra(SplashActivity.EXTRA_OPEN_DAILY_DROP, true);
        launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(
                ctx, 0, launch,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String title = ctx.getString(R.string.daily_line_notification_title);
        String body;
        if (quote != null && quote.getQuote() != null) {
            body = quote.getQuote();
            if (quote.getCharacter() != null) {
                body = body + "\n— " + quote.getCharacter();
            }
        } else {
            body = ctx.getString(R.string.daily_line_notification_fallback);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_cita_small)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        try {
            NotificationManagerCompat.from(ctx).notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException ignored) {
            // Missing POST_NOTIFICATIONS on API 33+.
        }

        DailyLineScheduler.scheduleNext(ctx);
        return Result.success();
    }

    static void ensureChannel(Context ctx) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                ctx.getString(R.string.daily_line_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(ctx.getString(R.string.daily_line_channel_desc));
        NotificationManager nm = ctx.getSystemService(NotificationManager.class);
        if (nm != null) nm.createNotificationChannel(channel);
    }
}

package me.ngarak.cita.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import me.ngarak.cita.DailyDrop;
import me.ngarak.cita.QuotesCatalog;
import me.ngarak.cita.R;
import me.ngarak.cita.UserTaste;
import me.ngarak.cita.models.QuoteResponse;
import me.ngarak.cita.ui.SplashActivity;

public class CitaDailyDropWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        QuotesCatalog.init(context);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void refreshAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] ids = manager.getAppWidgetIds(new ComponentName(context, CitaDailyDropWidget.class));
        if (ids == null || ids.length == 0) return;
        Intent intent = new Intent(context, CitaDailyDropWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_daily_drop);
        UserTaste taste = new UserTaste(context);
        QuoteResponse quote = DailyDrop.today(taste.lastMood(), taste.favoriteAnime());
        if (quote != null && quote.getQuote() != null) {
            views.setTextViewText(R.id.widgetQuote, quote.getQuote());
            String meta = (quote.getCharacter() != null ? quote.getCharacter() : "")
                    + (quote.getAnime() != null ? " · " + quote.getAnime() : "");
            views.setTextViewText(R.id.widgetMeta, meta);
        } else {
            views.setTextViewText(R.id.widgetQuote, context.getString(R.string.widget_empty));
            views.setTextViewText(R.id.widgetMeta, "");
        }

        Intent launch = new Intent(context, SplashActivity.class);
        PendingIntent pi = PendingIntent.getActivity(
                context, appWidgetId, launch,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widgetRoot, pi);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

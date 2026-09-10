package me.ngarak.cita.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.ngarak.cita.Mood;
import me.ngarak.cita.UserTaste;

/** Per-widget mood / anime override; falls back to {@link UserTaste}. */
public final class WidgetPrefs {
    private static final String PREFS = "cita_widget";

    private final SharedPreferences prefs;
    private final UserTaste taste;

    public WidgetPrefs(Context context) {
        Context app = context.getApplicationContext();
        prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        taste = new UserTaste(app);
    }

    public Mood moodFor(int appWidgetId) {
        String raw = prefs.getString(keyMood(appWidgetId), null);
        if (TextUtils.isEmpty(raw)) return taste.lastMood();
        try {
            return Mood.valueOf(raw);
        } catch (Exception e) {
            return taste.lastMood();
        }
    }

    public List<String> animeFor(int appWidgetId) {
        String anime = prefs.getString(keyAnime(appWidgetId), null);
        if (!TextUtils.isEmpty(anime)) {
            return Collections.singletonList(anime);
        }
        return taste.favoriteAnime();
    }

    public String rawAnime(int appWidgetId) {
        return prefs.getString(keyAnime(appWidgetId), "");
    }

    public String rawMood(int appWidgetId) {
        return prefs.getString(keyMood(appWidgetId), Mood.ALL.name());
    }

    public void save(int appWidgetId, Mood mood, String animeOrEmpty) {
        SharedPreferences.Editor ed = prefs.edit()
                .putString(keyMood(appWidgetId), mood != null ? mood.name() : Mood.ALL.name());
        if (TextUtils.isEmpty(animeOrEmpty)) {
            ed.remove(keyAnime(appWidgetId));
        } else {
            ed.putString(keyAnime(appWidgetId), animeOrEmpty);
        }
        ed.apply();
    }

    public void delete(int appWidgetId) {
        prefs.edit()
                .remove(keyMood(appWidgetId))
                .remove(keyAnime(appWidgetId))
                .apply();
    }

    public static List<String> animeChoices(Context context) {
        List<String> out = new ArrayList<>();
        out.add(""); // “Follow favorites”
        UserTaste taste = new UserTaste(context);
        for (String a : taste.favoriteAnime()) {
            if (!TextUtils.isEmpty(a) && !out.contains(a)) out.add(a);
        }
        for (String a : UserTaste.seedSuggestions()) {
            if (!out.contains(a)) out.add(a);
        }
        return out;
    }

    private static String keyMood(int id) {
        return "mood_" + id;
    }

    private static String keyAnime(int id) {
        return "anime_" + id;
    }
}

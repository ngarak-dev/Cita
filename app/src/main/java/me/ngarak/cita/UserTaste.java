package me.ngarak.cita;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Onboarding taste + last mood for personalization. */
public final class UserTaste {
    private static final String PREFS = "cita_taste";
    private static final String KEY_ONBOARDED = "onboarded_v1";
    private static final String KEY_ANIME = "favorite_anime";
    private static final String KEY_MOOD = "last_mood";

    private final SharedPreferences prefs;

    public UserTaste(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public boolean needsOnboarding() {
        return !prefs.getBoolean(KEY_ONBOARDED, false);
    }

    public void completeOnboarding(List<String> animeTitles) {
        Set<String> set = new HashSet<>();
        if (animeTitles != null) {
            for (String t : animeTitles) {
                if (!TextUtils.isEmpty(t)) set.add(t);
            }
        }
        prefs.edit()
                .putBoolean(KEY_ONBOARDED, true)
                .putStringSet(KEY_ANIME, set)
                .apply();
    }

    public void skipOnboarding() {
        prefs.edit().putBoolean(KEY_ONBOARDED, true).apply();
    }

    public List<String> favoriteAnime() {
        Set<String> set = prefs.getStringSet(KEY_ANIME, Collections.emptySet());
        return set == null ? new ArrayList<>() : new ArrayList<>(set);
    }

    public void setMood(Mood mood) {
        if (mood == null) return;
        prefs.edit().putString(KEY_MOOD, mood.name()).apply();
    }

    public Mood lastMood() {
        String raw = prefs.getString(KEY_MOOD, Mood.ALL.name());
        try {
            return Mood.valueOf(raw);
        } catch (Exception e) {
            return Mood.ALL;
        }
    }

    public static List<String> seedSuggestions() {
        return Arrays.asList(
                "Naruto",
                "Naruto Shippuden",
                "One Piece",
                "Attack on Titan",
                "Demon Slayer",
                "Jujutsu Kaisen",
                "My Hero Academia",
                "Death Note",
                "Fullmetal Alchemist",
                "Hunter x Hunter",
                "Frieren",
                "Solo Leveling"
        );
    }
}

package me.ngarak.cita;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import me.ngarak.cita.models.QuoteResponse;

/** Deterministic daily quote pick, biased by local taste when available. */
public final class DailyDrop {

    private DailyDrop() {
    }

    public static QuoteResponse today() {
        return today(null, Mood.ALL, null);
    }

    public static QuoteResponse today(Mood mood, List<String> preferredAnime) {
        return today(null, mood, preferredAnime);
    }

    public static QuoteResponse today(Context context, Mood mood, List<String> preferredAnime) {
        List<QuoteResponse> pool = QuotesCatalog.get().getQuotesForMood(mood, preferredAnime);
        if (pool.isEmpty()) {
            pool = QuotesCatalog.get().allQuotesCopy();
        }
        if (pool.isEmpty()) return null;

        if (context != null) {
            TasteModel taste = new TasteModel(context);
            List<QuoteResponse> ranked = taste.rank(pool, mood);
            // Rotate among top taste hits by day so it still changes daily.
            int top = Math.min(25, ranked.size());
            int index = Math.floorMod(daySeed(), top);
            return ranked.get(index);
        }

        int index = Math.floorMod(daySeed(), pool.size());
        return pool.get(index);
    }

    public static int daySeed() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        return cal.get(Calendar.YEAR) * 1000 + cal.get(Calendar.DAY_OF_YEAR);
    }

    public static String dayLabel() {
        Calendar cal = Calendar.getInstance();
        return String.format(Locale.getDefault(), "%1$tB %1$te", cal);
    }
}

package me.ngarak.cita;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import me.ngarak.cita.models.QuoteResponse;

/** Deterministic daily quote pick (UTC day seed). */
public final class DailyDrop {

    private DailyDrop() {
    }

    public static QuoteResponse today() {
        return today(Mood.ALL, null);
    }

    public static QuoteResponse today(Mood mood, List<String> preferredAnime) {
        List<QuoteResponse> pool = QuotesCatalog.get().getQuotesForMood(mood, preferredAnime);
        if (pool.isEmpty()) {
            pool = QuotesCatalog.get().allQuotesCopy();
        }
        if (pool.isEmpty()) return null;
        int seed = daySeed();
        int index = Math.floorMod(seed, pool.size());
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

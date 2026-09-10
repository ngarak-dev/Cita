package me.ngarak.cita;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.ngarak.cita.models.QuoteResponse;

/**
 * Local taste model (Phase 4 moat).
 * Learns from views / favorites / shares to re-rank discovery — no server required.
 */
public final class TasteModel {
    private static final String PREFS = "cita_taste_signals";
    private static final String KEY_JSON = "signals_v1";

    private static final double W_VIEW = 1.0;
    private static final double W_FAVORITE = 5.0;
    private static final double W_SHARE = 8.0;
    private static final double W_ANIME_AFFINITY = 3.0;
    private static final double W_MOOD = 2.0;

    private final SharedPreferences prefs;
    private final Map<String, Signal> byQuote = new HashMap<>();
    private final Map<String, Double> animeAffinity = new HashMap<>();

    public TasteModel(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        load();
    }

    public void recordView(QuoteResponse quote) {
        bump(quote, W_VIEW);
    }

    public void recordFavorite(QuoteResponse quote, boolean on) {
        if (quote == null) return;
        Signal s = signalFor(quote);
        s.favoriteScore = on ? W_FAVORITE : 0;
        bumpAnime(quote, on ? 2.0 : -1.0);
        persist();
    }

    public void recordShare(QuoteResponse quote) {
        bump(quote, W_SHARE);
    }

    public double score(QuoteResponse quote, Mood mood) {
        if (quote == null) return 0;
        Signal s = byQuote.get(FavoritesStore.keyOf(quote));
        double score = s == null ? 0 : s.score;
        if (quote.getAnime() != null) {
            Double aff = animeAffinity.get(quote.getAnime().toLowerCase(Locale.US));
            if (aff != null) score += aff * W_ANIME_AFFINITY;
        }
        if (mood != null && mood != Mood.ALL && MoodMatcher.matches(quote, mood)) {
            score += W_MOOD;
        }
        return score;
    }

    /** Higher taste score first; stable shuffle-like tie-break via hash. */
    public List<QuoteResponse> rank(List<QuoteResponse> input, Mood mood) {
        if (input == null || input.isEmpty()) return new ArrayList<>();
        List<QuoteResponse> copy = new ArrayList<>(input);
        Collections.sort(copy, Comparator
                .comparingDouble((QuoteResponse q) -> score(q, mood)).reversed()
                .thenComparingInt(q -> FavoritesStore.keyOf(q).hashCode()));
        return copy;
    }

    private void bump(QuoteResponse quote, double amount) {
        if (quote == null || TextUtils.isEmpty(quote.getQuote())) return;
        Signal s = signalFor(quote);
        s.score += amount;
        bumpAnime(quote, amount * 0.15);
        persist();
    }

    private void bumpAnime(QuoteResponse quote, double delta) {
        if (quote.getAnime() == null) return;
        String key = quote.getAnime().toLowerCase(Locale.US);
        animeAffinity.put(key, animeAffinity.getOrDefault(key, 0.0) + delta);
    }

    private Signal signalFor(QuoteResponse quote) {
        String key = FavoritesStore.keyOf(quote);
        Signal s = byQuote.get(key);
        if (s == null) {
            s = new Signal();
            byQuote.put(key, s);
        }
        return s;
    }

    private void load() {
        String raw = prefs.getString(KEY_JSON, null);
        if (TextUtils.isEmpty(raw)) return;
        try {
            JSONObject root = new JSONObject(raw);
            JSONObject quotes = root.optJSONObject("quotes");
            if (quotes != null) {
                for (java.util.Iterator<String> it = quotes.keys(); it.hasNext(); ) {
                    String k = it.next();
                    JSONObject o = quotes.getJSONObject(k);
                    Signal s = new Signal();
                    s.score = o.optDouble("score", 0);
                    s.favoriteScore = o.optDouble("fav", 0);
                    byQuote.put(k, s);
                }
            }
            JSONObject anime = root.optJSONObject("anime");
            if (anime != null) {
                for (java.util.Iterator<String> it = anime.keys(); it.hasNext(); ) {
                    String k = it.next();
                    animeAffinity.put(k, anime.getDouble(k));
                }
            }
        } catch (JSONException ignored) {
        }
    }

    private void persist() {
        try {
            JSONObject root = new JSONObject();
            JSONObject quotes = new JSONObject();
            for (Map.Entry<String, Signal> e : byQuote.entrySet()) {
                JSONObject o = new JSONObject();
                o.put("score", e.getValue().score + e.getValue().favoriteScore);
                o.put("fav", e.getValue().favoriteScore);
                quotes.put(e.getKey(), o);
            }
            JSONObject anime = new JSONObject();
            for (Map.Entry<String, Double> e : animeAffinity.entrySet()) {
                anime.put(e.getKey(), e.getValue());
            }
            root.put("quotes", quotes);
            root.put("anime", anime);
            prefs.edit().putString(KEY_JSON, root.toString()).apply();
        } catch (JSONException ignored) {
        }
    }

    private static final class Signal {
        double score;
        double favoriteScore;
    }
}

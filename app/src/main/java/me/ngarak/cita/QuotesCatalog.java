package me.ngarak.cita;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import me.ngarak.cita.models.QuoteResponse;

/**
 * Local quote catalog shipped in assets/quotes.json.
 * Mirrors the old Anime Chan response behavior used by Cita.
 * Call {@link #init(Context)} from Application.onCreate().
 */
public final class QuotesCatalog {
    private static final String TAG = "QuotesCatalog";
    private static final String ASSET_FILE = "quotes.json";
    private static final int PAGE_SIZE = 10;

    private static Context appContext;
    private static QuotesCatalog instance;

    private final List<QuoteResponse> quotes = new ArrayList<>();
    private final List<String> animeTitles = new ArrayList<>();
    private final Random random = new Random();

    private QuotesCatalog(Context context) {
        load(context);
    }

    public static synchronized void init(Context context) {
        appContext = context.getApplicationContext();
        if (instance == null) {
            instance = new QuotesCatalog(appContext);
        }
    }

    public static synchronized QuotesCatalog get() {
        if (instance == null) {
            if (appContext == null) {
                throw new IllegalStateException("QuotesCatalog.init(context) must be called first");
            }
            instance = new QuotesCatalog(appContext);
        }
        return instance;
    }

    private void load(Context context) {
        try (InputStream in = context.getAssets().open(ASSET_FILE);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            Type type = TypeToken.getParameterized(List.class, QuoteResponse.class).getType();
            List<QuoteResponse> parsed = new Gson().fromJson(reader, type);
            if (parsed != null) {
                quotes.addAll(parsed);
            }
            Set<String> titles = new HashSet<>();
            for (QuoteResponse q : quotes) {
                if (q.getAnime() != null && !q.getAnime().trim().isEmpty()) {
                    titles.add(q.getAnime());
                }
            }
            animeTitles.addAll(titles);
            Collections.sort(animeTitles, String::compareToIgnoreCase);
            Log.d(TAG, "Loaded " + quotes.size() + " quotes across " + animeTitles.size() + " anime");
        } catch (Exception e) {
            Log.e(TAG, "Failed to load " + ASSET_FILE, e);
        }
    }

    public List<QuoteResponse> getRandomQuotes() {
        if (quotes.isEmpty()) {
            return Collections.emptyList();
        }
        List<QuoteResponse> copy = new ArrayList<>(quotes);
        Collections.shuffle(copy, random);
        return new ArrayList<>(copy.subList(0, Math.min(PAGE_SIZE, copy.size())));
    }

    public List<QuoteResponse> getQuotes(int page) {
        return pageSlice(quotes, page);
    }

    public List<QuoteResponse> getQuotesByAnime(String title, int page) {
        if (title == null || title.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String needle = title.trim().toLowerCase(Locale.US);
        List<QuoteResponse> filtered = new ArrayList<>();
        for (QuoteResponse q : quotes) {
            if (q.getAnime() == null) continue;
            String anime = q.getAnime().toLowerCase(Locale.US);
            if (anime.equals(needle) || anime.contains(needle)) {
                filtered.add(q);
            }
        }
        return pageSlice(filtered, page);
    }

    public List<String> getAvailableAnime() {
        return new ArrayList<>(animeTitles);
    }

    public List<QuoteResponse> allQuotesCopy() {
        return new ArrayList<>(quotes);
    }

    public int getQuoteCount() {
        return quotes.size();
    }

    public int getAnimeCount() {
        return animeTitles.size();
    }

    /** Random page, biased toward preferred anime when provided. */
    public List<QuoteResponse> getRandomQuotes(List<String> preferredAnime) {
        if (quotes.isEmpty()) {
            return Collections.emptyList();
        }
        List<QuoteResponse> preferred = new ArrayList<>();
        if (preferredAnime != null && !preferredAnime.isEmpty()) {
            for (QuoteResponse q : quotes) {
                if (q.getAnime() == null) continue;
                String anime = q.getAnime().toLowerCase(Locale.US);
                for (String pref : preferredAnime) {
                    if (pref == null) continue;
                    String p = pref.toLowerCase(Locale.US);
                    if (anime.equals(p) || anime.contains(p) || p.contains(anime)) {
                        preferred.add(q);
                        break;
                    }
                }
            }
        }
        List<QuoteResponse> pool = preferred.size() >= PAGE_SIZE ? preferred : new ArrayList<>(quotes);
        List<QuoteResponse> copy = new ArrayList<>(pool);
        Collections.shuffle(copy, random);
        return new ArrayList<>(copy.subList(0, Math.min(PAGE_SIZE, copy.size())));
    }

    public List<QuoteResponse> getQuotesForMood(Mood mood, List<String> preferredAnime) {
        List<QuoteResponse> source = quotes;
        if (preferredAnime != null && !preferredAnime.isEmpty()) {
            List<QuoteResponse> filtered = new ArrayList<>();
            for (QuoteResponse q : quotes) {
                if (matchesPreferred(q, preferredAnime)) filtered.add(q);
            }
            if (!filtered.isEmpty()) source = filtered;
        }
        if (mood == null || mood == Mood.ALL) {
            return new ArrayList<>(source);
        }
        List<QuoteResponse> out = new ArrayList<>();
        for (QuoteResponse q : source) {
            if (MoodMatcher.matches(q, mood)) out.add(q);
        }
        return out.isEmpty() ? new ArrayList<>(source) : out;
    }

    public List<QuoteResponse> getRandomByMood(Mood mood, List<String> preferredAnime) {
        List<QuoteResponse> pool = getQuotesForMood(mood, preferredAnime);
        if (pool.isEmpty()) return Collections.emptyList();
        List<QuoteResponse> copy = new ArrayList<>(pool);
        Collections.shuffle(copy, random);
        return new ArrayList<>(copy.subList(0, Math.min(PAGE_SIZE, copy.size())));
    }

    private static boolean matchesPreferred(QuoteResponse q, List<String> preferredAnime) {
        if (q.getAnime() == null) return false;
        String anime = q.getAnime().toLowerCase(Locale.US);
        for (String pref : preferredAnime) {
            if (pref == null) continue;
            String p = pref.toLowerCase(Locale.US);
            if (anime.equals(p) || anime.contains(p) || p.contains(anime)) return true;
        }
        return false;
    }

    /** Filter anime titles by substring (case-insensitive). */
    public List<String> searchAnime(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAvailableAnime();
        }
        String needle = query.trim().toLowerCase(Locale.US);
        List<String> out = new ArrayList<>();
        for (String title : animeTitles) {
            if (title.toLowerCase(Locale.US).contains(needle)) {
                out.add(title);
            }
        }
        return out;
    }

    /**
     * Search quotes by anime, character, or quote text.
     * Returns a page of matches (same page size as list browsing).
     */
    public List<QuoteResponse> searchQuotes(String query, int page) {
        if (query == null || query.trim().isEmpty()) {
            return getQuotes(page);
        }
        String needle = query.trim().toLowerCase(Locale.US);
        List<QuoteResponse> filtered = new ArrayList<>();
        for (QuoteResponse q : quotes) {
            if (matches(q, needle)) {
                filtered.add(q);
            }
        }
        return pageSlice(filtered, page);
    }

    public int countSearchQuotes(String query) {
        if (query == null || query.trim().isEmpty()) {
            return quotes.size();
        }
        String needle = query.trim().toLowerCase(Locale.US);
        int count = 0;
        for (QuoteResponse q : quotes) {
            if (matches(q, needle)) count++;
        }
        return count;
    }

    private static boolean matches(QuoteResponse q, String needle) {
        return contains(q.getAnime(), needle)
                || contains(q.getCharacter(), needle)
                || contains(q.getQuote(), needle);
    }

    private static boolean contains(String haystack, String needle) {
        return haystack != null && haystack.toLowerCase(Locale.US).contains(needle);
    }

    private static List<QuoteResponse> pageSlice(List<QuoteResponse> source, int page) {
        int p = Math.max(1, page);
        int start = (p - 1) * PAGE_SIZE;
        if (start >= source.size()) {
            return Collections.emptyList();
        }
        int end = Math.min(start + PAGE_SIZE, source.size());
        return new ArrayList<>(source.subList(start, end));
    }
}

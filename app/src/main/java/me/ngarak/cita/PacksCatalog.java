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
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import me.ngarak.cita.models.QuotePack;
import me.ngarak.cita.models.QuoteResponse;

/** Curated seasonal / editorial packs (Phase 4 content moat). */
public final class PacksCatalog {
    private static final String TAG = "PacksCatalog";
    private static final String ASSET = "packs.json";

    private static PacksCatalog instance;
    private final List<QuotePack> packs = new ArrayList<>();

    private PacksCatalog(Context context) {
        load(context);
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new PacksCatalog(context.getApplicationContext());
        }
    }

    public static synchronized PacksCatalog get() {
        if (instance == null) {
            throw new IllegalStateException("PacksCatalog.init must be called first");
        }
        return instance;
    }

    private void load(Context context) {
        try (InputStream in = context.getAssets().open(ASSET);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            Type type = new TypeToken<List<QuotePack>>() {}.getType();
            List<QuotePack> parsed = new Gson().fromJson(reader, type);
            if (parsed != null) packs.addAll(parsed);
            Log.d(TAG, "Loaded " + packs.size() + " packs");
        } catch (Exception e) {
            Log.e(TAG, "Failed to load packs", e);
        }
    }

    public List<QuotePack> allPacks() {
        return new ArrayList<>(packs);
    }

    /** Packs tagged for the current calendar month, else featured. */
    public List<QuotePack> featuredNow() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        int month = cal.get(Calendar.MONTH) + 1;
        List<QuotePack> seasonal = new ArrayList<>();
        List<QuotePack> featured = new ArrayList<>();
        for (QuotePack p : packs) {
            if (p.months != null) {
                for (int m : p.months) {
                    if (m == month) {
                        seasonal.add(p);
                        break;
                    }
                }
            }
            if (p.featured) featured.add(p);
        }
        if (!seasonal.isEmpty()) return seasonal;
        if (!featured.isEmpty()) return featured;
        return allPacks();
    }

    public List<QuoteResponse> resolveQuotes(QuotePack pack) {
        if (pack == null || pack.quoteKeys == null) return Collections.emptyList();
        List<QuoteResponse> all = QuotesCatalog.get().allQuotesCopy();
        List<QuoteResponse> out = new ArrayList<>();
        for (String key : pack.quoteKeys) {
            if (key == null) continue;
            String needle = key.toLowerCase(Locale.US);
            for (QuoteResponse q : all) {
                if (matchesKey(q, needle)) {
                    out.add(q);
                    break;
                }
            }
        }
        return out;
    }

    private static boolean matchesKey(QuoteResponse q, String needle) {
        // key formats: "character|substring" or plain substring of quote
        if (needle.contains("|")) {
            String[] parts = needle.split("\\|", 2);
            String character = parts[0].trim();
            String quotePart = parts.length > 1 ? parts[1].trim() : "";
            boolean charOk = q.getCharacter() != null
                    && q.getCharacter().toLowerCase(Locale.US).contains(character);
            boolean quoteOk = quotePart.isEmpty()
                    || (q.getQuote() != null && q.getQuote().toLowerCase(Locale.US).contains(quotePart));
            return charOk && quoteOk;
        }
        return (q.getQuote() != null && q.getQuote().toLowerCase(Locale.US).contains(needle))
                || (q.getCharacter() != null && q.getCharacter().toLowerCase(Locale.US).contains(needle));
    }
}

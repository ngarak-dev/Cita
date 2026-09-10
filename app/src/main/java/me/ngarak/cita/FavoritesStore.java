package me.ngarak.cita;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.ngarak.cita.models.QuoteResponse;

/** Local favorites — SharedPreferences + Gson (no account yet). */
public final class FavoritesStore {
    private static final String PREFS = "cita_favorites";
    private static final String KEY = "favorites_json";

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();
    private final Type listType = new TypeToken<List<QuoteResponse>>() {}.getType();

    public FavoritesStore(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static String keyOf(QuoteResponse q) {
        if (q == null) return "";
        return safe(q.getAnime()) + "\u0001" + safe(q.getCharacter()) + "\u0001" + safe(q.getQuote());
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    public synchronized List<QuoteResponse> getAll() {
        String json = prefs.getString(KEY, null);
        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        }
        List<QuoteResponse> list = gson.fromJson(json, listType);
        return list != null ? new ArrayList<>(list) : new ArrayList<>();
    }

    public synchronized boolean contains(QuoteResponse quote) {
        String target = keyOf(quote);
        for (QuoteResponse q : getAll()) {
            if (keyOf(q).equals(target)) return true;
        }
        return false;
    }

    public synchronized boolean toggle(QuoteResponse quote) {
        if (quote == null || TextUtils.isEmpty(quote.getQuote())) {
            return false;
        }
        List<QuoteResponse> list = getAll();
        String target = keyOf(quote);
        boolean removed = false;
        for (Iterator<QuoteResponse> it = list.iterator(); it.hasNext(); ) {
            if (keyOf(it.next()).equals(target)) {
                it.remove();
                removed = true;
                break;
            }
        }
        if (!removed) {
            list.add(0, new QuoteResponse(quote.getAnime(), quote.getCharacter(), quote.getQuote()));
        }
        persist(list);
        return !removed;
    }

    private void persist(List<QuoteResponse> list) {
        prefs.edit().putString(KEY, gson.toJson(list)).apply();
    }
}

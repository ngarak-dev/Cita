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

/** Last N quotes viewed — local return path for D7. */
public final class RecentlyViewedStore {
    private static final String PREFS = "cita_recent";
    private static final String KEY = "recent_json";
    private static final int MAX = 12;

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();
    private final Type listType = new TypeToken<List<QuoteResponse>>() {}.getType();

    public RecentlyViewedStore(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public synchronized void record(QuoteResponse quote) {
        if (quote == null || TextUtils.isEmpty(quote.getQuote())) return;
        List<QuoteResponse> list = getAll();
        String key = FavoritesStore.keyOf(quote);
        for (Iterator<QuoteResponse> it = list.iterator(); it.hasNext(); ) {
            if (FavoritesStore.keyOf(it.next()).equals(key)) it.remove();
        }
        list.add(0, new QuoteResponse(quote.getAnime(), quote.getCharacter(), quote.getQuote()));
        while (list.size() > MAX) list.remove(list.size() - 1);
        prefs.edit().putString(KEY, gson.toJson(list)).apply();
    }

    public synchronized List<QuoteResponse> getAll() {
        String json = prefs.getString(KEY, null);
        if (TextUtils.isEmpty(json)) return new ArrayList<>();
        List<QuoteResponse> list = gson.fromJson(json, listType);
        return list != null ? new ArrayList<>(list) : new ArrayList<>();
    }
}

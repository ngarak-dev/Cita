package me.ngarak.cita;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import me.ngarak.cita.models.QuoteResponse;

/** Named favorite collections — SharedPreferences + Gson (local, no account). */
public final class CollectionsStore {
    private static final String PREFS = "cita_collections";
    private static final String KEY = "collections_json";
    private static final String KEY_SEEDED = "defaults_seeded";

    public static final class Collection {
        public String id;
        public String name;
        public List<String> quoteKeys = new ArrayList<>();

        public Collection() {
        }

        public Collection(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();
    private final Type listType = new TypeToken<List<Collection>>() {}.getType();

    public CollectionsStore(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        ensureDefaults();
    }

    private void ensureDefaults() {
        if (prefs.getBoolean(KEY_SEEDED, false)) return;
        List<Collection> list = getAll();
        if (list.isEmpty()) {
            list.add(new Collection(UUID.randomUUID().toString(), "My resolve"));
            list.add(new Collection(UUID.randomUUID().toString(), "Cry lines"));
            persist(list);
        }
        prefs.edit().putBoolean(KEY_SEEDED, true).apply();
    }

    public synchronized List<Collection> getAll() {
        String json = prefs.getString(KEY, null);
        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        }
        List<Collection> list = gson.fromJson(json, listType);
        if (list == null) return new ArrayList<>();
        for (Collection c : list) {
            if (c.quoteKeys == null) c.quoteKeys = new ArrayList<>();
        }
        return new ArrayList<>(list);
    }

    public synchronized Collection create(String name) {
        if (TextUtils.isEmpty(name)) return null;
        String trimmed = name.trim();
        if (trimmed.isEmpty()) return null;
        List<Collection> list = getAll();
        Collection c = new Collection(UUID.randomUUID().toString(), trimmed);
        list.add(0, c);
        persist(list);
        return c;
    }

    public synchronized Collection findById(String id) {
        if (id == null) return null;
        for (Collection c : getAll()) {
            if (id.equals(c.id)) return c;
        }
        return null;
    }

    public synchronized boolean addQuote(String collectionId, QuoteResponse quote) {
        if (quote == null || collectionId == null) return false;
        String key = FavoritesStore.keyOf(quote);
        if (key.isEmpty()) return false;
        List<Collection> list = getAll();
        for (Collection c : list) {
            if (!collectionId.equals(c.id)) continue;
            LinkedHashSet<String> set = new LinkedHashSet<>(c.quoteKeys);
            if (!set.add(key)) {
                return false;
            }
            c.quoteKeys = new ArrayList<>(set);
            persist(list);
            return true;
        }
        return false;
    }

    public synchronized boolean removeQuote(String collectionId, QuoteResponse quote) {
        if (quote == null || collectionId == null) return false;
        String key = FavoritesStore.keyOf(quote);
        List<Collection> list = getAll();
        for (Collection c : list) {
            if (!collectionId.equals(c.id)) continue;
            boolean removed = c.quoteKeys.remove(key);
            if (removed) persist(list);
            return removed;
        }
        return false;
    }

    public synchronized boolean delete(String collectionId) {
        if (collectionId == null) return false;
        List<Collection> list = getAll();
        boolean removed = false;
        for (Iterator<Collection> it = list.iterator(); it.hasNext(); ) {
            if (collectionId.equals(it.next().id)) {
                it.remove();
                removed = true;
                break;
            }
        }
        if (removed) persist(list);
        return removed;
    }

    public synchronized List<QuoteResponse> quotesIn(String collectionId, List<QuoteResponse> favorites) {
        Collection c = findById(collectionId);
        if (c == null || favorites == null) return new ArrayList<>();
        LinkedHashSet<String> keys = new LinkedHashSet<>(c.quoteKeys);
        List<QuoteResponse> out = new ArrayList<>();
        for (QuoteResponse q : favorites) {
            if (keys.contains(FavoritesStore.keyOf(q))) {
                out.add(q);
            }
        }
        return out;
    }

    public synchronized List<Collection> collectionsContaining(QuoteResponse quote) {
        String key = FavoritesStore.keyOf(quote);
        List<Collection> out = new ArrayList<>();
        if (key.isEmpty()) return out;
        for (Collection c : getAll()) {
            if (c.quoteKeys.contains(key)) out.add(c);
        }
        return out;
    }

    private void persist(List<Collection> list) {
        prefs.edit().putString(KEY, gson.toJson(list)).apply();
    }
}

package me.ngarak.cita;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.List;

import me.ngarak.cita.models.QuoteResponse;

/** Shared “Add to collection” picker for detail, studio sheet, and list rows. */
public final class CollectionPicker {

    private CollectionPicker() {
    }

    public static void show(@NonNull Activity activity, @NonNull QuoteResponse quote) {
        show(activity, quote, null);
    }

    public static void show(@NonNull Activity activity, @NonNull QuoteResponse quote,
                            @Nullable Runnable afterChange) {
        CollectionsStore collections = new CollectionsStore(activity);
        FavoritesStore favorites = new FavoritesStore(activity);
        List<CollectionsStore.Collection> cols = collections.getAll();
        if (cols.isEmpty()) {
            Toast.makeText(activity, R.string.create_collection_first, Toast.LENGTH_SHORT).show();
            return;
        }
        String[] names = new String[cols.size()];
        for (int i = 0; i < cols.size(); i++) {
            names[i] = cols.get(i).name;
        }
        new AlertDialog.Builder(activity)
                .setTitle(R.string.add_to_collection)
                .setItems(names, (d, which) -> {
                    CollectionsStore.Collection c = cols.get(which);
                    boolean added = collections.addQuote(c.id, quote);
                    if (added && !favorites.contains(quote)) {
                        favorites.toggle(quote);
                    }
                    Toast.makeText(activity,
                            added ? activity.getString(R.string.added_to_collection, c.name)
                                    : activity.getString(R.string.already_in_collection, c.name),
                            Toast.LENGTH_SHORT).show();
                    if (afterChange != null) afterChange.run();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}

package me.ngarak.cita.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.google.android.material.button.MaterialButton;

import me.ngarak.cita.CollectionsStore;
import me.ngarak.cita.FavoritesStore;
import me.ngarak.cita.Mood;
import me.ngarak.cita.MoodMatcher;
import me.ngarak.cita.QuoteAnalytics;
import me.ngarak.cita.QuoteSheetController;
import me.ngarak.cita.R;
import me.ngarak.cita.TasteModel;
import me.ngarak.cita.databinding.ActivityQuoteDetailBinding;
import me.ngarak.cita.models.QuoteResponse;
import me.ngarak.cita.perm;
import me.ngarak.cita.visual.CoverArt;

/** Immersive full-screen quote — Favorite / Share (opens Card Studio sheet). */
public class QuoteDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ANIME = "extra_anime";
    public static final String EXTRA_CHARACTER = "extra_character";
    public static final String EXTRA_QUOTE = "extra_quote";

    public static Intent intent(@NonNull Context context, @NonNull QuoteResponse quote) {
        Intent i = new Intent(context, QuoteDetailActivity.class);
        i.putExtra(EXTRA_ANIME, quote.getAnime());
        i.putExtra(EXTRA_CHARACTER, quote.getCharacter());
        i.putExtra(EXTRA_QUOTE, quote.getQuote());
        return i;
    }

    private ActivityQuoteDetailBinding binding;
    private QuoteResponse quote;
    private FavoritesStore favorites;
    private CollectionsStore collections;
    private QuoteAnalytics analytics;
    private TasteModel taste;
    private QuoteSheetController sheetController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivityQuoteDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String anime = getIntent().getStringExtra(EXTRA_ANIME);
        String character = getIntent().getStringExtra(EXTRA_CHARACTER);
        String text = getIntent().getStringExtra(EXTRA_QUOTE);
        if (text == null || text.isEmpty()) {
            finish();
            return;
        }
        quote = new QuoteResponse(anime, character, text);

        favorites = new FavoritesStore(this);
        collections = new CollectionsStore(this);
        analytics = new QuoteAnalytics(this);
        taste = new TasteModel(this);
        analytics.quoteView(quote);
        taste.recordView(quote);

        sheetController = new QuoteSheetController(new QuoteSheetController.Host() {
            @NonNull
            @Override
            public Activity activity() {
                return QuoteDetailActivity.this;
            }

            @Override
            public boolean isActive() {
                return !isFinishing();
            }

            @Override
            public void requestStoragePermission() {
                new perm().reQuestStorage(QuoteDetailActivity.this);
            }
        });

        CoverArt.applyCover(binding.heroBg, quote.getAnime());
        CoverArt.applyLetterOverlay(binding.heroLetter,
                quote.getAnime() != null ? quote.getAnime() : quote.getCharacter());

        binding.quoteText.setText(quote.getQuote());
        String meta = (character != null ? character : "")
                + (anime != null && !anime.isEmpty() ? " · " + anime : "");
        binding.metaText.setText(meta);

        Mood mood = MoodMatcher.detect(quote);
        if (mood != Mood.ALL) {
            binding.moodTag.setVisibility(View.VISIBLE);
            binding.moodTag.setText(mood.titleRes);
        }

        updateFavoriteUi(favorites.contains(quote));
        binding.btnClose.setOnClickListener(v -> finish());
        binding.btnFavorite.setOnClickListener(v -> {
            boolean on = favorites.toggle(quote);
            updateFavoriteUi(on);
            analytics.favoriteToggle(quote, on);
            taste.recordFavorite(quote, on);
            Toast.makeText(this,
                    on ? R.string.added_to_favorites : R.string.removed_from_favorites,
                    Toast.LENGTH_SHORT).show();
            if (on) {
                showAddToCollection();
            }
        });
        binding.btnFavorite.setOnLongClickListener(v -> {
            showAddToCollection();
            return true;
        });
        binding.btnShare.setOnClickListener(v -> sheetController.open(quote));
    }

    private void showAddToCollection() {
        java.util.List<CollectionsStore.Collection> cols = collections.getAll();
        if (cols.isEmpty()) {
            Toast.makeText(this, R.string.create_collection_first, Toast.LENGTH_SHORT).show();
            return;
        }
        String[] names = new String[cols.size()];
        for (int i = 0; i < cols.size(); i++) {
            names[i] = cols.get(i).name;
        }
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.add_to_collection)
                .setItems(names, (d, which) -> {
                    CollectionsStore.Collection c = cols.get(which);
                    boolean added = collections.addQuote(c.id, quote);
                    if (added && !favorites.contains(quote)) {
                        favorites.toggle(quote);
                        updateFavoriteUi(true);
                    }
                    Toast.makeText(this,
                            added ? getString(R.string.added_to_collection, c.name)
                                    : getString(R.string.already_in_collection, c.name),
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void updateFavoriteUi(boolean on) {
        MaterialButton btn = binding.btnFavorite;
        btn.setText(on ? R.string.favorited : R.string.favorite);
        btn.setIconResource(on ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
    }
}

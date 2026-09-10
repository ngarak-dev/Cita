package me.ngarak.cita;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import me.ngarak.cita.ads.QuoteRewardAd;
import me.ngarak.cita.databinding.LayoutBgBottomSheetBinding;
import me.ngarak.cita.models.QuoteResponse;
import me.ngarak.layout_image.ActionListeners;
import me.ngarak.layout_image.ViewToImage;

/**
 * Shared quote sheet: view → favorite / copy / save→share.
 * Used by Quotes, Random, Anime detail, and Favorites.
 */
public final class QuoteSheetController {

    public interface Host {
        @NonNull Activity activity();

        /** False when fragment is detached. */
        boolean isActive();

        void requestStoragePermission();
    }

    private final Host host;
    private final QuoteCredits credits;
    private final FavoritesStore favorites;
    private final QuoteAnalytics analytics;
    private final QuoteRewardAd rewardAd = new QuoteRewardAd();

    private BottomSheetDialog bottomSheetDialog;
    private AlertDialog consentDialog;
    private QuoteResponse currentQuote;

    public QuoteSheetController(@NonNull Host host) {
        this.host = host;
        Context app = host.activity().getApplicationContext();
        this.credits = new QuoteCredits(app);
        this.favorites = new FavoritesStore(app);
        this.analytics = new QuoteAnalytics(app);
    }

    public void open(@NonNull QuoteResponse quote) {
        currentQuote = quote;
        analytics.quoteView(quote);

        Activity activity = host.activity();
        bottomSheetDialog = new BottomSheetDialog(activity);
        LayoutBgBottomSheetBinding binding =
                LayoutBgBottomSheetBinding.inflate(LayoutInflater.from(activity));
        bottomSheetDialog.setContentView(binding.getRoot());
        binding.setQuote(quote);
        updateFavoriteButton(binding, favorites.contains(quote));
        bottomSheetDialog.show();

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);
        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                binding.adView.setVisibility(View.GONE);
            }
        });

        binding.copyQuoteBtn.setOnClickListener(v -> copyQuote(quote));
        binding.favoriteBtn.setOnClickListener(v -> {
            boolean on = favorites.toggle(quote);
            updateFavoriteButton(binding, on);
            analytics.favoriteToggle(quote, on);
            Toast.makeText(activity,
                    on ? R.string.added_to_favorites : R.string.removed_from_favorites,
                    Toast.LENGTH_SHORT).show();
        });
        binding.saveQuoteBtn.setOnClickListener(v -> onSaveClicked(binding));
    }

    private void updateFavoriteButton(LayoutBgBottomSheetBinding binding, boolean favorited) {
        binding.favoriteBtn.setIconResource(
                favorited ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        binding.favoriteBtn.setText(
                favorited ? R.string.favorited : R.string.favorite);
    }

    private void copyQuote(QuoteResponse quote) {
        ClipboardManager clipboard =
                (ClipboardManager) host.activity().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null || quote.getQuote() == null) return;
        String text = "\"" + quote.getQuote() + "\"\n— "
                + (quote.getCharacter() != null ? quote.getCharacter() : "")
                + (quote.getAnime() != null ? " · " + quote.getAnime() : "");
        clipboard.setPrimaryClip(ClipData.newPlainText("Cita quote", text));
        analytics.quoteCopy(quote);
        Toast.makeText(host.activity(), R.string.quote_copied, Toast.LENGTH_SHORT).show();
    }

    private void onSaveClicked(LayoutBgBottomSheetBinding binding) {
        Activity activity = host.activity();
        perm storagePerm = new perm();
        boolean canWrite = !storagePerm.needsStoragePermission()
                || activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        if (!canWrite) {
            host.requestStoragePermission();
            return;
        }
        if (credits.canSaveWithoutAd()) {
            saveAndShare(binding);
        } else {
            showConsentDialog(binding);
        }
    }

    private void showConsentDialog(LayoutBgBottomSheetBinding binding) {
        Activity activity = host.activity();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.layout_ad_consent, null);
        dialogBuilder.setTitle(R.string.save_options_title);
        dialogBuilder.setView(dialogView);

        MaterialButton showAd = dialogView.findViewById(R.id.showAd);
        MaterialButton getReward = dialogView.findViewById(R.id.getReward);

        consentDialog = dialogBuilder.create();
        consentDialog.show();

        showAd.setOnClickListener(v -> loadRewardAd(false, binding));
        getReward.setOnClickListener(v -> loadRewardAd(true, binding));
    }

    private void loadRewardAd(boolean reward, LayoutBgBottomSheetBinding binding) {
        if (consentDialog != null && consentDialog.isShowing()) {
            consentDialog.dismiss();
        }
        Toast.makeText(host.activity(), R.string.loading_ad, Toast.LENGTH_SHORT).show();

        rewardAd.loadAd(host.activity(), host.activity(), reward, new QuoteRewardAd.Listener() {
            @Override
            public void onRewardEarned(int amount) {
                // QuoteRewardAd already writes credits into SharedPreferences.
            }

            @Override
            public void onAdDismissed(boolean earnedReward) {
                if (!host.isActive()) return;
                if (reward) {
                    if (earnedReward) saveAndShare(binding);
                } else {
                    // One-shot path: grant a single credit then save (never negative).
                    credits.add(1);
                    saveAndShare(binding);
                }
            }

            @Override
            public void onAdFailed() {
                // Toast already shown by QuoteRewardAd
            }
        });
    }

    private void saveAndShare(LayoutBgBottomSheetBinding binding) {
        Activity activity = host.activity();
        QuoteResponse quote = currentQuote;
        new ViewToImage(activity, binding.toBeConverted, new ActionListeners() {
            @Override
            public void convertedWithSuccess(Bitmap bitmap, String filePath, String absolutePath) {
                if (!host.isActive()) return;
                credits.consumeOne();
                if (quote != null) {
                    analytics.quoteSave(quote);
                    analytics.quoteShare(quote);
                }
                Toast.makeText(activity, activity.getString(R.string.quote_saved, filePath),
                        Toast.LENGTH_SHORT).show();
                if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.dismiss();
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(absolutePath));
                activity.startActivity(Intent.createChooser(intent,
                        activity.getString(R.string.share_quote)));
            }

            @Override
            public void convertedWithError(String error) {
                if (!host.isActive()) return;
                Toast.makeText(activity, activity.getString(R.string.error_prefix, error),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package me.ngarak.cita.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.jetbrains.annotations.NotNull;

import me.ngarak.cita.R;
import me.ngarak.cita.adapters.QuotesRVAdapter;
import me.ngarak.cita.databinding.ActivityQuoteByAnimeBinding;
import me.ngarak.cita.ui.quotes.QuotesViewModel;

public class QuoteByAnimeActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private ActivityQuoteByAnimeBinding binding;
    private static String anime;
    private QuotesRVAdapter quotesRVAdapter;

    private InterstitialAd interstitialAd;
    AdRequest adRequest = new AdRequest.Builder().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuoteByAnimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadAd();
        loadSmartAd();

        /*get extras from fragment*/
        anime = getIntent().getStringExtra("anime");

        /*setting actionBar*/
        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(anime);

        settingUpAdapter();
        retrieveQuotesByAnime();

        binding.toolBar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadSmartAd() {
        binding.adView.loadAd(adRequest);
        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                /*dismiss view on AdLoadError*/
                binding.adView.setVisibility(View.GONE);
            }
        });
    }

    private void settingUpAdapter() {
        binding.quotesRv.setHasFixedSize(true);
        quotesRVAdapter = new QuotesRVAdapter();

        binding.quotesRv.setAdapter(quotesRVAdapter);
    }

    private void retrieveQuotesByAnime() {
        new QuotesViewModel().getQuotesByAnime(anime, 1).observe(this, quoteResponses -> {
            if (quoteResponses != null && !quoteResponses.isEmpty()) {
                Log.d(TAG, "retrieveQuotesByAnime() called" + quoteResponses.size());
                quotesRVAdapter.setQuoteList(quoteResponses);

                binding.progressBar.setVisibility(View.GONE);
                binding.quotesRv.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.about) {
            onAboutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("An android application for free quotes from anime movies and series\n" +
                "Support this development by clicking Open Ad")
                .setTitle("About Cita")
                .setPositiveButton("Open Ad", (dialog, id) -> {
                    showInterstitial();
                })
                .setNegativeButton("Dismiss", (dialog, id) -> {
                    // User cancelled the dialog
                    dialog.dismiss();
                });
        builder.create();
        builder.show();
    }

    private void loadAd() {
        InterstitialAd.load(this, getString(R.string.SUPPORT_AD_UNIT), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd mInterstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                interstitialAd = mInterstitialAd;

                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d("TAG", "The ad was dismissed.");
//                        loadAd();
                        Toast.makeText(QuoteByAnimeActivity.this, "Thank you for support", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NotNull AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        interstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i(TAG, loadAdError.getMessage());
                interstitialAd = null;
            }
        });

    }


    private void showInterstitial() {
        if (interstitialAd != null) {
            interstitialAd.show(QuoteByAnimeActivity.this);
        } else {
            Toast.makeText(this, "Failed to load Ad", Toast.LENGTH_SHORT).show();
        }
    }
}
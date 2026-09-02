package me.ngarak.cita.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import me.ngarak.cita.R;
import me.ngarak.cita.adapters.AutoScroll;
import me.ngarak.cita.adapters.QuotesRVAdapter;
import me.ngarak.cita.ads.QuoteRewardAd;
import me.ngarak.cita.ads.SupportAd;
import me.ngarak.cita.databinding.ActivityQuoteByAnimeBinding;
import me.ngarak.cita.databinding.LayoutBgBottomSheetBinding;
import me.ngarak.cita.models.QuoteResponse;
import me.ngarak.cita.perm;
import me.ngarak.cita.ui.quotes.QuotesViewModel;
import me.ngarak.layout_image.ActionListeners;
import me.ngarak.layout_image.ViewToImage;

public class QuoteByAnimeActivity extends AppCompatActivity {

    private static String anime;
    private final String TAG = getClass().getSimpleName();
    private final int currentPage = 1;
    AdRequest adRequest = new AdRequest.Builder().build();
    private ActivityQuoteByAnimeBinding binding;
    private QuotesRVAdapter quotesRVAdapter;
    private int maxPages = 200;
    private int onErrorPage;

    private QuotesViewModel quotesViewModel;
    private SharedPreferences preferences;
    private BottomSheetDialog bottomSheetDialog;
    private QuoteRewardAd quoteRewardAd;
    private AlertDialog consentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuoteByAnimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences("quote_views", Context.MODE_PRIVATE);
        quoteRewardAd = new QuoteRewardAd();
        quotesViewModel = new ViewModelProvider(this).get(QuotesViewModel.class);

        loadSmartAd();

        anime = getIntent().getStringExtra("anime");

        setSupportActionBar(binding.toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(anime);
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        settingUpAdapter();

        binding.quotesRv.addOnScrollListener(new AutoScroll(binding.quotesRv.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView recyclerView) {
                loadPage(page + 1);
            }
        });

        retrieveQuotesByAnime(currentPage);

        binding.layoutError.reloadPage.setOnClickListener(v -> {
            quotesRVAdapter.clear();
            settingUpAdapter();
            retrieveQuotesByAnime(currentPage);
            loadSmartAd();
        });

        binding.loadMoreBtn.setOnClickListener(v -> loadPage(onErrorPage));

        binding.toolBar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void loadPage(int page) {
        if (page < maxPages) {
            retrieveQuotesByAnime(page);
        } else {
            Toast.makeText(QuoteByAnimeActivity.this, R.string.end_of_quotes, Toast.LENGTH_LONG).show();
        }
    }

    private void loadSmartAd() {
        binding.adView.loadAd(adRequest);
        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.i(TAG, "banner loaded");
                binding.adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.w(TAG, "banner failed code=" + loadAdError.getCode()
                        + " msg=" + loadAdError.getMessage());
                binding.adView.setVisibility(View.GONE);
            }
        });
    }

    private void settingUpAdapter() {
        binding.quotesRv.setHasFixedSize(true);
        quotesRVAdapter = new QuotesRVAdapter(this::openBottomSheet);

        binding.quotesRv.setAdapter(quotesRVAdapter);
    }

    private void openBottomSheet(QuoteResponse quoteResponse) {
        bottomSheetDialog = new BottomSheetDialog(this);
        LayoutBgBottomSheetBinding bg_binding = LayoutBgBottomSheetBinding.inflate(LayoutInflater.from(this));
        bottomSheetDialog.setContentView(bg_binding.getRoot());

        bg_binding.setQuote(quoteResponse);
        bottomSheetDialog.show();

        AdRequest adRequest = new AdRequest.Builder().build();
        bg_binding.adView.loadAd(adRequest);
        bg_binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                loadAdError.getResponseInfo();
            }
        });

        bg_binding.saveQuoteBtn.setOnClickListener(v -> {
            perm storagePerm = new perm();
            boolean canWrite = !storagePerm.needsStoragePermission()
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            if (canWrite) {
                Log.e(TAG, "REWARD: " + preferences.getInt("quote_views", 0));
                if (preferences.getInt("quote_views", 0) > 0) {
                    saveLayout(bg_binding);
                } else {
                    showDialog(bg_binding);
                }
            } else {
                storagePerm.reQuestStorage(QuoteByAnimeActivity.this);
            }
        });
    }

    private void showDialog(LayoutBgBottomSheetBinding bg_binding) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_ad_consent, null);
        dialogBuilder.setTitle(R.string.save_options_title);
        dialogBuilder.setView(dialogView);

        MaterialButton showAd = dialogView.findViewById(R.id.showAd);
        MaterialButton getReward = dialogView.findViewById(R.id.getReward);

        consentDialog = dialogBuilder.create();
        consentDialog.show();

        showAd.setOnClickListener(v -> showAdFirst(false, bg_binding));
        getReward.setOnClickListener(v -> showAdFirst(true, bg_binding));
    }

    private void showAdFirst(boolean reward, LayoutBgBottomSheetBinding bg_binding) {
        if (consentDialog != null && consentDialog.isShowing()) {
            consentDialog.dismiss();
        }

        Toast.makeText(this, R.string.loading_ad, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Show Ad");

        quoteRewardAd.loadAd(this, QuoteByAnimeActivity.this, reward, new QuoteRewardAd.Listener() {
            @Override
            public void onRewardEarned(int amount) {
                // Prefs updated in QuoteRewardAd; save after dismiss
            }

            @Override
            public void onAdDismissed(boolean earnedReward) {
                if (isFinishing()) {
                    return;
                }
                if (reward) {
                    if (earnedReward) {
                        saveLayout(bg_binding);
                    }
                } else {
                    saveLayout(bg_binding);
                }
            }

            @Override
            public void onAdFailed() {
                // Toast already shown by QuoteRewardAd
            }
        });
    }

    private void saveLayout(LayoutBgBottomSheetBinding bg_binding) {
        new ViewToImage(this, bg_binding.toBeConverted, new ActionListeners() {
            @Override
            public void convertedWithSuccess(Bitmap bitmap, String filePath, String absolutePath) {
                Toast.makeText(QuoteByAnimeActivity.this, getString(R.string.quote_saved, filePath), Toast.LENGTH_SHORT).show();

                preferences.edit().putInt("quote_views", preferences.getInt("quote_views", 0) - 1).apply();
                if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.dismiss();
                }

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(absolutePath));
                startActivity(Intent.createChooser(intent, "Share Quote"));
            }

            @Override
            public void convertedWithError(String error) {
                Toast.makeText(QuoteByAnimeActivity.this, getString(R.string.error_prefix, error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrieveQuotesByAnime(int page) {
        Log.d(TAG, "retrieveQuotesByAnime() called with: page = [" + page + "]");

        if (page == 1) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.layoutError.getRoot().setVisibility(View.GONE);
            binding.layoutNoQuotes.getRoot().setVisibility(View.GONE);

            quotesViewModel.getQuotesByAnime(anime, page).observe(this, quoteResponses -> {

                boolean isErrorCode = false, isThrowable = false;

                if (quoteResponses != null) {
                    for (QuoteResponse quoteResponse : quoteResponses) {
                        if (quoteResponse.getError_code() >= 300) {
                            Log.e(TAG, "randomQuotes: Error");
                            isErrorCode = true;
                        }

                        if (quoteResponse.getThrowable() != null) {
                            Log.e(TAG, "randomQuotes: Throwable");
                            isThrowable = true;
                        }
                    }
                }

                if (isErrorCode || isThrowable) {
                    binding.layoutError.getRoot().setVisibility(View.VISIBLE);
                    binding.layoutNoQuotes.getRoot().setVisibility(View.GONE);
                    binding.quotesRv.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                } else if (quoteResponses == null || quoteResponses.isEmpty()) {
                    binding.layoutNoQuotes.getRoot().setVisibility(View.VISIBLE);
                    binding.layoutError.getRoot().setVisibility(View.GONE);
                    binding.quotesRv.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                } else {
                    Log.d(TAG, "randomQuotes() returned: " + quoteResponses.size());
                    quotesRVAdapter.setQuoteList(quoteResponses);

                    if (quoteResponses.size() < 10) {
                        maxPages = page;
                    }

                    binding.progressBar.setVisibility(View.GONE);
                    binding.quotesRv.setVisibility(View.VISIBLE);
                    binding.layoutError.getRoot().setVisibility(View.GONE);
                    binding.layoutNoQuotes.getRoot().setVisibility(View.GONE);
                }
            });
        } else {
            binding.progressLoadMore.setVisibility(View.VISIBLE);

            quotesViewModel.getQuotesByAnime(anime, page).observe(this, quoteResponses -> {

                boolean isErrorCode = false, isThrowable = false;

                if (quoteResponses != null) {
                    for (QuoteResponse quoteResponse : quoteResponses) {
                        if (quoteResponse.getError_code() >= 300) {
                            Log.e(TAG, "randomQuotes: Error");
                            isErrorCode = true;
                            onErrorPage = page;
                        }

                        if (quoteResponse.getThrowable() != null) {
                            Log.e(TAG, "randomQuotes: Throwable");
                            isThrowable = true;
                            onErrorPage = page;
                        }
                    }
                }

                if (isErrorCode || isThrowable || quoteResponses == null) {
                    binding.loadMoreBtn.setVisibility(View.VISIBLE);
                    binding.progressLoadMore.setVisibility(View.GONE);
                } else {
                    quotesRVAdapter.setQuoteList(quoteResponses);

                    if (quoteResponses.size() < 10) {
                        maxPages = page;
                    }
                    binding.loadMoreBtn.setVisibility(View.GONE);
                    binding.progressLoadMore.setVisibility(View.GONE);
                }
            });
        }
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
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_about, null);
        dialogBuilder.setTitle(R.string.about_title);
        dialogBuilder.setView(dialogView);

        MaterialButton showAdd = dialogView.findViewById(R.id.showAd);
        TextView cita = dialogView.findViewById(R.id.cita);

        AlertDialog alertDialog = dialogBuilder.create();
        showAdd.setOnClickListener(v -> {
            alertDialog.dismiss();
            attemptToShowAd();
        });
        cita.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse("https://github.com/Ngara-K/Cita"))));
        alertDialog.show();
    }

    private void attemptToShowAd() {
        Toast.makeText(this, R.string.loading_ad, Toast.LENGTH_SHORT).show();
        new SupportAd().loadAd(this, this, null);
    }
}

package me.ngarak.cita.ui;

import android.app.Activity;
import android.content.Intent;
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
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import me.ngarak.cita.QuoteSheetController;
import me.ngarak.cita.R;
import me.ngarak.cita.adapters.AutoScroll;
import me.ngarak.cita.adapters.QuotesRVAdapter;
import me.ngarak.cita.ads.SupportAd;
import me.ngarak.cita.databinding.ActivityQuoteByAnimeBinding;
import me.ngarak.cita.models.QuoteResponse;
import me.ngarak.cita.perm;
import me.ngarak.cita.ui.quotes.QuotesViewModel;

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
    private QuoteSheetController sheetController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuoteByAnimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        quotesViewModel = new ViewModelProvider(this).get(QuotesViewModel.class);
        sheetController = new QuoteSheetController(new QuoteSheetController.Host() {
            @NonNull
            @Override
            public Activity activity() {
                return QuoteByAnimeActivity.this;
            }

            @Override
            public boolean isActive() {
                return !isFinishing();
            }

            @Override
            public void requestStoragePermission() {
                new perm().reQuestStorage(QuoteByAnimeActivity.this);
            }
        });

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
                binding.adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                Log.w(TAG, "banner failed code=" + loadAdError.getCode()
                        + " msg=" + loadAdError.getMessage());
                binding.adView.setVisibility(View.GONE);
            }
        });
    }

    private void settingUpAdapter() {
        binding.quotesRv.setHasFixedSize(true);
        quotesRVAdapter = new QuotesRVAdapter(quote -> sheetController.open(quote));
        binding.quotesRv.setAdapter(quotesRVAdapter);
    }

    private void retrieveQuotesByAnime(int page) {
        if (page == 1) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.layoutError.getRoot().setVisibility(View.GONE);
            binding.layoutNoQuotes.getRoot().setVisibility(View.GONE);

            quotesViewModel.getQuotesByAnime(anime, page).observe(this, quoteResponses -> {
                binding.progressBar.setVisibility(View.GONE);
                boolean isErrorCode = false, isThrowable = false;
                if (quoteResponses != null) {
                    for (QuoteResponse quoteResponse : quoteResponses) {
                        if (quoteResponse.getError_code() >= 300) isErrorCode = true;
                        if (quoteResponse.getThrowable() != null) isThrowable = true;
                    }
                }
                if (isErrorCode || isThrowable) {
                    binding.layoutError.getRoot().setVisibility(View.VISIBLE);
                    binding.quotesRv.setVisibility(View.INVISIBLE);
                } else if (quoteResponses == null || quoteResponses.isEmpty()) {
                    binding.layoutNoQuotes.getRoot().setVisibility(View.VISIBLE);
                    binding.quotesRv.setVisibility(View.INVISIBLE);
                } else {
                    quotesRVAdapter.setQuoteList(quoteResponses);
                    if (quoteResponses.size() < 10) maxPages = page;
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
                            isErrorCode = true;
                            onErrorPage = page;
                        }
                        if (quoteResponse.getThrowable() != null) {
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
                    if (quoteResponses.size() < 10) maxPages = page;
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

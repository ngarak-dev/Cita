package me.ngarak.cita.ui;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import me.ngarak.cita.ads.SupportAd;
import me.ngarak.cita.databinding.ActivityQuoteByAnimeBinding;
import me.ngarak.cita.databinding.LayoutBottomSheetBinding;
import me.ngarak.cita.models.QuoteResponse;
import me.ngarak.cita.ui.quotes.QuotesViewModel;

public class QuoteByAnimeActivity extends AppCompatActivity {

    private static String anime;
    private final String TAG = getClass().getSimpleName();
    private final int currentPage = 1;
    RecyclerView quotesRecyclerView;
    AdRequest adRequest = new AdRequest.Builder().build();
    private ActivityQuoteByAnimeBinding binding;
    private QuotesRVAdapter quotesRVAdapter;
    private int maxPages = 200;
    private int onErrorPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuoteByAnimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        quotesRecyclerView = findViewById(R.id.random_rv);

        loadSmartAd();

        /*get extras from fragment*/
        anime = getIntent().getStringExtra("anime");

        /*setting actionBar*/
        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(anime);

        settingUpAdapter();

        binding.quotesRv.addOnScrollListener(new AutoScroll(binding.quotesRv.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView recyclerView) {
                quotesRecyclerView = recyclerView;
                loadPage(page + 1);
            }
        });

        retrieveQuotesByAnime(currentPage);

        binding.layoutError.reloadPage.setOnClickListener(v -> {
            if (quotesRVAdapter.getQuoteList() != null) {
                quotesRVAdapter.getQuoteList().clear();
                quotesRVAdapter.notifyDataSetChanged();
            }
            settingUpAdapter();
            retrieveQuotesByAnime(currentPage);
            loadSmartAd();
        });

        binding.loadMoreBtn.setOnClickListener(v -> loadPage(onErrorPage));

        binding.toolBar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadPage(int page) {
        if (page < maxPages) {
            retrieveQuotesByAnime(page);
        } else {
            /*End of Pages*/
            Toast.makeText(QuoteByAnimeActivity.this, "End of Quotes", Toast.LENGTH_LONG).show();
        }
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
        quotesRVAdapter = new QuotesRVAdapter(this::openBottomSheet);

        binding.quotesRv.setAdapter(quotesRVAdapter);
    }

    private void openBottomSheet(QuoteResponse quoteResponse) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        LayoutBottomSheetBinding binding = LayoutBottomSheetBinding.inflate(LayoutInflater.from(this));
        bottomSheetDialog.setContentView(binding.getRoot());

        binding.setQuote(quoteResponse);
        bottomSheetDialog.show();

        /*show ad*/
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);
        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                /*dismiss view on AdLoadError*/
                loadAdError.getResponseInfo();
                binding.adView.setVisibility(View.GONE);
            }
        });

        binding.copyBtn.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(getString(R.string.app_name), quoteResponse.getQuote());
            clipboard.setPrimaryClip(clip);

            binding.copyBtn.setIconTint(ColorStateList.valueOf(Color.GREEN));
            Toast.makeText(this, "Quote Copied", Toast.LENGTH_SHORT).show();
        });
    }

    private void retrieveQuotesByAnime(int page) {
        Log.d(TAG, "retrieveQuotesByAnime() called with: page = [" + page + "]");

        if (page == 1) {
            new QuotesViewModel().getQuotesByAnime(anime, page).observe(this, quoteResponses -> {

                boolean isErrorCode = false, isThrowable = false;

                for (QuoteResponse quoteResponse : quoteResponses) {
                    if (quoteResponse.getError_code() >= 300) {
                        Log.e(TAG, "randomQuotes: Error" );
                        isErrorCode = true;
                    }

                    if (quoteResponse.getThrowable() != null) {
                        Log.e(TAG, "randomQuotes: Throwable" );
                        isThrowable = true;
                    }
                }

                if (isErrorCode) {
                    //return Error
                    binding.layoutError.getRoot().setVisibility(View.VISIBLE);
                    binding.quotesRv.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.INVISIBLE);
                }
                else if (isThrowable) {
                    //return throwable
                    binding.layoutError.getRoot().setVisibility(View.VISIBLE);
                    binding.quotesRv.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.INVISIBLE);
                }
                else if (quoteResponses == null) {
                    binding.layoutNoQuotes.getRoot().setVisibility(View.VISIBLE);
                    binding.quotesRv.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.INVISIBLE);
                }
                else {
                    //return quotes
                    Log.d(TAG, "randomQuotes() returned: " + quoteResponses.size());
                    quotesRVAdapter.setQuoteList(quoteResponses);

                    /*checking list size then set last page*/
                    if (quoteResponses.size() < 10) {
                        maxPages = page;
                    }

                    binding.progressBar.setVisibility(View.GONE);
                    binding.quotesRv.setVisibility(View.VISIBLE);

                    binding.layoutError.getRoot().setVisibility(View.GONE);
                    binding.layoutNoQuotes.getRoot().setVisibility(View.GONE);
                }
            });
        }
        else {
            binding.progressLoadMore.setVisibility(View.VISIBLE);

            new QuotesViewModel().getQuotesByAnime(anime, page).observe(this, quoteResponses -> {

                boolean isErrorCode = false, isThrowable = false;

                for (QuoteResponse quoteResponse : quoteResponses) {
                    if (quoteResponse.getError_code() >= 300) {
                        Log.e(TAG, "randomQuotes: Error" );
                        isErrorCode = true;
                        onErrorPage = page;
                    }

                    if (quoteResponse.getThrowable() != null) {
                        Log.e(TAG, "randomQuotes: Throwable" );
                        isThrowable = true;
                        onErrorPage = page;
                    }
                }

                if (isErrorCode) {
                    //return Error
                    binding.loadMoreBtn.setVisibility(View.VISIBLE);
                    binding.progressLoadMore.setVisibility(View.GONE);
                }
                else if (isThrowable) {
                    //return throwable
                    binding.loadMoreBtn.setVisibility(View.VISIBLE);
                    binding.progressLoadMore.setVisibility(View.GONE);
                }
                else if (quoteResponses == null) {
                    //null list
                    binding.loadMoreBtn.setVisibility(View.VISIBLE);
                    binding.progressLoadMore.setVisibility(View.GONE);
                }
                else {
                    //return quotes
                    quotesRVAdapter.setQuoteList(quoteResponses);

                    /*checking list size then set last page*/
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
        dialogBuilder.setView(dialogView);

        MaterialButton showAdd = dialogView.findViewById(R.id.showAd);
        TextView animechan = dialogView.findViewById(R.id.animechan);
        TextView cita = dialogView.findViewById(R.id.cita);
        showAdd.setOnClickListener(v -> attemptToShowAd ());

        animechan.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://github.com/rocktimsaikia/anime-chan"))));
        cita.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://github.com/Ngara-K/Cita"))));

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void attemptToShowAd() {
        ProgressDialog progressDialog = new ProgressDialog(QuoteByAnimeActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        /*loadAd*/
        new SupportAd().loadAd(this, progressDialog, QuoteByAnimeActivity.this);
    }
}
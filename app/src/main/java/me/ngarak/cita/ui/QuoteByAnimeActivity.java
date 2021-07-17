package me.ngarak.cita.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import me.ngarak.cita.R;
import me.ngarak.cita.adapters.QuotesRVAdapter;
import me.ngarak.cita.databinding.ActivityQuoteByAnimeBinding;
import me.ngarak.cita.ui.quotes.QuotesViewModel;

public class QuoteByAnimeActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private ActivityQuoteByAnimeBinding binding;
    private static String anime;
    private QuotesRVAdapter quotesRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuoteByAnimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
}